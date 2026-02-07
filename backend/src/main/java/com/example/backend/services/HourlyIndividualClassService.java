package com.example.backend.services;

import com.example.backend.dtos.hourly.CreateHourlyIndividualClassRequestDto;
import com.example.backend.dtos.hourly.HourlyIndividualClassDto;
import com.example.backend.dtos.hourly.UpdateHourlyIndividualClassRequestDto;
import com.example.backend.dtos.student.StudentDto;
import com.example.backend.dtos.tutor.TutorDto;
import com.example.backend.entities.Grade;
import com.example.backend.entities.Subject;
import com.example.backend.entities.User;
import com.example.backend.entities.hourly.HourlyIndividualClass;
import com.example.backend.entities.hourly.HourlyIndividualClassStudent;
import com.example.backend.entities.hourly.HourlyIndividualClassTutor;
import com.example.backend.entities.student.Student;
import com.example.backend.repositories.GradeRepository;
import com.example.backend.repositories.SubjectRepository;
import com.example.backend.repositories.UserRepository;
import com.example.backend.repositories.hourly.HourlyIndividualClassRepository;
import com.example.backend.repositories.hourly.HourlyIndividualClassStudentRepository;
import com.example.backend.repositories.hourly.HourlyIndividualClassTutorRepository;
import com.example.backend.repositories.student.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class HourlyIndividualClassService {

    @Autowired
    private HourlyIndividualClassRepository classRepository;

    @Autowired
    private HourlyIndividualClassTutorRepository classTutorRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private GradeRepository gradeRepository;

    @Autowired
    private HourlyIndividualClassStudentRepository classStudentRepository;

    @Autowired
    private HourlyIndividualClassStudentRepository hourlyIndividualClassStudentRepository;

    /**
     * ึงรายการคลาสทั้งหมด - รองรับ filter ตาม tutorId
     */
    @Transactional(readOnly = true)
    public List<HourlyIndividualClassDto> getAllClasses(Long tutorId, Boolean active) {
        List<HourlyIndividualClass> classes;

        if (tutorId != null) {
            // Filter ตาม tutor
            classes = getClassesByTutor(tutorId, active);
        } else if (active != null && active) {
            classes = classRepository.findAllActive();
        } else {
            classes = classRepository.findAll();
        }

        return classes.stream()
                .map(cls -> {
                    HourlyIndividualClassDto dto = HourlyIndividualClassDto.fromEntity(cls);

                    // นับจำนวนนักเรียนจริง
                    Long totalFromDb = hourlyIndividualClassStudentRepository.countAllByClassId(cls.getId());
                    dto.setTotalStudents(totalFromDb);

                    // ดึง gradeName ของนักเรียนคนแรก
                    List<HourlyIndividualClassStudent> students =
                            classStudentRepository.findByClassIdAndActive(cls.getId());
                    if (!students.isEmpty() && students.get(0).getStudent() != null
                            && students.get(0).getStudent().getGrade() != null) {
                        dto.setGradeName(students.get(0).getStudent().getGrade().getGradeName());
                    }

                    return dto;
                })
                .collect(Collectors.toList());
    }

    /**
     * เพิ่มใหม่: ดึงคลาสตาม tutor
     */
    @Transactional(readOnly = true)
    public List<HourlyIndividualClass> getClassesByTutor(Long tutorId, Boolean active) {
        // ดึง class IDs ที่ tutor สอน
        List<Long> classIds = classTutorRepository.findByTutorId(tutorId).stream()
                .map(ct -> ct.getHourlyIndividualClass().getId())
                .collect(Collectors.toList());

        if (classIds.isEmpty()) {
            return List.of();
        }

        // ดึงคลาสตาม IDs
        List<HourlyIndividualClass> classes = classRepository.findAllById(classIds);

        // Filter ตาม active ถ้ามี
        if (active != null && active) {
            classes = classes.stream()
                    .filter(HourlyIndividualClass::getIsActive)
                    .collect(Collectors.toList());
        }

        return classes;
    }

    /**
     * ดึงรายการคลาสที่ active
     */
    @Transactional(readOnly = true)
    public List<HourlyIndividualClassDto> getActiveClasses() {
        return classRepository.findAllActive().stream()
                .map(HourlyIndividualClassDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * ดึงข้อมูลคลาสตาม ID
     */
    @Transactional(readOnly = true)
    public HourlyIndividualClassDto getClassById(Long id) {
        HourlyIndividualClass classEntity = classRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Class not found with id: " + id));
        return HourlyIndividualClassDto.fromEntity(classEntity);
    }

    /**
     * ดึงคลาสทั้งหมดของ student
     */
    @Transactional(readOnly = true)
    public List<HourlyIndividualClassDto> getClassesByStudent(Long studentId, Boolean active) {
        List<HourlyIndividualClass> classes;

        if (active != null && active) {
            classes = classRepository.findByStudentIdAndActive(studentId);
        } else {
            classes = classRepository.findByStudentId(studentId);
        }

        return classes.stream()
                .map(HourlyIndividualClassDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * ดึงคลาสทั้งหมดตามวิชา
     */
    @Transactional(readOnly = true)
    public List<HourlyIndividualClassDto> getClassesBySubject(Long subjectId, Boolean active) {
        List<HourlyIndividualClass> classes;

        if (active != null && active) {
            classes = classRepository.findBySubjectIdAndActive(subjectId);
        } else {
            classes = classRepository.findAll().stream()
                    .filter(c -> c.getSubject().getId().equals(subjectId))
                    .collect(Collectors.toList());
        }

        return classes.stream()
                .map(HourlyIndividualClassDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * แก้ไข: สร้างคลาสใหม่ - ไม่บังคับ tutor
     */

    public HourlyIndividualClassDto createClass(CreateHourlyIndividualClassRequestDto request) {
        // 1. Validate ว่ามีนักเรียนอย่างน้อย 1 คน
        if (request.getStudentIds() == null || request.getStudentIds().isEmpty()) {
            throw new RuntimeException("ต้องเลือกนักเรียนอย่างน้อย 1 คน");
        }

        // 2. Validate subject
        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new RuntimeException("Subject not found"));

        // 3. Validate grade (ถ้ามี)
        Grade grade = null;
        if (request.getGradeId() != null) {
            grade = gradeRepository.findById(request.getGradeId())
                    .orElseThrow(() -> new RuntimeException("Grade not found"));
        }

        // 4. Validate ว่านักเรียนทุกคนมีอยู่จริง
        List<Student> students = new ArrayList<>();
        for (Long studentId : request.getStudentIds()) {
            Student student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new RuntimeException("Student not found: " + studentId));
            students.add(student);
        }

        // 5. สร้างชื่อคลาส
        String className = request.getClassName();
        if (className == null || className.isEmpty()) {
            String subjectName = subject.getSubjectName();
            String studentNames = students.stream()
                    .map(Student::getNickname)
                    .collect(Collectors.joining(", "));

            if (students.size() == 1) {
                className = "คลาสเดี่ยว - " + studentNames + " (" + subjectName + ")";
            } else {
                className = "คลาส PV-กลุ่ม - " + studentNames + " (" + subjectName + ")";
            }
        }

        User currentUser = getCurrentUser();

        // 6. สร้างคลาส (student_id = null เพราะเก็บใน junction table)
        HourlyIndividualClass newClass = new HourlyIndividualClass();
        newClass.setSubject(subject);
        newClass.setGrade(grade);
        newClass.setStudent(null);  // ไม่ใช้ field นี้แล้ว
        newClass.setClassName(className);
        newClass.setIsActive(true);
        newClass.setCreatedBy(currentUser);

        HourlyIndividualClass savedClass = classRepository.save(newClass);

        if (request.getStudentIds() != null && !request.getStudentIds().isEmpty()) {
            for (Long studentId : request.getStudentIds()) {
                Student student = studentRepository.findById(studentId)
                        .orElseThrow(() -> new RuntimeException("Student not found: " + studentId));

                HourlyIndividualClassStudent classStudent = new HourlyIndividualClassStudent();
                classStudent.setHourlyIndividualClass(savedClass);
                classStudent.setStudent(student);
                classStudent.setIsActive(true);

                hourlyIndividualClassStudentRepository.save(classStudent);
            }
        }

        return HourlyIndividualClassDto.fromEntity(savedClass);
    }

    /**
     * แก้ไขข้อมูลคลาส
     */
    public HourlyIndividualClassDto updateClass(Long id, UpdateHourlyIndividualClassRequestDto request) {
        HourlyIndividualClass existingClass = classRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Class not found with id: " + id));

        if (request.getSubjectId() != null) {
            Subject subject = subjectRepository.findById(request.getSubjectId())
                    .orElseThrow(() -> new RuntimeException("Subject not found with id: " + request.getSubjectId()));
            existingClass.setSubject(subject);
        }

        if (request.getClassName() != null) {
            existingClass.setClassName(request.getClassName());
        }

        if (request.getIsActive() != null) {
            existingClass.setIsActive(request.getIsActive());
        }

        HourlyIndividualClass updatedClass = classRepository.save(existingClass);
        return HourlyIndividualClassDto.fromEntity(updatedClass);
    }

    /**
     * เปิด/ปิดการใช้งานคลาส
     */
    public HourlyIndividualClassDto toggleActive(Long id) {
        HourlyIndividualClass classEntity = classRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Class not found with id: " + id));

        classEntity.setIsActive(!classEntity.getIsActive());

        HourlyIndividualClass updatedClass = classRepository.save(classEntity);
        return HourlyIndividualClassDto.fromEntity(updatedClass);
    }

    /**
     * ลบคลาส (Soft delete)
     */
    public void deleteClass(Long id) {
        HourlyIndividualClass classEntity = classRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Class not found with id: " + id));

        classEntity.setIsActive(false);
        classRepository.save(classEntity);
    }

    /**
     * ค้นหาคลาสด้วยหลายเงื่อนไข
     */
    @Transactional(readOnly = true)
    public List<HourlyIndividualClassDto> searchClasses(Long tutorId, Long subjectId, Long studentId) {
        return classRepository.findByMultipleCriteria(tutorId, subjectId, studentId).stream()
                .map(HourlyIndividualClassDto::fromEntity)
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
        return classTutorRepository.countByTutorId(tutorId);
    }

    /**
     * นับจำนวนคลาสของนักเรียน
     */
    @Transactional(readOnly = true)
    public long countClassesByStudent(Long studentId) {
        return classRepository.countByStudentIdAndActive(studentId);
    }

    /**
     * เปิดการใช้งานคลาส
     */
    public void activateClass(Long id) {
        HourlyIndividualClass classEntity = classRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Class not found with id: " + id));

        classEntity.setIsActive(true);
        classRepository.save(classEntity);
    }

    /**
     * ปิดการใช้งานคลาส
     */
    public void deactivateClass(Long id) {
        HourlyIndividualClass classEntity = classRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Class not found with id: " + id));

        classEntity.setIsActive(false);
        classRepository.save(classEntity);
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
     * แก้ไข: เพิ่มครูให้กับคลาส - ใช้ตาราง junction
     */
    public HourlyIndividualClassDto assignTutor(Long classId, Long tutorId) {
        HourlyIndividualClass classEntity = classRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Class not found"));

        User tutor = userRepository.findById(tutorId)
                .orElseThrow(() -> new RuntimeException("Tutor not found"));

        // เช็คว่ามีครูคนนี้แล้วหรือยัง
        if (classTutorRepository.existsByClassIdAndTutorId(classId, tutorId)) {
            throw new RuntimeException("Tutor already assigned to this class");
        }

        HourlyIndividualClass updatedClass = classRepository.findById(classId).get();
        return HourlyIndividualClassDto.fromEntity(updatedClass);
    }

    /**
     * ดึงคลาสของครู + คลาสที่ยังไม่มีครู (optional)
     */
    @Transactional(readOnly = true)
    public List<HourlyIndividualClassDto> getClassesByTutorWithUnassigned(
            Long tutorId, Boolean active, Boolean includeUnassigned) {

        List<HourlyIndividualClass> classes = new ArrayList<>();

        // Filter ตาม active
        if (active != null && active) {
            classes = classes.stream()
                    .filter(HourlyIndividualClass::getIsActive)
                    .collect(Collectors.toList());
        }

        return classes.stream()
                .map(HourlyIndividualClassDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * ดึงรายการครูทั้งหมดที่สอนคลาสนี้
     */
    public List<TutorDto> getClassTutors(Long classId) {
        // ตรวจสอบว่าคลาสมีอยู่จริง
        classRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Class not found with id: " + classId));

        // ดึงข้อมูลครูจาก junction table
        List<HourlyIndividualClassTutor> classTutors = classTutorRepository.findByClassId(classId);

        // แปลงเป็น TutorDto
        return classTutors.stream()
                .map(ct -> {
                    User tutor = ct.getTutor();
                    return new TutorDto(
                            tutor.getId(),
                            tutor.getUsername(),
                            tutor.getNickname(),
                            tutor.getCreatedAt()
                    );
                })
                .collect(Collectors.toList());
    }

    /**
     * เพิ่มครูเข้าคลาส (รองรับหลายคน)
     */
    @Transactional
    public void addTutorsToClass(Long classId, List<Long> tutorIds) {
        HourlyIndividualClass cls = classRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Class not found"));

        for (Long tutorId : tutorIds) {
            // เช็คว่าครูมีอยู่จริง
            User tutor = userRepository.findById(tutorId)
                    .orElseThrow(() -> new RuntimeException("Tutor not found: " + tutorId));

            // เช็คว่าครูสอนคลาสนี้อยู่แล้วหรือไม่
            if (classTutorRepository.existsByClassIdAndTutorId(classId, tutorId)) {
                throw new RuntimeException("Tutor already assigned: " + tutor.getNickname());
            }

            // สร้าง record ใหม่
            HourlyIndividualClassTutor classTutor = new HourlyIndividualClassTutor();
            classTutor.setHourlyIndividualClass(cls);
            classTutor.setTutor(tutor);

            classTutorRepository.save(classTutor);
        }
    }

    /**
     * ลบครูออกจากคลาส
     */
    @Transactional
    public void removeTutorsFromClass(Long classId, List<Long> tutorIds) {
        for (Long tutorId : tutorIds) {
            if (!classTutorRepository.existsByClassIdAndTutorId(classId, tutorId)) {
                throw new RuntimeException("Tutor not found in this class");
            }

            classTutorRepository.deleteByClassIdAndTutorId(classId, tutorId);
        }
    }

    // ดึงรายชื่อนักเรียนในคลาส
    @Transactional(readOnly = true)
    public List<StudentDto> getClassStudents(Long classId) {
        classRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Class not found"));

        List<HourlyIndividualClassStudent> classStudents =
                classStudentRepository.findByClassIdAndActive(classId);

        return classStudents.stream()
                .map(cs -> StudentDto.fromEntity(cs.getStudent()))
                .collect(Collectors.toList());
    }

    // เพิ่มนักเรียนเข้าคลาส
    @Transactional
    public void addStudentsToClass(Long classId, List<Long> studentIds) {
        HourlyIndividualClass cls = classRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Class not found"));

        for (Long studentId : studentIds) {
            // เช็คว่ามีนักเรียนคนนี้อยู่แล้วหรือยัง
            if (classStudentRepository.existsByClassIdAndStudentId(classId, studentId)) {
                throw new RuntimeException("Student already in class: " + studentId);
            }

            Student student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new RuntimeException("Student not found: " + studentId));

            HourlyIndividualClassStudent classStudent = new HourlyIndividualClassStudent();
            classStudent.setHourlyIndividualClass(cls);
            classStudent.setStudent(student);
            classStudent.setIsActive(true);
            classStudentRepository.save(classStudent);
        }
    }

    // ลบนักเรียนออกจากคลาส
    @Transactional
    public void removeStudentFromClass(Long classId, Long studentId) {
        if (!classStudentRepository.existsByClassIdAndStudentId(classId, studentId)) {
            throw new RuntimeException("Student not in class");
        }
        classStudentRepository.deleteByClassIdAndStudentId(classId, studentId);
    }
}