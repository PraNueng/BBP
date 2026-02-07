package com.example.backend.services;

import com.example.backend.dtos.monthly.CreateMonthlyClassRequestDto;
import com.example.backend.dtos.monthly.MonthlyClassDto;
import com.example.backend.dtos.monthly.UpdateMonthlyClassRequestDto;
import com.example.backend.dtos.tutor.AssignTutorsRequestDto;
import com.example.backend.dtos.tutor.RemoveTutorsRequestDto;
import com.example.backend.entities.Grade;
import com.example.backend.entities.Subject;
import com.example.backend.entities.User;
import com.example.backend.entities.monthly.MonthlyClass;
import com.example.backend.entities.monthly.MonthlyClassTutor;
import com.example.backend.entities.monthly.MonthlyEnrollment;
import com.example.backend.entities.monthly.MonthlySubtype;
import com.example.backend.entities.student.Student;
import com.example.backend.repositories.GradeRepository;
import com.example.backend.repositories.SubjectRepository;
import com.example.backend.repositories.UserRepository;
import com.example.backend.repositories.monthly.MonthlyClassRepository;
import com.example.backend.repositories.monthly.MonthlyClassTutorRepository;
import com.example.backend.repositories.monthly.MonthlySubtypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MonthlyClassService {

    @Autowired
    private MonthlyClassRepository classRepository;

    @Autowired
    private MonthlyClassTutorRepository classTutorRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private MonthlySubtypeRepository subtypeRepository;

    @Autowired
    private GradeRepository gradeRepository;

    /**
     * ดึงรายการคลาสทั้งหมด
     */
    @Transactional(readOnly = true)
    public List<MonthlyClassDto> getAllClasses() {
        // ใช้ findAllWithTutors
        return classRepository.findAllWithTutors().stream()
                .map(MonthlyClassDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * ดึงรายการคลาสที่ active
     */
    @Transactional(readOnly = true)
    public List<MonthlyClassDto> getActiveClasses() {
        // ใช้ findAllActiveWithTutors
        return classRepository.findAllActiveWithTutors().stream()
                .map(MonthlyClassDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * ดึงข้อมูลคลาสตาม ID
     */
    @Transactional(readOnly = true)
    public MonthlyClassDto getClassById(Long id) {
        // ใช้ findByIdWithTutors
        MonthlyClass classEntity = classRepository.findByIdWithTutors(id)
                .orElseThrow(() -> new RuntimeException("Class not found with id: " + id));
        return MonthlyClassDto.fromEntityWithEnrollments(classEntity);
    }

    /**
     * ดึงคลาสทั้งหมดของ tutor
     */
    @Transactional(readOnly = true)
    public List<MonthlyClassDto> getClassesByTutor(Long tutorId, Boolean active) {
        if (tutorId == null) {
            // ดึงคลาสที่ยังไม่มีครู
            List<MonthlyClass> classes = classRepository.findAll().stream()
                    .filter(c -> c.getClassTutors().isEmpty())
                    .filter(c -> active == null || c.getIsActive().equals(active))
                    .collect(Collectors.toList());

            return classes.stream()
                    .map(MonthlyClassDto::fromEntity)
                    .collect(Collectors.toList());
        }

        // ดึงคลาสของครูคนนั้น
        List<MonthlyClassTutor> classTutors = classTutorRepository.findByTutorId(tutorId);

        return classTutors.stream()
                .map(MonthlyClassTutor::getMonthlyClass)
                .filter(c -> active == null || c.getIsActive().equals(active))
                .map(MonthlyClassDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * สร้างคลาสใหม่ (ไม่มีครู)
     */
    public MonthlyClassDto createClass(CreateMonthlyClassRequestDto request) {
        if (request.getTutorId() != null) {
            throw new RuntimeException("Cannot assign tutor during class creation. Use assignTutors endpoint instead.");
        }

        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new RuntimeException("Subject not found"));

        MonthlySubtype subtype = subtypeRepository.findById(request.getSubtypeId())
                .orElseThrow(() -> new RuntimeException("Subtype not found"));

        Grade grade = null;
        if (request.getGradeId() != null) {
            grade = gradeRepository.findById(request.getGradeId())
                    .orElseThrow(() -> new RuntimeException("Grade not found"));
        }

        User currentUser = getCurrentUser();

        MonthlyClass newClass = new MonthlyClass();
        newClass.setSubject(subject);
        newClass.setGrade(grade);
        newClass.setSubtype(subtype);
        newClass.setClassName(request.getClassName());
        newClass.setStartDate(request.getStartDate());
        newClass.setEndDate(request.getEndDate());
        newClass.setIsActive(true);
        newClass.setCreatedBy(currentUser);

        MonthlyClass savedClass = classRepository.save(newClass);
        return MonthlyClassDto.fromEntity(savedClass);
    }

    /**
     * ดึงคลาสทั้งหมดตามวิชา
     */
    @Transactional(readOnly = true)
    public List<MonthlyClassDto> getClassesBySubject(Long subjectId, Boolean active) {
        List<MonthlyClass> classes;

        if (active != null && active) {
            classes = classRepository.findBySubjectIdAndActive(subjectId);
        } else {
            classes = classRepository.findAll().stream()
                    .filter(c -> c.getSubject().getId().equals(subjectId))
                    .collect(Collectors.toList());
        }

        return classes.stream()
                .map(MonthlyClassDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * ดึงคลาสที่กำลังดำเนินการอยู่
     */
    @Transactional(readOnly = true)
    public List<MonthlyClassDto> getCurrentClasses() {
        LocalDate today = LocalDate.now();
        return classRepository.findActiveOnDate(today).stream()
                .map(MonthlyClassDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * ดึงคลาสที่จะเริ่มในอนาคต
     */
    @Transactional(readOnly = true)
    public List<MonthlyClassDto> getUpcomingClasses() {
        LocalDate today = LocalDate.now();
        return classRepository.findUpcomingClasses(today).stream()
                .map(MonthlyClassDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * ดึงคลาสที่สิ้นสุดแล้ว
     */
    @Transactional(readOnly = true)
    public List<MonthlyClassDto> getPastClasses() {
        LocalDate today = LocalDate.now();
        return classRepository.findPastClasses(today).stream()
                .map(MonthlyClassDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * เพิ่มครูหลายคนให้กับคลาส
     */
    public MonthlyClassDto assignTutors(Long classId, AssignTutorsRequestDto request) {
        MonthlyClass classEntity = classRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Class not found with id: " + classId));

        User currentUser = getCurrentUser();

        for (Long tutorId : request.getTutorIds()) {
            User tutor = userRepository.findById(tutorId)
                    .orElseThrow(() -> new RuntimeException("Tutor not found with id: " + tutorId));

            if (classTutorRepository.existsByClassIdAndTutorId(classId, tutorId)) {
                throw new RuntimeException("Tutor " + tutor.getNickname() + " is already assigned to this class");
            }

            MonthlyClassTutor classTutor = new MonthlyClassTutor(classEntity, tutor, currentUser);
            classTutorRepository.save(classTutor);
        }

        // Refresh ด้วย findByIdWithTutors
        classEntity = classRepository.findByIdWithTutors(classId).orElseThrow();
        return MonthlyClassDto.fromEntity(classEntity);
    }

    /**
     *เพิ่มครู 1 คน (สำหรับ backward compatibility)
     */
    public MonthlyClassDto assignTutor(Long classId, Long tutorId) {
        MonthlyClass classEntity = classRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Class not found"));

        User tutor = userRepository.findById(tutorId)
                .orElseThrow(() -> new RuntimeException("Tutor not found"));

        if (classTutorRepository.existsByClassIdAndTutorId(classId, tutorId)) {
            throw new RuntimeException("Tutor already assigned to this class");
        }

        User currentUser = getCurrentUser();
        MonthlyClassTutor classTutor = new MonthlyClassTutor(classEntity, tutor, currentUser);
        classTutorRepository.save(classTutor);

        classEntity = classRepository.findById(classId).orElseThrow();
        return MonthlyClassDto.fromEntity(classEntity);
    }

    public MonthlyClassDto updateClass(Long id, UpdateMonthlyClassRequestDto request) {
        MonthlyClass existingClass = classRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Class not found"));

        if (request.getSubjectId() != null) {
            Subject subject = subjectRepository.findById(request.getSubjectId())
                    .orElseThrow(() -> new RuntimeException("Subject not found"));
            existingClass.setSubject(subject);
        }

        if (request.getGradeId() != null) {
            Grade grade = gradeRepository.findById(request.getGradeId())
                    .orElseThrow(() -> new RuntimeException("Grade not found"));
            existingClass.setGrade(grade);
        }

        if (request.getSubtypeId() != null) {
            MonthlySubtype subtype = subtypeRepository.findById(request.getSubtypeId())
                    .orElseThrow(() -> new RuntimeException("Subtype not found"));
            existingClass.setSubtype(subtype);
        }

        if (request.getTutorId() != null) {
            throw new RuntimeException("Cannot update tutors through this endpoint. Use assignTutors/removeTutors instead.");
        }

        if (request.getClassName() != null) {
            existingClass.setClassName(request.getClassName());
        }

        if (request.getStartDate() != null) {
            existingClass.setStartDate(request.getStartDate());
        }

        if (request.getEndDate() != null) {
            existingClass.setEndDate(request.getEndDate());
        }

        if (request.getIsActive() != null) {
            existingClass.setIsActive(request.getIsActive());
        }

        MonthlyClass updatedClass = classRepository.save(existingClass);
        return MonthlyClassDto.fromEntity(updatedClass);
    }

    /**
     * ลบครูหลายคนออกจากคลาส
     */
    public MonthlyClassDto removeTutors(Long classId, RemoveTutorsRequestDto request) {
        MonthlyClass classEntity = classRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Class not found"));

        for (Long tutorId : request.getTutorIds()) {
            User tutor = userRepository.findById(tutorId)
                    .orElseThrow(() -> new RuntimeException("Tutor not found with id: " + tutorId));

            if (!classTutorRepository.existsByClassIdAndTutorId(classId, tutorId)) {
                throw new RuntimeException("Tutor " + tutor.getNickname() + " is not assigned to this class");
            }

            MonthlyClassTutor classTutor = classTutorRepository
                    .findByMonthlyClassAndTutor(classEntity, tutor)
                    .orElseThrow();

            classTutorRepository.delete(classTutor);
        }

        // Refresh ด้วย findByIdWithTutors
        classEntity = classRepository.findByIdWithTutors(classId).orElseThrow();
        return MonthlyClassDto.fromEntity(classEntity);
    }

    /**
     * ลบครู 1 คน (backward compatibility)
     */
    public MonthlyClassDto removeTutor(Long classId, Long tutorId) {
        MonthlyClass classEntity = classRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Class not found"));

        User tutor = userRepository.findById(tutorId)
                .orElseThrow(() -> new RuntimeException("Tutor not found"));

        MonthlyClassTutor classTutor = classTutorRepository
                .findByMonthlyClassAndTutor(classEntity, tutor)
                .orElseThrow(() -> new RuntimeException("Tutor is not assigned to this class"));

        classTutorRepository.delete(classTutor);

        classEntity = classRepository.findById(classId).orElseThrow();
        return MonthlyClassDto.fromEntity(classEntity);
    }

    /**
     * เปิด/ปิดการใช้งานคลาส
     */
    public MonthlyClassDto toggleActive(Long id) {
        MonthlyClass classEntity = classRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Class not found"));

        classEntity.setIsActive(!classEntity.getIsActive());
        MonthlyClass updatedClass = classRepository.save(classEntity);
        return MonthlyClassDto.fromEntity(updatedClass);
    }

    /**
     * ลบคลาส
     */
    public void deleteClass(Long id) {
        MonthlyClass classEntity = classRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Class not found"));

        classEntity.setIsActive(false);
        classRepository.save(classEntity);
    }

    /**
     * ค้นหาคลาสด้วยหลายเงื่อนไข
     */
    @Transactional(readOnly = true)
    public List<MonthlyClassDto> searchClasses(Long tutorId, Long subjectId, Long subtypeId) {
        return classRepository.findByMultipleCriteria(tutorId, subjectId, subtypeId).stream()
                .map(MonthlyClassDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * ค้นหาคลาสตามช่วงวันที่
     */
    @Transactional(readOnly = true)
    public List<MonthlyClassDto> getClassesByDateRange(LocalDate startDate, LocalDate endDate) {
        if (endDate.isBefore(startDate)) {
            throw new RuntimeException("End date must be after or equal to start date");
        }

        return classRepository.findByDateRange(startDate, endDate).stream()
                .map(MonthlyClassDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * นับจำนวนคลาสที่ active
     */
    @Transactional(readOnly = true)
    public long countActiveClasses() {
        return classRepository.countByIsActiveTrue();
    }

    /**
     * นับจำนวนคลาสของติวเตอร์
     */
    @Transactional(readOnly = true)
    public long countClassesByTutor(Long tutorId) {
        return classRepository.countByTutorIdAndActive(tutorId);
    }

    /**
     * Helper method: Get current logged-in user
     */
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Current user not found"));
    }

    /**
     * ดึงรายชื่อครูทั้งหมดในคลาส
     */
    @Transactional(readOnly = true)
    public List<User> getClassTutors(Long classId) {
        List<MonthlyClassTutor> classTutors = classTutorRepository.findByClassId(classId);
        return classTutors.stream()
                .map(MonthlyClassTutor::getTutor)
                .collect(Collectors.toList());
    }

    /**
     * แทนที่ครูทั้งหมดด้วยรายการใหม่
     */
    public MonthlyClassDto replaceTutors(Long classId, AssignTutorsRequestDto request) {
        MonthlyClass classEntity = classRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Class not found"));

        classTutorRepository.deleteByMonthlyClass(classEntity);

        User currentUser = getCurrentUser();
        for (Long tutorId : request.getTutorIds()) {
            User tutor = userRepository.findById(tutorId)
                    .orElseThrow(() -> new RuntimeException("Tutor not found with id: " + tutorId));

            MonthlyClassTutor classTutor = new MonthlyClassTutor(classEntity, tutor, currentUser);
            classTutorRepository.save(classTutor);
        }

        // Refresh ด้วย findByIdWithTutors
        classEntity = classRepository.findByIdWithTutors(classId).orElseThrow();
        return MonthlyClassDto.fromEntity(classEntity);
    }

    /**
     * ดึงรายชื่อนักเรียนทั้งหมดในคลาส
     */
    @Transactional(readOnly = true)
    public List<Student> getClassStudents(Long classId) {
        MonthlyClass classEntity = classRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Class not found with id: " + classId));

        return classEntity.getEnrollments().stream()
                .filter(enrollment -> enrollment.getIsActive())
                .map(MonthlyEnrollment::getStudent)
                .collect(Collectors.toList());
    }
}