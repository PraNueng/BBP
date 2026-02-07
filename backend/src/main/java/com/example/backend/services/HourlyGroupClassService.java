package com.example.backend.services;

import com.example.backend.dtos.hourly.CreateHourlyGroupClassRequestDto;
import com.example.backend.dtos.hourly.HourlyGroupClassDto;
import com.example.backend.dtos.hourly.UpdateHourlyGroupClassRequestDto;
import com.example.backend.dtos.tutor.AssignTutorsRequestDto;
import com.example.backend.dtos.tutor.RemoveTutorsRequestDto;
import com.example.backend.entities.Grade;
import com.example.backend.entities.Subject;
import com.example.backend.entities.User;
import com.example.backend.entities.hourly.HourlyGroupClass;
import com.example.backend.entities.hourly.HourlyGroupClassTutor;
import com.example.backend.entities.hourly.HourlyGroupSubtype;
import com.example.backend.entities.hourly.HourlyGroupEnrollment;
import com.example.backend.entities.student.Student;
import com.example.backend.repositories.GradeRepository;
import com.example.backend.repositories.SubjectRepository;
import com.example.backend.repositories.UserRepository;
import com.example.backend.repositories.hourly.HourlyGroupClassRepository;
import com.example.backend.repositories.hourly.HourlyGroupClassTutorRepository;
import com.example.backend.repositories.hourly.HourlyGroupSubtypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class HourlyGroupClassService {

    @Autowired
    private HourlyGroupClassRepository classRepository;

    @Autowired
    private HourlyGroupClassTutorRepository classTutorRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private HourlyGroupSubtypeRepository subtypeRepository;

    @Autowired
    private GradeRepository gradeRepository;

    // ============================================
    // UPDATED: ใช้ findAllWithTutors แทน findAll
    // ============================================

    @Transactional(readOnly = true)
    public List<HourlyGroupClassDto> getAllClasses() {
        return classRepository.findAllWithTutors().stream()
                .map(HourlyGroupClassDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<HourlyGroupClassDto> getActiveClasses() {
        return classRepository.findAllActiveWithTutors().stream()
                .map(HourlyGroupClassDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public HourlyGroupClassDto getClassById(Long id) {
        HourlyGroupClass classEntity = classRepository.findByIdWithTutors(id)
                .orElseThrow(() -> new RuntimeException("Class not found with id: " + id));
        return HourlyGroupClassDto.fromEntityWithEnrollments(classEntity);
    }

    // ============================================
    // EXISTING METHODS (Keep as is)
    // ============================================

    @Transactional(readOnly = true)
    public List<HourlyGroupClassDto> getClassesByTutor(Long tutorId, Boolean active) {
        if (tutorId == null) {
            List<HourlyGroupClass> classes = classRepository.findAll().stream()
                    .filter(c -> c.getClassTutors().isEmpty())
                    .filter(c -> active == null || c.getIsActive().equals(active))
                    .collect(Collectors.toList());

            return classes.stream()
                    .map(HourlyGroupClassDto::fromEntity)
                    .collect(Collectors.toList());
        }

        List<HourlyGroupClassTutor> classTutors = classTutorRepository.findByTutorId(tutorId);

        return classTutors.stream()
                .map(HourlyGroupClassTutor::getHourlyGroupClass)
                .filter(c -> active == null || c.getIsActive().equals(active))
                .map(HourlyGroupClassDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<HourlyGroupClassDto> getClassesBySubject(Long subjectId, Boolean active) {
        List<HourlyGroupClass> classes;

        if (active != null && active) {
            classes = classRepository.findBySubjectIdAndActive(subjectId);
        } else {
            classes = classRepository.findAll().stream()
                    .filter(c -> c.getSubject().getId().equals(subjectId))
                    .collect(Collectors.toList());
        }

        return classes.stream()
                .map(HourlyGroupClassDto::fromEntity)
                .collect(Collectors.toList());
    }

    public HourlyGroupClassDto createClass(CreateHourlyGroupClassRequestDto request) {
        if (request.getTutorId() != null) {
            throw new RuntimeException("Cannot assign tutor during class creation. Use assignTutors endpoint instead.");
        }

        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new RuntimeException("Subject not found with id: " + request.getSubjectId()));

        HourlyGroupSubtype subtype = subtypeRepository.findById(request.getSubtypeId())
                .orElseThrow(() -> new RuntimeException("Subtype not found with id: " + request.getSubtypeId()));

        Grade grade = null;
        if (request.getGradeId() != null) {
            grade = gradeRepository.findById(request.getGradeId())
                    .orElseThrow(() -> new RuntimeException("Grade not found with id: " + request.getGradeId()));
        }

        User currentUser = getCurrentUser();

        HourlyGroupClass newClass = new HourlyGroupClass();
        newClass.setSubject(subject);
        newClass.setGrade(grade);
        newClass.setSubtype(subtype);
        newClass.setClassName(request.getClassName());
        newClass.setIsActive(true);
        newClass.setCreatedBy(currentUser);

        HourlyGroupClass savedClass = classRepository.save(newClass);
        return HourlyGroupClassDto.fromEntity(savedClass);
    }

    public HourlyGroupClassDto updateClass(Long id, UpdateHourlyGroupClassRequestDto request) {
        HourlyGroupClass existingClass = classRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Class not found with id: " + id));

        if (request.getSubjectId() != null) {
            Subject subject = subjectRepository.findById(request.getSubjectId())
                    .orElseThrow(() -> new RuntimeException("Subject not found with id: " + request.getSubjectId()));
            existingClass.setSubject(subject);
        }

        if (request.getSubtypeId() != null) {
            HourlyGroupSubtype subtype = subtypeRepository.findById(request.getSubtypeId())
                    .orElseThrow(() -> new RuntimeException("Subtype not found with id: " + request.getSubtypeId()));
            existingClass.setSubtype(subtype);
        }

        if (request.getGradeId() != null) {
            Grade grade = gradeRepository.findById(request.getGradeId())
                    .orElseThrow(() -> new RuntimeException("Grade not found"));
            existingClass.setGrade(grade);
        }

        if (request.getClassName() != null) {
            existingClass.setClassName(request.getClassName());
        }

        if (request.getIsActive() != null) {
            existingClass.setIsActive(request.getIsActive());
        }

        HourlyGroupClass updatedClass = classRepository.save(existingClass);
        return HourlyGroupClassDto.fromEntity(updatedClass);
    }

    public HourlyGroupClassDto toggleActive(Long id) {
        HourlyGroupClass classEntity = classRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Class not found with id: " + id));

        classEntity.setIsActive(!classEntity.getIsActive());

        HourlyGroupClass updatedClass = classRepository.save(classEntity);
        return HourlyGroupClassDto.fromEntity(updatedClass);
    }

    public void deleteClass(Long id) {
        HourlyGroupClass classEntity = classRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Class not found with id: " + id));

        classEntity.setIsActive(false);
        classRepository.save(classEntity);
    }

    @Transactional(readOnly = true)
    public List<HourlyGroupClassDto> searchClasses(Long tutorId, Long subjectId, Long subtypeId) {
        return classRepository.findByMultipleCriteria(tutorId, subjectId, subtypeId).stream()
                .map(HourlyGroupClassDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public long countActiveClasses() {
        return classRepository.countByIsActiveTrue();
    }

    @Transactional(readOnly = true)
    public long countClassesByTutor(Long tutorId) {
        return classRepository.countByTutorIdAndActive(tutorId);
    }

    // ============================================
    // TUTOR MANAGEMENT - Many-to-Many
    // ============================================

    public HourlyGroupClassDto assignTutors(Long classId, AssignTutorsRequestDto request) {
        HourlyGroupClass classEntity = classRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Class not found with id: " + classId));

        User currentUser = getCurrentUser();

        for (Long tutorId : request.getTutorIds()) {
            User tutor = userRepository.findById(tutorId)
                    .orElseThrow(() -> new RuntimeException("Tutor not found with id: " + tutorId));

            if (classTutorRepository.existsByClassIdAndTutorId(classId, tutorId)) {
                throw new RuntimeException("Tutor " + tutor.getNickname() + " is already assigned to this class");
            }

            HourlyGroupClassTutor classTutor = new HourlyGroupClassTutor(classEntity, tutor, currentUser);
            classTutorRepository.save(classTutor);
        }

        // Refresh ด้วย findByIdWithTutors
        classEntity = classRepository.findByIdWithTutors(classId).orElseThrow();
        return HourlyGroupClassDto.fromEntity(classEntity);
    }

    public HourlyGroupClassDto assignTutor(Long classId, Long tutorId) {
        HourlyGroupClass classEntity = classRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Class not found"));

        User tutor = userRepository.findById(tutorId)
                .orElseThrow(() -> new RuntimeException("Tutor not found"));

        if (classTutorRepository.existsByClassIdAndTutorId(classId, tutorId)) {
            throw new RuntimeException("Tutor already assigned to this class");
        }

        User currentUser = getCurrentUser();
        HourlyGroupClassTutor classTutor = new HourlyGroupClassTutor(classEntity, tutor, currentUser);
        classTutorRepository.save(classTutor);

        classEntity = classRepository.findById(classId).orElseThrow();
        return HourlyGroupClassDto.fromEntity(classEntity);
    }

    public HourlyGroupClassDto removeTutors(Long classId, RemoveTutorsRequestDto request) {
        HourlyGroupClass classEntity = classRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Class not found"));

        for (Long tutorId : request.getTutorIds()) {
            User tutor = userRepository.findById(tutorId)
                    .orElseThrow(() -> new RuntimeException("Tutor not found with id: " + tutorId));

            if (!classTutorRepository.existsByClassIdAndTutorId(classId, tutorId)) {
                throw new RuntimeException("Tutor " + tutor.getNickname() + " is not assigned to this class");
            }

            HourlyGroupClassTutor classTutor = classTutorRepository
                    .findByHourlyGroupClassAndTutor(classEntity, tutor)
                    .orElseThrow();

            classTutorRepository.delete(classTutor);
        }

        // Refresh ด้วย findByIdWithTutors
        classEntity = classRepository.findByIdWithTutors(classId).orElseThrow();
        return HourlyGroupClassDto.fromEntity(classEntity);
    }

    public HourlyGroupClassDto removeTutor(Long classId, Long tutorId) {
        HourlyGroupClass classEntity = classRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Class not found"));

        User tutor = userRepository.findById(tutorId)
                .orElseThrow(() -> new RuntimeException("Tutor not found"));

        HourlyGroupClassTutor classTutor = classTutorRepository
                .findByHourlyGroupClassAndTutor(classEntity, tutor)
                .orElseThrow(() -> new RuntimeException("Tutor is not assigned to this class"));

        classTutorRepository.delete(classTutor);

        classEntity = classRepository.findById(classId).orElseThrow();
        return HourlyGroupClassDto.fromEntity(classEntity);
    }

    public HourlyGroupClassDto replaceTutors(Long classId, AssignTutorsRequestDto request) {
        HourlyGroupClass classEntity = classRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Class not found"));

        classTutorRepository.deleteByHourlyGroupClass(classEntity);

        User currentUser = getCurrentUser();
        for (Long tutorId : request.getTutorIds()) {
            User tutor = userRepository.findById(tutorId)
                    .orElseThrow(() -> new RuntimeException("Tutor not found with id: " + tutorId));

            HourlyGroupClassTutor classTutor = new HourlyGroupClassTutor(classEntity, tutor, currentUser);
            classTutorRepository.save(classTutor);
        }

        // Refresh ด้วย findByIdWithTutors
        classEntity = classRepository.findByIdWithTutors(classId).orElseThrow();
        return HourlyGroupClassDto.fromEntity(classEntity);
    }

    @Transactional(readOnly = true)
    public List<User> getClassTutors(Long classId) {
        List<HourlyGroupClassTutor> classTutors = classTutorRepository.findByClassId(classId);
        return classTutors.stream()
                .map(HourlyGroupClassTutor::getTutor)
                .collect(Collectors.toList());
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Current user not found"));
    }

    /**
     * ดึงรายชื่อนักเรียนทั้งหมดในคลาส
     */
    @Transactional(readOnly = true)
    public List<Student> getClassStudents(Long classId) {
        HourlyGroupClass classEntity = classRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Class not found with id: " + classId));

        return classEntity.getEnrollments().stream()
                .filter(enrollment -> enrollment.getIsActive())
                .map(HourlyGroupEnrollment::getStudent)
                .collect(Collectors.toList());
    }
}