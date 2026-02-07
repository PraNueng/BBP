package com.example.backend.services.student;

import com.example.backend.dtos.hourform.HourFormDto;
import com.example.backend.dtos.student.StudentDto;
import com.example.backend.dtos.student.StudentRequestDto;
import com.example.backend.dtos.student.StudentDetailDto;
import com.example.backend.dtos.student.StudentHistoryDto;
import com.example.backend.entities.Grade;
import com.example.backend.entities.User;
import com.example.backend.entities.hourform.HourForm;
import com.example.backend.entities.hourform.StudentHourFormOverride;
import com.example.backend.entities.hourly.*;
import com.example.backend.entities.monthly.MonthlyEnrollment;
import com.example.backend.entities.monthly.MonthlySubtype;
import com.example.backend.entities.student.Student;
import com.example.backend.entities.monthly.MonthlyClass;
import com.example.backend.entities.student.StudentCoursePurchase;
import com.example.backend.repositories.GradeRepository;
import com.example.backend.repositories.SubjectRepository;
import com.example.backend.repositories.UserRepository;
import com.example.backend.repositories.hourform.HourFormRepository;
import com.example.backend.repositories.hourform.StudentHourFormOverrideRepository;
import com.example.backend.repositories.hourly.*;
import com.example.backend.repositories.monthly.MonthlyEnrollmentRepository;
import com.example.backend.repositories.monthly.MonthlyClassRepository;
import com.example.backend.repositories.monthly.MonthlySubtypeRepository;
import com.example.backend.repositories.student.StudentCoursePurchaseRepository;
import com.example.backend.repositories.student.StudentRepository;
import com.example.backend.services.EnrollmentService;
import com.example.backend.services.HourFormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@CacheConfig(cacheNames = "students")
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private GradeRepository gradeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HourlyGroupEnrollmentRepository hourlyGroupEnrollmentRepository;

    @Autowired
    private HourlyGroupClassRepository hourlyGroupClassRepository;

    @Autowired
    private HourlyGroupSubtypeRepository hourlyGroupSubtypeRepository;

    @Autowired
    private HourlyIndividualClassRepository hourlyIndividualClassRepository;

    @Autowired
    private MonthlyEnrollmentRepository monthlyEnrollmentRepository;

    @Autowired
    private MonthlyClassRepository monthlyClassRepository;

    @Autowired
    private MonthlySubtypeRepository monthlySubtypeRepository;

    @Autowired
    private HourFormRepository hourFormRepository;

    @Autowired
    private StudentHistoryService historyService;

    @Autowired
    private StudentCodeGeneratorService codeGenerator;

    @Autowired
    private StudentValidationService validationService;

    @Autowired
    private EnrollmentService enrollmentService;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private HourlyIndividualClassTutorRepository hourlyIndividualClassTutorRepository;

    @Autowired
    private StudentCoursePurchaseRepository coursePurchaseRepository;

    @Autowired
    private HourlyIndividualClassStudentRepository hourlyIndividualClassStudentRepository;

    @Autowired
    private HourFormService hourFormService;

    @Autowired
    private StudentHourFormOverrideRepository overrideRepository;

    // ===== CREATE WITH CLASSES =====

    @Transactional
    @CacheEvict(allEntries = true)
    public StudentDto createStudentWithClasses(StudentRequestDto dto, String username) {
        var validation = validationService.validateStudent(convertToBasicDto(dto), false);
        if (!validation.isValid()) {
            throw new IllegalArgumentException(validation.getErrorMessage());
        }

        Grade grade = gradeRepository.findById(dto.getGradeId())
                .orElseThrow(() -> new IllegalArgumentException("Grade not found: " + dto.getGradeId()));

        User creator = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("User not found: " + username));

        String studentCode = Optional.ofNullable(dto.getStudentCode())
                .filter(code -> !code.trim().isEmpty())
                .orElseGet(() -> codeGenerator.generateSequentialCode(grade));

        Student student = new Student();
        student.setStudentCode(studentCode);
        student.setFirstName(validationService.sanitizeText(dto.getFirstName()));
        student.setLastName(validationService.sanitizeText(dto.getLastName()));
        student.setNickname(validationService.sanitizeText(dto.getNickname()));
        student.setSchoolName(dto.getSchoolName());
        student.setGrade(grade);
        student.setPhoneStudent(validationService.sanitizePhoneNumber(dto.getPhoneStudent()));
        student.setPhoneParent(validationService.sanitizePhoneNumber(dto.getPhoneParent()));
        student.setStudyPlan(dto.getStudyPlan());
        student.setIsActive(true);
        student.setCreatedBy(creator);

        Student savedStudent = studentRepository.save(student);

        if (dto.getClasses() != null && !dto.getClasses().isEmpty()) {
            for (StudentRequestDto.ClassEnrollmentDto classDto : dto.getClasses()) {
                enrollStudentToClass(savedStudent, classDto, grade);
            }
        }

        return StudentDto.fromEntity(savedStudent);
    }

    private void enrollStudentToClass(Student student, StudentRequestDto.ClassEnrollmentDto classDto, Grade grade) {
        if ("MONTH".equals(classDto.getClassType())) {
            enrollToMonthlyClass(student, classDto, grade);
        } else if ("HOUR".equals(classDto.getClassType())) {
            if ("GROUP".equals(classDto.getMode())) {
                enrollToHourlyGroupClass(student, classDto, grade);
            } else if ("PRIVATE".equals(classDto.getMode())) {
                createHourlyIndividualClass(student, classDto, grade);
            } else if ("PRIVATE_GROUP".equals(classDto.getMode())) {
                createHourlyIndividualGroupClass(student, classDto, grade);
            }
        }
    }

    /**
     * ลงทะเบียนคลาส Monthly - รองรับหลายครู
     */
    @CacheEvict(cacheNames = "students", allEntries = true)
    public void enrollToMonthlyClass(Student student, StudentRequestDto.ClassEnrollmentDto classDto, Grade grade) {
        Long subtypeId;

        if (classDto.getSubtypeId() != null) {
            subtypeId = classDto.getSubtypeId();
            monthlySubtypeRepository.findById(subtypeId)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "ไม่พบประเภทคลาสรายเดือน ID: " + subtypeId
                    ));
        } else if (classDto.getSchedule() != null && !classDto.getSchedule().isEmpty()) {
            // แปลง code (MON-FRI) เป็นชื่อไทย (จันทร์-ศุกร์) ก่อนค้นหา
            String thaiScheduleName = convertScheduleCodeToThaiName(classDto.getSchedule());
            subtypeId = getMonthlySubtypeIdByName(thaiScheduleName);
        } else {
            throw new IllegalArgumentException("กรุณาระบุประเภทคลาสรายเดือน (subtypeId หรือ schedule)");
        }

        // ตรวจสอบว่ามี subjectId หรือไม่
        if (classDto.getSubjectId() == null) {
            throw new IllegalArgumentException("กรุณาระบุวิชาที่ต้องการลงทะเบียน");
        }

        // หาคลาสที่ตรงกับเงื่อนไข: grade + subtype + subject + active
        List<MonthlyClass> availableClasses = monthlyClassRepository
                .findByGradeIdAndSubtypeIdAndIsActiveTrue(grade.getId(), subtypeId)
                .stream()
                .filter(c -> c.getSubject() != null && c.getSubject().getId().equals(classDto.getSubjectId()))
                .collect(Collectors.toList());

        if (availableClasses.isEmpty()) {
            throw new IllegalArgumentException(
                    "ไม่พบวิชา ระดับชั้น และคลาสรายเดือนดังกล่าวที่เปิดอยู่ กรุณาตรวจสอบว่ามีครูผู้สอนคลาสนั้นหรือไม่"
            );
        }

        // เลือกคลาสแรกที่เจอ (หรือใช้ logic อื่นในการเลือก เช่น จำนวนนักเรียนน้อยที่สุด)
        MonthlyClass monthlyClass = availableClasses.get(0);

        // ตรวจสอบว่าลงทะเบียนแล้วหรือยัง
        Optional<MonthlyEnrollment> existing = monthlyEnrollmentRepository
                .findByClassIdAndStudentId(monthlyClass.getId(), student.getId());

        if (existing.isPresent()) {
            throw new IllegalArgumentException(
                    "นักเรียนลงทะเบียนคลาส " + monthlyClass.getClassName() + " แล้ว"
            );
        }

        MonthlyEnrollment enrollment = new MonthlyEnrollment();
        enrollment.setMonthlyClass(monthlyClass);
        enrollment.setStudent(student);
        enrollment.setEnrolledGradeId(grade.getId());
        enrollment.setIsActive(true);

        monthlyEnrollmentRepository.save(enrollment);
    }

    /**
     * ลงทะเบียนคลาส Hourly Group - รองรับหลายครู
     */
    @CacheEvict(cacheNames = "students", allEntries = true)
    public void enrollToHourlyGroupClass(Student student, StudentRequestDto.ClassEnrollmentDto classDto, Grade grade) {
        Long subtypeId;

        if (classDto.getSubtypeId() != null) {
            subtypeId = classDto.getSubtypeId();
            hourlyGroupSubtypeRepository.findById(subtypeId)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "ไม่พบประเภทกลุ่ม ID: " + subtypeId
                    ));
        } else if (classDto.getGroupType() != null && !classDto.getGroupType().isEmpty()) {
            subtypeId = getHourlyGroupSubtypeIdByName(classDto.getGroupType());
        } else {
            throw new IllegalArgumentException("กรุณาระบุประเภทกลุ่ม (subtypeId หรือ groupType)");
        }

        // ตรวจสอบว่ามี subjectId หรือไม่
        if (classDto.getSubjectId() == null) {
            throw new IllegalArgumentException("กรุณาระบุวิชาที่ต้องการลงทะเบียน");
        }

        validateStudentHasCoursePurchase(student, classDto.getSubjectId(), "GROUP");

        // หาคลาสที่ตรงกับเงื่อนไข: grade + subtype + subject + active
        List<HourlyGroupClass> availableClasses = hourlyGroupClassRepository
                .findByGradeIdAndSubtypeIdAndIsActiveTrue(grade.getId(), subtypeId)
                .stream()
                .filter(c -> c.getSubject() != null && c.getSubject().getId().equals(classDto.getSubjectId()))
                .collect(Collectors.toList());

        if (availableClasses.isEmpty()) {
            throw new IllegalArgumentException(
                    "ไม่พบวิชา ระดับชั้น และประเภทคลาสรายชั่วโมงดังกล่าวที่เปิดอยู่ กรุณาตรวจสอบว่ามีครูผู้สอนคลาสนั้นหรือไม่"
            );
        }

        HourlyGroupClass hourlyGroupClass = availableClasses.get(0);

        Optional<HourlyGroupEnrollment> existing = hourlyGroupEnrollmentRepository
                .findByClassIdAndStudentId(hourlyGroupClass.getId(), student.getId());

        if (existing.isPresent()) {
            throw new IllegalArgumentException(
                    "นักเรียนลงทะเบียนคลาส " + hourlyGroupClass.getClassName() + " แล้ว"
            );
        }

        HourlyGroupEnrollment enrollment = new HourlyGroupEnrollment();
        enrollment.setHourlyGroupClass(hourlyGroupClass);
        enrollment.setStudent(student);
        enrollment.setEnrolledGradeId(grade.getId());
        enrollment.setIsActive(true);

        hourlyGroupEnrollmentRepository.save(enrollment);
    }

    @CacheEvict(cacheNames = "students", allEntries = true)
    public void createHourlyIndividualClass(Student student, StudentRequestDto.ClassEnrollmentDto classDto, Grade grade) {
        if (classDto.getHours() == null || classDto.getHours() <= 0) {
            throw new IllegalArgumentException("จำนวนชั่วโมงต้องมากกว่า 0");
        }

        // ต้องมี subjectId
        if (classDto.getSubjectId() == null) {
            throw new IllegalArgumentException("กรุณาระบุวิชาที่ต้องการเรียน");
        }

        validateStudentHasCoursePurchase(student, classDto.getSubjectId(), "INDIVIDUAL");

        // ดึงข้อมูล Subject
        var subject = subjectRepository.findById(classDto.getSubjectId())
                .orElseThrow(() -> new IllegalArgumentException("ไม่พบวิชา ID: " + classDto.getSubjectId()));

        User currentUser = getCurrentUser();

        // ดึงข้อมูลครูถ้ามี tutorId
        User tutor = null;
        if (classDto.getTutorId() != null) {
            tutor = userRepository.findById(classDto.getTutorId())
                    .orElseThrow(() -> new IllegalArgumentException("ไม่พบครู ID: " + classDto.getTutorId()));
        }

        // สร้างคลาส
        HourlyIndividualClass individualClass = new HourlyIndividualClass();
        individualClass.setStudent(student);
        individualClass.setGrade(grade);
        individualClass.setSubject(subject);
        individualClass.setIsActive(true);
        individualClass.setCreatedBy(currentUser);
        individualClass.setClassName("คลาสเดี่ยว - " + student.getNickname() + " (" + subject.getSubjectName() + ")");

        // บันทึกคลาส
        HourlyIndividualClass savedClass = hourlyIndividualClassRepository.save(individualClass);

        if (tutor != null) {
            HourlyIndividualClassTutor classTutor = new HourlyIndividualClassTutor();
            classTutor.setHourlyIndividualClass(savedClass);
            classTutor.setTutor(tutor);
            hourlyIndividualClassTutorRepository.save(classTutor);

            System.out.println("Created junction record: Class " + savedClass.getId() + " -> Tutor " + tutor.getId());
        }
    }

    @CacheEvict(cacheNames = "students", allEntries = true)
    public void createHourlyIndividualGroupClass(Student student, StudentRequestDto.ClassEnrollmentDto classDto, Grade grade) {
        if (classDto.getHours() == null || classDto.getHours() <= 0) {
            throw new IllegalArgumentException("จำนวนชั่วโมงต้องมากกว่า 0");
        }

        if (classDto.getSubjectId() == null) {
            throw new IllegalArgumentException("กรุณาระบุวิชาที่ต้องการเรียน");
        }

        // Validate ว่าซื้อคอร์ส hourly_individual_group แล้ว
        validateStudentHasCoursePurchase(student, classDto.getSubjectId(), "INDIVIDUAL_GROUP");

        var subject = subjectRepository.findById(classDto.getSubjectId())
                .orElseThrow(() -> new IllegalArgumentException("ไม่พบวิชา ID: " + classDto.getSubjectId()));

        User currentUser = getCurrentUser();

        User tutor = null;
        if (classDto.getTutorId() != null) {
            tutor = userRepository.findById(classDto.getTutorId())
                    .orElseThrow(() -> new IllegalArgumentException("ไม่พบครู ID: " + classDto.getTutorId()));
        }

        // ใช้ HourlyIndividualClass เดิม (เพราะโครงสร้างเหมือนกัน แค่ class_type ต่างกัน)
        HourlyIndividualClass individualClass = new HourlyIndividualClass();
        individualClass.setStudent(student);
        individualClass.setGrade(grade);
        individualClass.setSubject(subject);
        individualClass.setIsActive(true);
        individualClass.setCreatedBy(currentUser);
        individualClass.setClassName("คลาสเดี่ยวแบบกลุ่ม - " + student.getNickname() + " (" + subject.getSubjectName() + ")");

        HourlyIndividualClass savedClass = hourlyIndividualClassRepository.save(individualClass);

        if (tutor != null) {
            HourlyIndividualClassTutor classTutor = new HourlyIndividualClassTutor();
            classTutor.setHourlyIndividualClass(savedClass);
            classTutor.setTutor(tutor);
            hourlyIndividualClassTutorRepository.save(classTutor);
        }
    }

    // ===== HELPER METHODS =====

    private Long getMonthlySubtypeIdByName(String subtypeName) {
        return monthlySubtypeRepository.findBySubtypeName(subtypeName)
                .map(MonthlySubtype::getId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "ไม่พบประเภทคลาสดังกล่าวที่เปิดอยู่ กรุณาตรวจสอบว่ามีครูผู้สอนคลาสนั้นหรือไม่"
                ));
    }

    private Long getHourlyGroupSubtypeIdByName(String subtypeName) {
        return hourlyGroupSubtypeRepository.findBySubtypeName(subtypeName)
                .map(HourlyGroupSubtype::getId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "ไม่พบประเภทคลาสดังกล่าวที่เปิดอยู่ กรุณาตรวจสอบว่ามีครูผู้สอนคลาสนั้นหรือไม่"
                ));
    }

    private StudentDto convertToBasicDto(StudentRequestDto dto) {
        StudentDto basicDto = new StudentDto();
        basicDto.setFirstName(dto.getFirstName());
        basicDto.setLastName(dto.getLastName());
        basicDto.setNickname(dto.getNickname());
        basicDto.setSchoolName(dto.getSchoolName());
        basicDto.setGradeId(dto.getGradeId());
        basicDto.setPhoneStudent(dto.getPhoneStudent());
        basicDto.setPhoneParent(dto.getPhoneParent());
        basicDto.setStudyPlan(dto.getStudyPlan());
        return basicDto;
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Current user not found"));
    }

    // ===== CRUD METHODS =====

    @Transactional
    @CacheEvict(allEntries = true)
    public StudentDto createStudent(StudentDto dto, String username) {
        var validation = validationService.validateStudent(dto, false);
        if (!validation.isValid()) {
            throw new IllegalArgumentException(validation.getErrorMessage());
        }

        Grade grade = gradeRepository.findById(dto.getGradeId())
                .orElseThrow(() -> new IllegalArgumentException("Grade not found: " + dto.getGradeId()));

        User creator = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("User not found: " + username));

        String studentCode = Optional.ofNullable(dto.getStudentCode())
                .filter(code -> !code.trim().isEmpty())
                .orElseGet(() -> codeGenerator.generateSequentialCode(grade));

        Student student = new Student();
        student.setStudentCode(studentCode);
        student.setFirstName(validationService.sanitizeText(dto.getFirstName()));
        student.setLastName(validationService.sanitizeText(dto.getLastName()));
        student.setNickname(validationService.sanitizeText(dto.getNickname()));
        student.setSchoolName(dto.getSchoolName());
        student.setGrade(grade);
        student.setPhoneStudent(validationService.sanitizePhoneNumber(dto.getPhoneStudent()));
        student.setPhoneParent(validationService.sanitizePhoneNumber(dto.getPhoneParent()));
        student.setStudyPlan(dto.getStudyPlan());
        student.setIsActive(true);
        student.setCreatedBy(creator);

        Student saved = studentRepository.save(student);
        return StudentDto.fromEntity(saved);
    }

    @Cacheable(key = "'all-active'")
    public List<StudentDto> getAllActiveStudents() {
        return studentRepository.findByIsActiveTrueOrderByFirstNameAsc().stream()
                .map(StudentDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Cacheable(key = "'search-' + #search")
    public List<StudentDto> searchStudents(String search) {
        if (search == null || search.trim().isEmpty()) {
            return getAllActiveStudents();
        }
        return studentRepository.searchActiveStudents(search.trim()).stream()
                .map(StudentDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Cacheable(key = "'grade-' + #gradeId")
    public List<StudentDto> getStudentsByGrade(Long gradeId) {
        return studentRepository.findByGradeIdAndActiveTrue(gradeId).stream()
                .map(StudentDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Cacheable(key = "#id")
    public StudentDto getStudentById(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Student not found: " + id));
        return StudentDto.fromEntity(student);
    }

    @Transactional
    @CacheEvict(allEntries = true)
    public StudentDto updateStudent(Long id, StudentDto dto) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Student not found: " + id));

        User updatedBy = getCurrentUser();
        updateBasicFields(student, dto, updatedBy);

        if (dto.getGradeId() != null && !student.getGrade().getId().equals(dto.getGradeId())) {
            updateGrade(student, dto.getGradeId(), updatedBy);
        }

        studentRepository.save(student);
        return StudentDto.fromEntity(student);
    }

    private void updateBasicFields(Student student, StudentDto dto, User updatedBy) {
        updateFieldIfChanged(student, "firstName", student.getFirstName(),
                dto.getFirstName(), updatedBy,
                value -> student.setFirstName(validationService.sanitizeText(value)));

        updateFieldIfChanged(student, "lastName", student.getLastName(),
                dto.getLastName(), updatedBy,
                value -> student.setLastName(validationService.sanitizeText(value)));

        updateFieldIfChanged(student, "nickname", student.getNickname(),
                dto.getNickname(), updatedBy,
                value -> student.setNickname(validationService.sanitizeText(value)));

        updateFieldIfChanged(student, "schoolName", student.getSchoolName(),
                dto.getSchoolName(), updatedBy,
                student::setSchoolName);

        updateFieldIfChanged(student, "phoneStudent", student.getPhoneStudent(),
                dto.getPhoneStudent(), updatedBy,
                value -> student.setPhoneStudent(validationService.sanitizePhoneNumber(value)));

        updateFieldIfChanged(student, "phoneParent", student.getPhoneParent(),
                dto.getPhoneParent(), updatedBy,
                value -> student.setPhoneParent(validationService.sanitizePhoneNumber(value)));

        updateFieldIfChanged(student, "studyPlan", student.getStudyPlan(),
                dto.getStudyPlan(), updatedBy,
                student::setStudyPlan);

        if (dto.getIsActive() != null) {
            student.setIsActive(dto.getIsActive());
        }
    }

    private <T> void updateFieldIfChanged(Student student, String fieldName,
                                          T oldValue, T newValue, User updatedBy,
                                          java.util.function.Consumer<T> setter) {
        if (newValue != null && !Objects.equals(oldValue, newValue)) {
            String oldValStr = oldValue != null ? oldValue.toString() : null;
            String newValStr = newValue != null ? newValue.toString() : null;
            historyService.trackFieldChange(student, fieldName, oldValStr, newValStr, updatedBy);
            setter.accept(newValue);
        }
    }

    private void updateGrade(Student student, Long newGradeId, User updatedBy) {
        Grade newGrade = gradeRepository.findById(newGradeId)
                .orElseThrow(() -> new IllegalArgumentException("Grade not found: " + newGradeId));

        historyService.trackFieldChange(student, "grade",
                student.getGrade().getGradeName(),
                newGrade.getGradeName(), updatedBy);

        student.setGrade(newGrade);
    }

    @Transactional
    @CacheEvict(allEntries = true)
    public void deactivateStudent(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Student not found: " + id));
        student.setIsActive(false);
        studentRepository.save(student);
    }

    @Transactional
    @CacheEvict(allEntries = true)
    public void activateStudent(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Student not found: " + id));
        student.setIsActive(true);
        studentRepository.save(student);
    }

    @Transactional
    @CacheEvict(allEntries = true)
    public void deleteStudent(Long id) {
        historyService.deleteHistoryByStudent(id);
        studentRepository.deleteById(id);
    }

    public List<StudentHistoryDto> getStudentHistory(Long studentId) {
        return historyService.getStudentHistory(studentId);
    }

    @Cacheable(key = "'grade-search-' + #gradeId + '-' + #search")
    public List<StudentDto> searchStudentsByGrade(Long gradeId, String search) {
        List<Student> students;
        if (search == null || search.trim().isEmpty()) {
            students = studentRepository.findByGradeIdAndActiveTrue(gradeId);
        } else {
            students = studentRepository.searchActiveStudentsByGrade(gradeId, search.trim());
        }
        return students.stream()
                .map(StudentDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Cacheable(key = "'student-with-classes-' + #studentId")
    public StudentDetailDto getStudentWithClasses(Long studentId, Long gradeId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found: " + studentId));

        if (gradeId != null && !student.getGrade().getId().equals(gradeId)) {
            throw new IllegalArgumentException("Student does not belong to grade: " + gradeId);
        }

        List<MonthlyEnrollment> monthlyEnrollments = monthlyEnrollmentRepository.findByStudentId(studentId);
        List<HourlyGroupEnrollment> hourlyGroupEnrollments = hourlyGroupEnrollmentRepository.findByStudentId(studentId);
        List<HourlyIndividualClass> hourlyIndividualClasses = hourlyIndividualClassStudentRepository.findByStudentIdViaJunction(studentId);
        List<StudentCoursePurchase> purchases = coursePurchaseRepository.findByStudentIdAndIsActiveTrue(studentId);

        StudentDetailDto dto = StudentDetailDto.fromEntity(student);

        dto.setMonthlyEnrollments(monthlyEnrollments.stream()
                .map(this::convertMonthlyEnrollmentToDto)
                .collect(Collectors.toList()));

        dto.setHourlyGroupEnrollments(hourlyGroupEnrollments.stream()
                .map(this::convertHourlyGroupEnrollmentToDto)
                .collect(Collectors.toList()));

        dto.setHourlyIndividualClasses(hourlyIndividualClasses.stream()
                .map(cls -> convertHourlyIndividualClassToDto(cls, studentId))
                .collect(Collectors.toList()));

        dto.setCoursePurchases(purchases.stream()
                .map(this::convertPurchaseToDto)
                .collect(Collectors.toList()));

        return dto;
    }

    private StudentDetailDto.MonthlyClassInfo convertMonthlyEnrollmentToDto(MonthlyEnrollment enrollment) {
        MonthlyClass clazz = enrollment.getMonthlyClass();

        // ดึงครูทั้งหมด แล้วเอาคนแรก (หรือรวมชื่อทั้งหมด)
        String tutorName = null;
        if (clazz.getClassTutors() != null && !clazz.getClassTutors().isEmpty()) {
            // วิธีที่ 1: เอาครูคนแรก
            tutorName = clazz.getClassTutors().stream()
                    .findFirst()
                    .map(ct -> ct.getTutor().getNickname())
                    .orElse(null);

            // วิธีที่ 2: รวมชื่อครูทั้งหมด
            // tutorName = clazz.getClassTutors().stream()
            //         .map(ct -> ct.getTutor().getNickname())
            //         .collect(Collectors.joining(", "));
        }

        return new StudentDetailDto.MonthlyClassInfo(
                enrollment.getId(),
                clazz.getClassName(),
                tutorName,
                clazz.getSubject() != null ? clazz.getSubject().getId() : null,
                clazz.getSubject() != null ? clazz.getSubject().getSubjectName() : null,
                clazz.getSubtype() != null ? clazz.getSubtype().getSubtypeName() : null,
                clazz.getStartDate(),
                clazz.getEndDate(),
                enrollment.getIsActive()
        );
    }

    private StudentDetailDto.HourlyGroupClassInfo convertHourlyGroupEnrollmentToDto(HourlyGroupEnrollment enrollment) {
        HourlyGroupClass clazz = enrollment.getHourlyGroupClass();

        // ดึงชื่อครูทั้งหมด
        List<String> tutorNicknames = null;
        String firstTutorNickname = null;

        if (clazz.getClassTutors() != null && !clazz.getClassTutors().isEmpty()) {
            tutorNicknames = clazz.getClassTutors().stream()
                    .map(ct -> ct.getTutor().getNickname())
                    .collect(Collectors.toList());
            firstTutorNickname = tutorNicknames.get(0);
        }

        // คำนวณชั่วโมงเฉพาะนักเรียนคนนี้ (จาก student_course_purchases)
        BigDecimal hoursCompleted = BigDecimal.ZERO;
        BigDecimal hoursTarget = null;
        BigDecimal completionPercentage = null;

        if (clazz.getSubject() != null && enrollment.getIsActive()) {
            List<StudentCoursePurchase> purchases = coursePurchaseRepository
                    .findByStudentIdAndSubjectIdAndClassTypeAndIsActiveTrue(
                            enrollment.getStudent().getId(),
                            clazz.getSubject().getId(),
                            "hourly_group"
                    );

            if (!purchases.isEmpty()) {  // เช็คว่ามีการซื้อคอร์สหรือไม่
                // รวมชั่วโมงที่ซื้อมา
                hoursTarget = purchases.stream()
                        .map(StudentCoursePurchase::getHoursPurchased)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                // รวมชั่วโมงที่เรียนไปแล้ว
                hoursCompleted = purchases.stream()
                        .map(StudentCoursePurchase::getHoursCompleted)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                // คำนวณเปอร์เซ็นต์
                if (hoursTarget.compareTo(BigDecimal.ZERO) > 0) {
                    completionPercentage = hoursCompleted
                            .divide(hoursTarget, 4, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100));
                }
            }
        }

        return new StudentDetailDto.HourlyGroupClassInfo(
                enrollment.getId(),
                clazz.getClassName(),
                firstTutorNickname,
                tutorNicknames,
                clazz.getSubject() != null ? clazz.getSubject().getId() : null,
                clazz.getSubject() != null ? clazz.getSubject().getSubjectName() : null,
                clazz.getSubtype() != null ? clazz.getSubtype().getSubtypeName() : null,
                hoursTarget != null ? hoursTarget.doubleValue() : null,
                hoursCompleted.doubleValue(),
                completionPercentage != null ? completionPercentage.doubleValue() : null,
                null,
                enrollment.getIsActive()
        );
    }

    private StudentDetailDto.HourlyIndividualClassInfo convertHourlyIndividualClassToDto(
            HourlyIndividualClass cls,
            Long currentStudentId) {  // ← เพิ่ม parameter

        BigDecimal hoursCompleted = BigDecimal.ZERO;
        BigDecimal hoursTarget = null;

        Long totalStudents = cls.getTotalStudentsEverEnrolled();
        boolean isSingleStudent = totalStudents == 1;

        Boolean isStudentActive = cls.getClassStudents() != null
                ? cls.getClassStudents().stream()
                .filter(cs -> cs.getStudent().getId().equals(currentStudentId))
                .findFirst()
                .map(HourlyIndividualClassStudent::getIsActive)
                .orElse(false)
                : false;

        if (cls.getSubject() != null && cls.getClassStudents() != null && !cls.getClassStudents().isEmpty()) {
            Student firstStudent = cls.getClassStudents().stream()
                    .filter(cs -> cs.getStudent().getId().equals(currentStudentId))
                    .findFirst()
                    .map(HourlyIndividualClassStudent::getStudent)
                    .orElse(null);

            if (firstStudent != null) {
                String targetClassType = isSingleStudent ? "hourly_individual" : "hourly_individual_group";

                List<StudentCoursePurchase> purchases = coursePurchaseRepository
                        .findByStudentIdAndSubjectIdAndClassTypeAndIsActiveTrue(
                                firstStudent.getId(),
                                cls.getSubject().getId(),
                                targetClassType
                        );

                if (!purchases.isEmpty()) {
                    hoursTarget = purchases.stream()
                            .map(StudentCoursePurchase::getHoursPurchased)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    hoursCompleted = purchases.stream()
                            .map(StudentCoursePurchase::getHoursCompleted)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                }
            }
        }

        Long enrollmentId = cls.getClassStudents() != null
                ? cls.getClassStudents().stream()
                .filter(cs -> cs.getStudent().getId().equals(currentStudentId))
                .findFirst()
                .map(HourlyIndividualClassStudent::getId)
                .orElse(null)
                : null;

        return new StudentDetailDto.HourlyIndividualClassInfo(
                cls.getId(),
                cls.getClassName(),
                cls.getSubject() != null ? cls.getSubject().getId() : null,
                cls.getSubject() != null ? cls.getSubject().getSubjectName() : null,
                hoursTarget != null ? hoursTarget.doubleValue() : null,
                hoursCompleted != null ? hoursCompleted.doubleValue() : 0.0,
                isStudentActive,
                totalStudents,
                enrollmentId
        );
    }

    @Cacheable(key = "'student-hours-summary-' + #studentId")
    public Map<String, Object> getStudentHoursSummary(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found: " + studentId));

        Map<String, Object> summary = new HashMap<>();

        List<HourlyGroupEnrollment> groupEnrollments = hourlyGroupEnrollmentRepository.findByStudentId(studentId);
        List<HourlyIndividualClass> individualClasses = hourlyIndividualClassRepository.findByStudentId(studentId);

        BigDecimal totalIndividualCompleted = calculateIndividualHoursFromForms(studentId);

        BigDecimal completionPercentage = BigDecimal.ZERO;

        summary.put("studentId", studentId);
        summary.put("studentName", student.getFirstName() + " " + student.getLastName());
        summary.put("nickname", student.getNickname());
        summary.put("totalIndividualHours", totalIndividualCompleted.doubleValue());
        summary.put("completionPercentage", completionPercentage.doubleValue());
        summary.put("activeGroupClasses", groupEnrollments.stream().filter(HourlyGroupEnrollment::getIsActive).count());
        summary.put("activeIndividualClasses", individualClasses.stream().filter(HourlyIndividualClass::getIsActive).count());

        return summary;
    }

    private BigDecimal calculateIndividualHoursFromForms(Long studentId) {
        List<HourlyIndividualClass> individualClasses = hourlyIndividualClassRepository
                .findByStudentId(studentId);

        BigDecimal totalHours = BigDecimal.ZERO;

        for (HourlyIndividualClass clazz : individualClasses) {
            if (clazz.getIsActive()) {
                BigDecimal classHours = hourFormRepository
                        .sumHoursByClassTypeAndClassId("hourly_individual", clazz.getId());

                if (classHours != null) {
                    totalHours = totalHours.add(classHours);
                }
            }
        }

        return totalHours;
    }

    @Transactional
    @CacheEvict(allEntries = true)
    public StudentDto updateStudentWithClasses(Long id, StudentRequestDto dto, String username) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Student not found: " + id));

        User updatedBy = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("User not found: " + username));

        updateBasicFields(student, convertToBasicDto(dto), updatedBy);

        if (dto.getGradeId() != null && !student.getGrade().getId().equals(dto.getGradeId())) {
            updateGrade(student, dto.getGradeId(), updatedBy);
        }

        studentRepository.save(student);

        if (dto.getClasses() != null && !dto.getClasses().isEmpty()) {
            manageStudentClasses(student, dto.getClasses());
        }

        return StudentDto.fromEntity(student);
    }

    private void manageStudentClasses(Student student, List<StudentRequestDto.ClassEnrollmentDto> classDtos) {
        Grade currentGrade = student.getGrade();
        User currentUser = getCurrentUser();

        List<MonthlyEnrollment> existingMonthly = monthlyEnrollmentRepository.findByStudentId(student.getId());
        List<HourlyGroupEnrollment> existingHourlyGroup = hourlyGroupEnrollmentRepository.findByStudentId(student.getId());
        List<HourlyIndividualClass> existingHourlyIndividual = hourlyIndividualClassRepository.findByStudentId(student.getId());

        Set<Long> keepMonthlyIds = new HashSet<>();
        Set<Long> keepHourlyGroupIds = new HashSet<>();
        Set<Long> keepHourlyIndividualIds = new HashSet<>();

        // Track new classes
        for (StudentRequestDto.ClassEnrollmentDto classDto : classDtos) {
            if (classDto.getId() != null) {
                if ("MONTH".equals(classDto.getClassType())) {
                    keepMonthlyIds.add(classDto.getId());
                } else if ("HOUR".equals(classDto.getClassType())) {
                    if ("GROUP".equals(classDto.getMode())) {
                        keepHourlyGroupIds.add(classDto.getId());
                    } else {
                        keepHourlyIndividualIds.add(classDto.getId());
                    }
                }
            } else {
                String classType = "MONTH".equals(classDto.getClassType()) ? "คลาสรายเดือน" : "คลาสรายชั่วโมง";
                String mode = "GROUP".equals(classDto.getMode()) ? "กลุ่ม" : "เดี่ยว";
                String subjectName = getSubjectNameById(classDto.getSubjectId());

                enrollStudentToClass(student, classDto, currentGrade);

                // Track ว่าเพิ่มคลาสอะไร
                historyService.trackClassAdded(
                        student,
                        classType + " (" + mode + ")",
                        classDto.getSchedule() != null ? classDto.getSchedule() : classDto.getGroupType(),
                        subjectName,
                        currentUser
                );
            }
        }

        // Track removed monthly classes
        for (MonthlyEnrollment enrollment : existingMonthly) {
            if (!keepMonthlyIds.contains(enrollment.getId()) && enrollment.getIsActive()) {
                enrollment.setIsActive(false);
                monthlyEnrollmentRepository.save(enrollment);

                // Track removal
                MonthlyClass clazz = enrollment.getMonthlyClass();
                historyService.trackClassRemoved(
                        student,
                        "คลาสรายเดือน",
                        clazz.getClassName(),
                        clazz.getSubject() != null ? clazz.getSubject().getSubjectName() : "",
                        currentUser
                );
            }
        }

        // Track removed hourly group classes
        for (HourlyGroupEnrollment enrollment : existingHourlyGroup) {
            if (!keepHourlyGroupIds.contains(enrollment.getId()) && enrollment.getIsActive()) {
                enrollment.setIsActive(false);
                hourlyGroupEnrollmentRepository.save(enrollment);

                // Track removal
                HourlyGroupClass clazz = enrollment.getHourlyGroupClass();
                historyService.trackClassRemoved(
                        student,
                        "คลาสรายชั่วโมง (กลุ่ม)",
                        clazz.getClassName(),
                        clazz.getSubject() != null ? clazz.getSubject().getSubjectName() : "",
                        currentUser
                );
            }
        }

        // Track removed hourly individual classes
        for (HourlyIndividualClass individualClass : existingHourlyIndividual) {
            if (!keepHourlyIndividualIds.contains(individualClass.getId()) && individualClass.getIsActive()) {
                individualClass.setIsActive(false);
                hourlyIndividualClassRepository.save(individualClass);

                // Track removal
                historyService.trackClassRemoved(
                        student,
                        "คลาสรายชั่วโมง (เดี่ยว)",
                        individualClass.getClassName(),
                        "", // Individual class ไม่มี subject
                        currentUser
                );
            }
        }
    }

    // Helper method ดึงชื่อวิชา
    private String getSubjectNameById(Long subjectId) {
        if (subjectId == null) return "";

        // ใช้ repository ดึงชื่อวิชา (ต้อง inject SubjectRepository)
        return subjectRepository.findById(subjectId)
                .map(subject -> subject.getSubjectName())
                .orElse("");
    }

    /**
     * แปลง schedule code (MON-FRI) เป็นชื่อภาษาไทย (จันทร์-ศุกร์)
     */
    private String convertScheduleCodeToThaiName(String scheduleCode) {
        Map<String, String> codeToThaiMapping = new HashMap<>();
        codeToThaiMapping.put("MON-FRI", "จันทร์-ศุกร์");
        codeToThaiMapping.put("TUE-THU", "อังคาร-พฤหัส");
        codeToThaiMapping.put("SAT", "เสาร์");
        codeToThaiMapping.put("SUN", "อาทิตย์");

        return codeToThaiMapping.getOrDefault(scheduleCode, scheduleCode);
    }

    /**
     * แปลง group type code (A, B, C) เป็นชื่อภาษาไทย (กลุ่ม A)
     */
    private String convertGroupTypeCodeToThaiName(String groupTypeCode) {
        Map<String, String> codeToThaiMapping = new HashMap<>();
        codeToThaiMapping.put("A", "กลุ่ม A");
        codeToThaiMapping.put("B", "กลุ่ม B");
        codeToThaiMapping.put("C", "กลุ่ม C");
        codeToThaiMapping.put("PV", "กลุ่ม PV");

        return codeToThaiMapping.getOrDefault(groupTypeCode, groupTypeCode);
    }

    // ===== CREATE WITH COURSE PURCHASES (แทน createStudentWithClasses) =====

    @Transactional
    @CacheEvict(allEntries = true)
    public StudentDto createStudentWithCoursePurchases(StudentRequestDto dto, String username) {
        var validation = validationService.validateStudent(convertToBasicDto(dto), false);
        if (!validation.isValid()) {
            throw new IllegalArgumentException(validation.getErrorMessage());
        }

        Grade grade = gradeRepository.findById(dto.getGradeId())
                .orElseThrow(() -> new IllegalArgumentException("Grade not found: " + dto.getGradeId()));

        User creator = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("User not found: " + username));

        String studentCode = Optional.ofNullable(dto.getStudentCode())
                .filter(code -> !code.trim().isEmpty())
                .orElseGet(() -> codeGenerator.generateSequentialCode(grade));

        Student student = new Student();
        student.setStudentCode(studentCode);
        student.setFirstName(validationService.sanitizeText(dto.getFirstName()));
        student.setLastName(validationService.sanitizeText(dto.getLastName()));
        student.setNickname(validationService.sanitizeText(dto.getNickname()));
        student.setSchoolName(dto.getSchoolName());
        student.setGrade(grade);
        student.setPhoneStudent(validationService.sanitizePhoneNumber(dto.getPhoneStudent()));
        student.setPhoneParent(validationService.sanitizePhoneNumber(dto.getPhoneParent()));
        student.setStudyPlan(dto.getStudyPlan());
        student.setIsActive(true);
        student.setCreatedBy(creator);

        Student savedStudent = studentRepository.save(student);

        // บันทึกข้อมูลการซื้อคอร์ส (ถ้ามี)
        if (dto.getCoursePurchases() != null && !dto.getCoursePurchases().isEmpty()) {
            for (StudentRequestDto.CoursePurchaseDto purchaseDto : dto.getCoursePurchases()) {
                createCoursePurchase(savedStudent, purchaseDto, creator);
            }

            coursePurchaseRepository.flush();
        }

        return StudentDto.fromEntity(savedStudent);
    }

    /**
     * บันทึกข้อมูลการซื้อคอร์ส
     */
    private void createCoursePurchase(Student student, StudentRequestDto.CoursePurchaseDto dto, User creator) {
        // Validate subject exists
        var subject = subjectRepository.findById(dto.getSubjectId())
                .orElseThrow(() -> new IllegalArgumentException("ไม่พบวิชา ID: " + dto.getSubjectId()));

        // แปลง classType เป็น format ของ database (ถ้ามี)
        String dbClassType = dto.getClassType();
        MonthlySubtype monthlySubtype = null;
        if (dbClassType != null && !dbClassType.trim().isEmpty()) {
            dbClassType = normalizeClassTypeForDatabase(dbClassType);

            monthlySubtype = null;
            if ("monthly".equals(dbClassType) && dto.getMonthlySubtypeId() != null) {
                monthlySubtype = monthlySubtypeRepository.findById(dto.getMonthlySubtypeId())
                        .orElseThrow(() -> new IllegalArgumentException(
                                "ไม่พบประเภทคลาสรายเดือน ID: " + dto.getMonthlySubtypeId()
                        ));
            }
        }

        // Create purchase record
        StudentCoursePurchase purchase = new StudentCoursePurchase();
        purchase.setStudent(student);
        purchase.setSubject(subject);

        if (dto.getHoursPurchased() != null) {
            purchase.setHoursPurchased(BigDecimal.valueOf(dto.getHoursPurchased()));
        } else {
            purchase.setHoursPurchased(null);
        }

        purchase.setHoursCompleted(BigDecimal.ZERO);
        purchase.setPurchaseDate(LocalDate.now());
        purchase.setClassType(dbClassType);
        purchase.setMonthlySubtype(monthlySubtype);
        purchase.setIsActive(true);
        purchase.setCreatedBy(creator);

        coursePurchaseRepository.save(purchase);

        // Track history
        String classTypeLabel = "";
        if (dbClassType != null) {
            switch (dbClassType) {
                case "hourly_group":
                    classTypeLabel = " (แบบกลุ่ม)";
                    break;
                case "hourly_individual":
                    classTypeLabel = " (แบบเดี่ยว)";
                    break;
                case "hourly_individual_group":
                    classTypeLabel = " (แบบเดี่ยวแบบกลุ่ม)";
                    break;
            }
        }

        historyService.trackCoursePurchaseAdded(
                student,
                subject.getSubjectName(),
                dto.getHoursPurchased(),
                dto.getClassType(),
                monthlySubtype != null ? monthlySubtype.getSubtypeName() : null,
                creator
        );
    }

    // ===== UPDATE WITH COURSE PURCHASES (แทน updateStudentWithClasses) =====

    @Transactional
    @CacheEvict(allEntries = true)
    public StudentDto updateStudentWithCoursePurchases(Long id, StudentRequestDto dto, String username) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Student not found: " + id));

        User updatedBy = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("User not found: " + username));

        // Update basic fields
        updateBasicFields(student, convertToBasicDto(dto), updatedBy);

        // Update grade if changed
        if (dto.getGradeId() != null && !student.getGrade().getId().equals(dto.getGradeId())) {
            updateGrade(student, dto.getGradeId(), updatedBy);
        }

        studentRepository.save(student);

        if (dto.getCoursePurchases() != null && !dto.getCoursePurchases().isEmpty()) {
            manageCoursePurchases(student, dto.getCoursePurchases(), updatedBy);
        }

        if (dto.getUpdatedCoursePurchases() != null && !dto.getUpdatedCoursePurchases().isEmpty()) {
            for (StudentRequestDto.CoursePurchaseDto updateDto : dto.getUpdatedCoursePurchases()) {
                updateCoursePurchase(student, updateDto, updatedBy);
            }
        }

        if (dto.getDeletedCoursePurchaseIds() != null && !dto.getDeletedCoursePurchaseIds().isEmpty()) {
            for (Long purchaseId : dto.getDeletedCoursePurchaseIds()) {
                // ดึง deletedReason จาก DTO (ถ้ามี)
                String deletedReason = dto.getCoursePurchases().stream()
                        .filter(cp -> cp.getId() != null && cp.getId().equals(purchaseId))
                        .findFirst()
                        .map(StudentRequestDto.CoursePurchaseDto::getDeletedReason)
                        .orElse("ไม่ระบุเหตุผล");

                deleteCoursePurchase(student, purchaseId, deletedReason, updatedBy);
            }
        }

        // Manage course purchases
        if (dto.getCoursePurchases() != null && !dto.getCoursePurchases().isEmpty()) {
            manageCoursePurchases(student, dto.getCoursePurchases(), updatedBy);
        }

        return StudentDto.fromEntity(student);
    }

    /**
     * จัดการข้อมูลการซื้อคอร์ส (เพิ่ม/แก้ไข/ลบ)
     */
    private void manageCoursePurchases(Student student,
                                       List<StudentRequestDto.CoursePurchaseDto> purchaseDtos,
                                       User updatedBy) {
        // ดึงข้อมูลการซื้อคอร์สที่มีอยู่
        List<StudentCoursePurchase> existingPurchases =
                coursePurchaseRepository.findByStudentIdAndIsActiveTrue(student.getId());

        Set<Long> keepPurchaseIds = new HashSet<>();

        // ตรวจสอบ duplicate ทั้งใน purchaseDtos และ existingPurchases
        Map<String, Object> purchaseMap = new HashMap<>();

        // เพิ่มคอร์สที่มีอยู่แล้วลงใน map และเก็บ ID ไว้
        // เก็บ ID ของคอร์สเก่าไว้
        Set<Long> existingPurchaseIds = new HashSet<>();
        for (StudentCoursePurchase existing : existingPurchases) {
            existingPurchaseIds.add(existing.getId());
        }

        for (StudentRequestDto.CoursePurchaseDto dto : purchaseDtos) {
            String normalizedClassType = normalizeClassTypeForDatabase(dto.getClassType());
            String key = dto.getSubjectId() + "-" + normalizedClassType;

            if (dto.getId() != null) {
                keepPurchaseIds.add(dto.getId());
                purchaseMap.put(key, dto);
            } else {
                // เช็คว่า key นี้มีใน existingPurchases หรือยัง
                boolean existsInDb = existingPurchases.stream()
                        .anyMatch(existing -> {
                            String existingKey = existing.getSubject().getId() + "-" + existing.getClassType();
                            return existingKey.equals(key);
                        });

                if (purchaseMap.containsKey(key)) {
                    throw new IllegalArgumentException("มีคอร์สซ้ำกันในรายการที่ส่งมา");
                } else if (!existsInDb) {  // สร้างใหม่ถ้ายังไม่มีใน DB
                    createCoursePurchase(student, dto, updatedBy);
                    purchaseMap.put(key, dto);
                } else {
                    // มีอยู่แล้วใน DB แต่ไม่ได้ส่ง ID มา ให้หา ID แล้วเพิ่มเข้า keepPurchaseIds
                    Optional<StudentCoursePurchase> existing = existingPurchases.stream()
                            .filter(e -> {
                                String existingKey = e.getSubject().getId() + "-" + e.getClassType();
                                return existingKey.equals(key);
                            })
                            .findFirst();

                    if (existing.isPresent()) {
                        keepPurchaseIds.add(existing.get().getId());
                        purchaseMap.put(key, dto);
                    }
                }
            }
        }
    }

    // ===== GET STUDENT WITH COURSE PURCHASES =====

    @Cacheable(key = "'student-with-purchases-' + #studentId")
    public StudentDetailDto getStudentWithCoursePurchases(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found: " + studentId));

        List<StudentCoursePurchase> purchases =
                coursePurchaseRepository.findByStudentIdAndIsActiveTrue(studentId);

        StudentDetailDto dto = StudentDetailDto.fromEntity(student);

        dto.setCoursePurchases(purchases.stream()
                .map(this::convertPurchaseToDto)
                .collect(Collectors.toList()));

        return dto;
    }

    private StudentDetailDto.CoursePurchaseInfo convertPurchaseToDto(StudentCoursePurchase purchase) {
        StudentDetailDto.CoursePurchaseInfo dto = new StudentDetailDto.CoursePurchaseInfo(
                purchase.getId(),
                purchase.getStudent().getId(),
                purchase.getSubject().getId(),
                purchase.getSubject().getSubjectName(),
                purchase.getHoursPurchased() != null ? purchase.getHoursPurchased().doubleValue() : null,
                purchase.getHoursCompleted().doubleValue(),
                purchase.getPurchaseDate(),
                purchase.getIsActive(),
                purchase.getClassType(),
                purchase.getMonthlySubtype() != null ? purchase.getMonthlySubtype().getId() : null,
                purchase.getMonthlySubtype() != null ? purchase.getMonthlySubtype().getSubtypeName() : null,
                purchase.getDeletedReason()
        );

        // เพิ่ม editReason (ถ้ามีใน entity)
        dto.setEditReason(purchase.getEditReason());

        return dto;
    }

    /**
     * เช็คว่านักเรียนซื้อชั่วโมงในวิชานี้หรือยัง (สำหรับ Hourly Class)
     */
    private void validateStudentHasCoursePurchase(Student student, Long subjectId, String classType) {
        // แปลง classType เป็น format ของ database
        String dbClassType = normalizeClassTypeForDatabase(classType);

        List<StudentCoursePurchase> purchases = coursePurchaseRepository
                .findByStudentIdAndSubjectIdAndClassTypeAndIsActiveTrue(
                        student.getId(),
                        subjectId,
                        dbClassType  // ใช้ค่าที่แปลงแล้ว
                );

        if (purchases.isEmpty()) {
            var subject = subjectRepository.findById(subjectId)
                    .orElseThrow(() -> new IllegalArgumentException("ไม่พบวิชา"));

            // แสดง error message ที่เป็นมิตร
            String classTypeName;
            switch (dbClassType) {
                case "hourly_group":
                    classTypeName = "แบบกลุ่ม";
                    break;
                case "hourly_individual":
                    classTypeName = "แบบเดี่ยว";
                    break;
                case "hourly_individual_group":
                    classTypeName = "แบบเดี่ยวแบบกลุ่ม";
                    break;
                default:
                    classTypeName = "";
            }

            throw new IllegalArgumentException(
                    "นักเรียน " + student.getNickname() +
                            " ยังไม่ซื้อคอร์สรายชั่วโมงวิชา" + subject.getSubjectName() +
                            " " + classTypeName + "\nกรุณาไปหน้าจัดการข้อมูลนักเรียนเพื่อเพิ่มคอร์สก่อน"
            );
        }
    }

    /**
     * แปลงค่า classType จากรูปแบบต่าง ๆ เป็น format ของ database
     * รองรับทั้ง "GROUP", "INDIVIDUAL", "hourly_group", "hourly_individual"
     */
    private String normalizeClassTypeForDatabase(String classType) {
        if (classType == null || classType.trim().isEmpty()) {
            return null;
        }

        String normalized = classType.trim().toUpperCase();

        // ถ้าส่งมาเป็น format ของ database อยู่แล้ว (hourly_group, hourly_individual)
        if (classType.contains("_")) {
            return classType.toLowerCase();
        }

        // แปลงจาก format แบบสั้น (GROUP, INDIVIDUAL, PRIVATE)
        switch (normalized) {
            case "GROUP":
                return "hourly_group";
            case "INDIVIDUAL":
            case "PRIVATE":
                return "hourly_individual";
            case "INDIVIDUAL_GROUP":
            case "PRIVATE_GROUP":
                return "hourly_individual_group";
            default:
                // ถ้าไม่ตรงกับอะไรเลย ให้ใช้ค่าเดิม
                return classType.toLowerCase();
        }
    }

    private void updateCoursePurchase(Student student,
                                      StudentRequestDto.CoursePurchaseDto dto,
                                      User updatedBy) {
        StudentCoursePurchase purchase = coursePurchaseRepository.findById(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException("ไม่พบคอร์ส ID: " + dto.getId()));

        // ตรวจสอบว่าเป็นของนักเรียนคนนี้จริง
        if (!purchase.getStudent().getId().equals(student.getId())) {
            throw new IllegalArgumentException("คอร์สนี้ไม่ใช่ของนักเรียนคนนี้");
        }

        // ถ้ามี hoursCompleted > 0 ห้ามเปลี่ยน classType
        if (purchase.getHoursCompleted().compareTo(BigDecimal.ZERO) > 0) {
            String oldClassType = purchase.getClassType();
            String newClassType = normalizeClassTypeForDatabase(dto.getClassType());

            if (!oldClassType.equals(newClassType)) {
                throw new IllegalArgumentException(
                        "ไม่สามารถเปลี่ยนประเภทคลาสได้ เนื่องจากมีการเรียนไปแล้ว"
                );
            }
        }

        // บันทึกค่าเก่าไว้สำหรับ history
        String oldSubject = purchase.getSubject().getSubjectName();
        Double oldHours = purchase.getHoursPurchased() != null ? purchase.getHoursPurchased().doubleValue() : null;
        String oldClassType = purchase.getClassType();
        String oldEditReason = purchase.getEditReason();

        // Update fields
        if (dto.getSubjectId() != null && !dto.getSubjectId().equals(purchase.getSubject().getId())) {
            var newSubject = subjectRepository.findById(dto.getSubjectId())
                    .orElseThrow(() -> new IllegalArgumentException("ไม่พบวิชา"));
            purchase.setSubject(newSubject);
        }

        if (dto.getHoursPurchased() != null) {
            purchase.setHoursPurchased(BigDecimal.valueOf(dto.getHoursPurchased()));
        }

        if (dto.getClassType() != null) {
            purchase.setClassType(normalizeClassTypeForDatabase(dto.getClassType()));
        }

        if (dto.getEditReason() != null && !dto.getEditReason().trim().isEmpty()) {
            purchase.setEditReason(dto.getEditReason());
        }

        if (dto.getMonthlySubtypeId() != null) {
            MonthlySubtype monthlySubtype = monthlySubtypeRepository.findById(dto.getMonthlySubtypeId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "ไม่พบประเภทคลาสรายเดือน ID: " + dto.getMonthlySubtypeId()
                    ));
            purchase.setMonthlySubtype(monthlySubtype);
        }

        coursePurchaseRepository.save(purchase);

        // Track history
        String newSubject = purchase.getSubject().getSubjectName();
        Double newHours = purchase.getHoursPurchased() != null ? purchase.getHoursPurchased().doubleValue() : null;
        String newClassType = purchase.getClassType();

        historyService.trackCoursePurchaseUpdated(
                student,
                oldSubject, oldHours, oldClassType,
                newSubject, newHours, newClassType,
                oldEditReason,
                dto.getEditReason(),
                updatedBy
        );
    }

    private void deleteCoursePurchase(Student student, Long purchaseId, String deletedReason, User updatedBy) {
        StudentCoursePurchase purchase = coursePurchaseRepository.findById(purchaseId)
                .orElseThrow(() -> new IllegalArgumentException("ไม่พบคอร์ส ID: " + purchaseId));

        if (!purchase.getStudent().getId().equals(student.getId())) {
            throw new IllegalArgumentException("คอร์สนี้ไม่ใช่ของนักเรียนคนนี้");
        }

        // Soft delete with reason
        purchase.setIsActive(false);
        purchase.setDeletedReason(deletedReason);  // เพิ่ม
        coursePurchaseRepository.save(purchase);

        // Track history
        historyService.trackCoursePurchaseDeleted(
                student,
                purchase.getSubject().getSubjectName(),
                purchase.getHoursPurchased() != null ? purchase.getHoursPurchased().doubleValue() : 0.0,
                purchase.getClassType(),
                updatedBy
        );
    }

    @Transactional(readOnly = true)
    public List<HourFormDto> getStudentLearningHistory(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found: " + studentId));

        List<HourFormDto> allForms = new ArrayList<>();

        // 1. ดึง forms จากคลาส Hourly Group
        List<HourlyGroupEnrollment> groupEnrollments =
                hourlyGroupEnrollmentRepository.findByStudentId(studentId);

        for (HourlyGroupEnrollment enrollment : groupEnrollments) {
            List<HourForm> groupForms = hourFormRepository
                    .findByClassIdAndClassType(
                            enrollment.getHourlyGroupClass().getId(),
                            "hourly_group"
                    );

            for (HourForm form : groupForms) {
                // เช็คว่าฟอร์มนี้ถูกสร้างตอนที่นักเรียน active หรือไม่
                boolean wasActiveAtFormCreation = wasStudentActiveWhenFormCreated(
                        enrollment.getStudent().getId(),
                        form,
                        enrollment
                );

                // ถ้าไม่ active ตอนนั้น ไม่ต้องเพิ่มฟอร์มนี้
                if (!wasActiveAtFormCreation) {
                    continue;
                }

                HourFormDto dto = HourFormDto.fromEntity(form);
                dto.setId(form.getId());
                dto.setWasStudentActive(true); // ต้อง true แน่นอน เพราะผ่าน check ข้างบนมา

                String className = getClassName(form.getClassId(), form.getClassType());
                dto.setClassName(className);
                dto.setClassDisplayName(className);

                Optional<StudentHourFormOverride> override = overrideRepository
                        .findByHourFormIdAndStudentId(form.getId(), studentId);

                if (override.isPresent()) {
                    dto.setHoursTaught(override.get().getHoursOverride());
                    if (override.get().getRemarkOverride() != null) {
                        dto.setRemark(override.get().getRemarkOverride());
                    }
                }

                allForms.add(dto);
            }
        }

        // 2. ดึง forms จากคลาส Hourly Individual
        List<HourlyIndividualClass> individualClasses =
                hourlyIndividualClassStudentRepository.findByStudentIdViaJunction(studentId);

        for (HourlyIndividualClass cls : individualClasses) {
            List<HourForm> individualForms = hourFormRepository
                    .findByClassIdAndClassType(
                            cls.getId(),
                            "hourly_individual"
                    );

            for (HourForm form : individualForms) {
                // เช็คว่าฟอร์มนี้ถูกสร้างตอนที่นักเรียน active หรือไม่
                boolean wasActiveAtFormCreation = wasStudentActiveInIndividualClass(
                        studentId,
                        form,
                        cls
                );

                // ถ้าไม่ active ตอนนั้น ไม่ต้องเพิ่มฟอร์มนี้
                if (!wasActiveAtFormCreation) {
                    continue;
                }

                HourFormDto dto = HourFormDto.fromEntity(form);
                dto.setWasStudentActive(true); // ต้อง true แน่นอน

                String className = getClassName(form.getClassId(), form.getClassType());
                dto.setClassName(className);
                dto.setClassDisplayName(className);

                Optional<StudentHourFormOverride> override = overrideRepository
                        .findByHourFormIdAndStudentId(form.getId(), studentId);

                if (override.isPresent()) {
                    dto.setHoursTaught(override.get().getHoursOverride());
                    if (override.get().getRemarkOverride() != null) {
                        dto.setRemark(override.get().getRemarkOverride());
                    }
                }

                allForms.add(dto);
            }
        }

        // เรียงตามวันที่ล่าสุดก่อน
        allForms.sort((a, b) -> b.getTeachingDate().compareTo(a.getTeachingDate()));

        return allForms;
    }

    /**
     * เช็คว่าฟอร์มนี้ถูกบันทึกชั่วโมงให้นักเรียนหรือไม่
     * (ถ้าบันทึกแล้ว = ตอนนั้นนักเรียน active)
     */
    private boolean hasHoursRecordedForForm(Long studentId, HourForm form, String classType) {
        // ถ้าไม่มี subject หรือฟอร์มมี hours = 0 ถือว่าไม่ active
        if (form.getSubject() == null || form.getHoursTaught().compareTo(BigDecimal.ZERO) == 0) {
            return false;
        }

        String dbClassType = "hourly_group".equals(classType) ? "hourly_group" : "hourly_individual";

        // เช็คว่ามี course purchase ที่ active ตอนนั้นหรือไม่
        List<StudentCoursePurchase> purchases = coursePurchaseRepository
                .findByStudentIdAndSubjectIdAndClassTypeAndIsActiveTrue(
                        studentId,
                        form.getSubject().getId(),
                        dbClassType
                );

        // ถ้ามีการซื้อคอร์ส = แสดงว่าตอนนั้น active
        return !purchases.isEmpty();
    }

    /**
     * เช็คว่าตอน form ถูกสร้าง นักเรียน active ในคลาสนี้หรือไม่
     * โดยเช็คจาก student_course_purchases ว่ามี hours_completed > 0 หรือไม่
     */
    private boolean wasStudentActiveWhenFormCreated(Long studentId, HourForm form,
                                                    HourlyGroupEnrollment enrollment) {
        if (form.getSubject() == null || form.getHoursTaught().compareTo(BigDecimal.ZERO) == 0) {
            return false;
        }

        // เช็คว่ามี override = แสดงว่าตอนนั้นนักเรียน active และได้รับชั่วโมง
        Optional<StudentHourFormOverride> override = overrideRepository
                .findByHourFormIdAndStudentId(form.getId(), studentId);

        return override.isPresent();
    }

    /**
     * เช็คว่าตอน form ถูกสร้าง นักเรียน active ในคลาส Individual หรือไม่
     */
    private boolean wasStudentActiveInIndividualClass(Long studentId, HourForm form,
                                                      HourlyIndividualClass individualClass) {
        if (form.getSubject() == null || form.getHoursTaught().compareTo(BigDecimal.ZERO) == 0) {
            return false;
        }

        // เช็คว่ามี override = แสดงว่าตอนนั้นนักเรียน active และได้รับชั่วโมง
        Optional<StudentHourFormOverride> override = overrideRepository
                .findByHourFormIdAndStudentId(form.getId(), studentId);

        return override.isPresent();
    }

    /**
     * ดึงชื่อคลาสตาม ID และประเภท
     */
    private String getClassName(Long classId, String classType) {
        if ("hourly_group".equalsIgnoreCase(classType)) {
            return hourlyGroupClassRepository.findById(classId)
                    .map(HourlyGroupClass::getClassName)
                    .orElse("ไม่พบข้อมูลคลาส");
        } else if ("hourly_individual".equalsIgnoreCase(classType)) {
            return hourlyIndividualClassRepository.findById(classId)
                    .map(HourlyIndividualClass::getClassName)
                    .orElse("ไม่พบข้อมูลคลาส");
        }
        return "ไม่ทราบประเภทคลาส";
    }
}