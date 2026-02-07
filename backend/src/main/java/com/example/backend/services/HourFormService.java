package com.example.backend.services;

import com.example.backend.dtos.hourform.CreateHourFormRequestDto;
import com.example.backend.dtos.hourform.HourFormDto;
import com.example.backend.dtos.hourform.UpdateHourFormRequestDto;
import com.example.backend.entities.Subject;
import com.example.backend.entities.User;
import com.example.backend.entities.hourform.HourForm;
import com.example.backend.entities.hourform.StudentHourFormOverride;
import com.example.backend.entities.hourly.HourlyGroupClass;
import com.example.backend.entities.hourly.HourlyGroupEnrollment;
import com.example.backend.entities.hourly.HourlyIndividualClass;
import com.example.backend.entities.hourly.HourlyIndividualClassStudent;
import com.example.backend.entities.student.Student;
import com.example.backend.entities.student.StudentCoursePurchase;
import com.example.backend.repositories.SubjectRepository;
import com.example.backend.repositories.UserRepository;
import com.example.backend.repositories.hourform.HourFormRepository;
import com.example.backend.repositories.hourform.StudentHourFormOverrideRepository;
import com.example.backend.repositories.hourly.HourlyGroupClassRepository;
import com.example.backend.repositories.hourly.HourlyGroupEnrollmentRepository;
import com.example.backend.repositories.hourly.HourlyIndividualClassRepository;
import com.example.backend.repositories.hourly.HourlyIndividualClassStudentRepository;
import com.example.backend.repositories.student.StudentCoursePurchaseRepository;
import com.example.backend.repositories.student.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class HourFormService {

    @Autowired
    private HourFormRepository hourFormRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private HourlyGroupClassRepository groupClassRepository;

    @Autowired
    private HourlyIndividualClassRepository individualClassRepository;

    @Autowired
    private HourlyGroupEnrollmentRepository enrollmentRepository;

    @Autowired
    private StudentCoursePurchaseRepository coursePurchaseRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private HourlyIndividualClassStudentRepository hourlyIndividualClassStudentRepository;

    @Autowired
    private StudentHourFormOverrideRepository overrideRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private HourFormHistoryService historyService;

    @Autowired
    private StudentHourFormOverrideHistoryService studentOverrideHistoryService;

    /**
     * ดึง HourForm ทั้งหมด (มี filter)
     */
    @Transactional(readOnly = true)
    public List<HourFormDto> getAllHourForms(Long tutorId, Long classId, String classType) {
        List<HourForm> forms;

        if (tutorId != null && classId != null && classType != null) {
            forms = hourFormRepository.findByTutorIdAndClassIdAndClassType(tutorId, classId, classType);
        } else if (tutorId != null) {
            forms = hourFormRepository.findByTutorId(tutorId);
        } else if (classId != null && classType != null) {
            forms = hourFormRepository.findByClassIdAndClassType(classId, classType);
        } else {
            forms = hourFormRepository.findAll();
        }

        return forms.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * ดึง HourForm ของครูคนหนึ่ง
     */
    @Transactional(readOnly = true)
    public List<HourFormDto> getHourFormsByTutor(Long tutorId) {
        List<HourForm> forms = hourFormRepository.findByTutorId(tutorId);
        return forms.stream()
                .filter(f -> f.getIsAdminCreated() == null || !f.getIsAdminCreated())
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * ดึง HourForm ตาม ID
     */
    @Transactional(readOnly = true)
    public HourFormDto getHourFormById(Long id) {
        HourForm form = hourFormRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ไม่พบข้อมูลการสอน"));
        return convertToDto(form);
    }

    /**
     * สร้าง HourForm ใหม่
     */
    public HourFormDto createHourForm(CreateHourFormRequestDto request, Long tutorId) {
        // Validate tutor
        Long effectiveTutorId = request.getTutorId() != null ? request.getTutorId() : tutorId;
        User tutor = userRepository.findById(effectiveTutorId)
                .orElseThrow(() -> new RuntimeException("ไม่พบข้อมูลครู"));

        // Validate subject
        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new RuntimeException("ไม่พบข้อมูลวิชา"));

        // Validate class exists
        validateClassExists(request.getClassId(), request.getClassType());

        // Create new HourForm
        HourForm form = new HourForm();
        form.setTutor(tutor);
        form.setClassType(request.getClassType());
        form.setClassId(request.getClassId());
        form.setSubject(subject);
        form.setContent(request.getContent());
        form.setTeachingDate(request.getTeachingDate());
        form.setHoursTaught(request.getHoursTaught());
        form.setStudentsPresent(request.getStudentsPresent());
        form.setStudentsAbsent(request.getStudentsAbsent());
        form.setRemark(request.getRemark());

        HourForm saved = hourFormRepository.save(form);


        recordActiveStudentsForForm(saved);

        updateStudentHours(request.getClassId(), request.getClassType(), request.getHoursTaught());

        return convertToDto(saved);
    }

    /**
     * บันทึกรายชื่อนักเรียนที่ active ตอนฟอร์มถูกสร้าง
     */
    private void recordActiveStudentsForForm(HourForm form) {
        if ("hourly_group".equalsIgnoreCase(form.getClassType())) {
            List<HourlyGroupEnrollment> enrollments =
                    enrollmentRepository.findByClassIdAndIsActiveTrue(form.getClassId());

            for (HourlyGroupEnrollment enrollment : enrollments) {
                StudentHourFormOverride override = new StudentHourFormOverride();
                override.setHourForm(form);
                override.setStudent(enrollment.getStudent());
                override.setHoursOverride(form.getHoursTaught());
                overrideRepository.save(override);
            }

        } else if ("hourly_individual".equalsIgnoreCase(form.getClassType())) {
            List<HourlyIndividualClassStudent> activeEnrollments =
                    hourlyIndividualClassStudentRepository.findByClassIdAndActive(form.getClassId());

            for (HourlyIndividualClassStudent enrollment : activeEnrollments) {
                StudentHourFormOverride override = new StudentHourFormOverride();
                override.setHourForm(form);
                override.setStudent(enrollment.getStudent());
                override.setHoursOverride(form.getHoursTaught());
                overrideRepository.save(override);
            }
        }
    }

    /**
     * แก้ไข HourForm
     */
    public HourFormDto updateHourForm(Long id, UpdateHourFormRequestDto request, Long tutorId) {
        HourForm form = hourFormRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ไม่พบข้อมูลการสอน"));

        // ตรวจสอบสิทธิ์ (เฉพาะครูที่สร้างเท่านั้น)
        if (!form.getTutor().getId().equals(tutorId)) {
            throw new RuntimeException("คุณไม่มีสิทธิ์แก้ไขข้อมูลนี้");
        }

        // Update fields
        if (request.getContent() != null) {
            form.setContent(request.getContent());
        }
        if (request.getTeachingDate() != null) {
            form.setTeachingDate(request.getTeachingDate());
        }
        if (request.getHoursTaught() != null) {
            form.setHoursTaught(request.getHoursTaught());
        }
        if (request.getStudentsPresent() != null) {
            form.setStudentsPresent(request.getStudentsPresent());
        }
        if (request.getStudentsAbsent() != null) {
            form.setStudentsAbsent(request.getStudentsAbsent());
        }
        if (request.getRemark() != null) {
            form.setRemark(request.getRemark());
        }

        HourForm updated = hourFormRepository.save(form);
        return convertToDto(updated);
    }

    /**
     * ลบ HourForm
     */
    public void deleteHourForm(Long id, Long tutorId, String role) {
        HourForm form = hourFormRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ไม่พบข้อมูลการสอน"));

        // ตรวจสอบสิทธิ์ (Admin ลบได้ทั้งหมด, Tutor ลบได้เฉพาะของตัวเอง)
        if (!role.equalsIgnoreCase("ADMIN") && !form.getTutor().getId().equals(tutorId)) {
            throw new RuntimeException("คุณไม่มีสิทธิ์ลบข้อมูลนี้");
        }

        hourFormRepository.delete(form);
    }

    /**
     * ดึง HourForm ตามช่วงวันที่
     */
    @Transactional(readOnly = true)
    public List<HourFormDto> getHourFormsByDateRange(String startDate, String endDate, Long tutorId) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);

        List<HourForm> forms;
        if (tutorId != null) {
            forms = hourFormRepository.findByTutorIdAndTeachingDateBetween(tutorId, start, end);
        } else {
            forms = hourFormRepository.findByTeachingDateBetween(start, end);
        }

        return forms.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * ดึงสถิติการสอน
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getTeachingStatistics(Long tutorId, String startDate, String endDate) {
        LocalDate start = startDate != null ? LocalDate.parse(startDate) : LocalDate.now().minusMonths(1);
        LocalDate end = endDate != null ? LocalDate.parse(endDate) : LocalDate.now();

        List<HourForm> forms = hourFormRepository.findByTutorIdAndTeachingDateBetween(tutorId, start, end);

        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalForms", forms.size());
        statistics.put("totalHours", forms.stream()
                .map(HourForm::getHoursTaught)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        statistics.put("totalStudentsPresent", forms.stream()
                .mapToInt(HourForm::getStudentsPresent)
                .sum());
        statistics.put("totalStudentsAbsent", forms.stream()
                .mapToInt(HourForm::getStudentsAbsent)
                .sum());

        return statistics;
    }

    // ===== HELPER METHODS =====

    /**
     * ตรวจสอบว่าคลาสมีอยู่จริง
     */
    private void validateClassExists(Long classId, String classType) {
        if ("hourly_group".equalsIgnoreCase(classType)) {
            if (!groupClassRepository.existsById(classId)) {
                throw new RuntimeException("ไม่พบข้อมูลคลาสกลุ่ม");
            }
        } else if ("hourly_individual".equalsIgnoreCase(classType)) {
            if (!individualClassRepository.existsById(classId)) {
                throw new RuntimeException("ไม่พบข้อมูลคลาสเดี่ยว");
            }
        } else {
            throw new RuntimeException("ประเภทคลาสไม่ถูกต้อง");
        }
    }

    /**
     * แปลง Entity เป็น DTO พร้อมข้อมูลคลาส
     */
    private HourFormDto convertToDto(HourForm form) {
        HourFormDto dto = HourFormDto.fromEntity(form);

        // ดึงชื่อคลาสตามประเภท
        String className = getClassName(form.getClassId(), form.getClassType());
        dto.setClassName(className);
        dto.setClassDisplayName(className);

        String grade = getGradeFromClass(form.getClassId(), form.getClassType());
        dto.setGrade(grade);

        return dto;
    }

    /**
     * ดึงชื่อคลาสตาม ID และประเภท
     */
    private String getClassName(Long classId, String classType) {
        if ("hourly_group".equalsIgnoreCase(classType)) {
            return groupClassRepository.findById(classId)
                    .map(HourlyGroupClass::getClassName)
                    .orElse("ไม่พบข้อมูลคลาส");
        } else if ("hourly_individual".equalsIgnoreCase(classType)) {
            return individualClassRepository.findById(classId)
                    .map(HourlyIndividualClass::getClassName)
                    .orElse("ไม่พบข้อมูลคลาส");
        }
        return "ไม่ทราบประเภทคลาส";
    }

    /**
     * อัปเดตชั่วโมงของนักเรียนทั้งหมดในคลาส
     * - Hourly Group: ทุกคนที่ active ได้ชั่วโมงเท่ากัน
     * - Hourly Individual: นักเรียนคนเดียวในคลาสได้ชั่วโมง
     */
    private void updateStudentHours(Long classId, String classType, BigDecimal hoursToAdd) {
        Long subjectId = getSubjectIdFromClass(classId, classType);

        if ("hourly_group".equalsIgnoreCase(classType)) {
            List<HourlyGroupEnrollment> enrollments =
                    enrollmentRepository.findByClassIdAndIsActiveTrue(classId);

            for (HourlyGroupEnrollment enrollment : enrollments) {
                // บันทึก override เพื่อ mark ว่านักเรียนคนนี้ได้รับชั่วโมงจากฟอร์มนี้
                markStudentReceivedHours(enrollment.getStudent(), classId, classType, hoursToAdd);

                updateStudentCoursePurchaseHoursWithNotification(
                        enrollment.getStudent(),
                        subjectId,
                        hoursToAdd,
                        "GROUP",
                        classId
                );
            }

        } else if ("hourly_individual".equalsIgnoreCase(classType)) {
            HourlyIndividualClass individualClass = individualClassRepository.findById(classId)
                    .orElseThrow(() -> new RuntimeException("ไม่พบคลาสเดี่ยว"));

            if (!individualClass.getIsActive()) {
                return;
            }

            List<Student> allStudentsInClass = getStudentsInIndividualClass(classId);
            int totalStudents = allStudentsInClass.size();
            String actualClassType = (totalStudents == 1) ? "INDIVIDUAL" : "INDIVIDUAL_GROUP";

            List<HourlyIndividualClassStudent> activeEnrollments =
                    hourlyIndividualClassStudentRepository.findByClassIdAndActive(classId);

            for (HourlyIndividualClassStudent enrollment : activeEnrollments) {
                // บันทึก override เพื่อ mark ว่านักเรียนคนนี้ได้รับชั่วโมงจากฟอร์มนี้
                markStudentReceivedHours(enrollment.getStudent(), classId, classType, hoursToAdd);

                updateStudentCoursePurchaseHoursWithNotification(
                        enrollment.getStudent(),
                        individualClass.getSubject().getId(),
                        hoursToAdd,
                        actualClassType,
                        classId
                );
            }
        }
    }

    /**
     * บันทึกว่านักเรียนคนนี้ได้รับชั่วโมงจากฟอร์มนี้
     */
    private void markStudentReceivedHours(Student student, Long classId, String classType, BigDecimal hours) {
        // หา HourForm ล่าสุดที่เพิ่งสร้าง (จะยังไม่มี ID ใน context นี้)
        // เราจะบันทึกทีหลังใน createHourForm()
    }

    /**
     * ดึง subjectId จากคลาส
     */
    private Long getSubjectIdFromClass(Long classId, String classType) {
        if ("hourly_group".equalsIgnoreCase(classType)) {
            return groupClassRepository.findById(classId)
                    .map(c -> c.getSubject().getId())
                    .orElseThrow(() -> new RuntimeException("ไม่พบคลาสกลุ่ม"));
        } else if ("hourly_individual".equalsIgnoreCase(classType)) {
            return individualClassRepository.findById(classId)
                    .map(c -> c.getSubject().getId())
                    .orElseThrow(() -> new RuntimeException("ไม่พบคลาสเดี่ยว"));
        }
        throw new RuntimeException("ประเภทคลาสไม่ถูกต้อง");
    }

    /**
     * อัปเดตชั่วโมงและเช็ค notification
     */
    private void updateStudentCoursePurchaseHoursWithNotification(
            Student student,
            Long subjectId,
            BigDecimal hoursToAdd,
            String classType,
            Long classId) {

        // แปลง classType ให้ตรงกับ format ในฐานข้อมูล
        String dbClassType;
        if ("GROUP".equalsIgnoreCase(classType)) {
            dbClassType = "hourly_group";
        } else if ("INDIVIDUAL".equalsIgnoreCase(classType)) {
            dbClassType = "hourly_individual";
        } else {
            // ถ้าส่งมาเป็น "hourly_group" หรือ "hourly_individual" อยู่แล้ว ใช้เลย
            dbClassType = classType;
        }

        System.out.println("=== LOOKING FOR COURSE PURCHASE ===");
        System.out.println("Student ID: " + student.getId());
        System.out.println("Subject ID: " + subjectId);
        System.out.println("DB ClassType: " + dbClassType);

        List<StudentCoursePurchase> purchases = coursePurchaseRepository
                .findByStudentIdAndSubjectIdAndClassTypeAndIsActiveTrue(
                        student.getId(),
                        subjectId,
                        dbClassType
                );

        System.out.println("Found purchases: " + purchases.size());

        if (!purchases.isEmpty()) {
            System.out.println("   - First purchase classType: " + purchases.get(0).getClassType());
        }
        System.out.println("---");

        if (purchases.isEmpty()) {
            System.out.println("Warning: Student " + student.getId() +
                    " has no active course for subject " + subjectId +
                    " with classType: " + dbClassType);
            return;
        }

        // เก็บค่า totalHoursCompleted ก่อน update
        BigDecimal previousTotalCompleted = purchases.stream()
                .map(StudentCoursePurchase::getHoursCompleted)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // เลือกคอร์สแรกที่ยังเรียนไม่เสร็จ
        StudentCoursePurchase targetPurchase = purchases.stream()
                .filter(p -> p.getHoursCompleted().compareTo(p.getHoursPurchased()) < 0)
                .findFirst()
                .orElse(purchases.get(0));

        // เพิ่มชั่วโมง
        BigDecimal newHoursCompleted = targetPurchase.getHoursCompleted().add(hoursToAdd);
        targetPurchase.setHoursCompleted(newHoursCompleted);
        coursePurchaseRepository.save(targetPurchase);

        // เช็คและสร้าง notification พร้อมส่ง previousTotalCompleted
        notificationService.checkAndCreateHoursCompletedNotification(
                student, subjectId, dbClassType, classId, previousTotalCompleted);
    }

    /**
     * ดึงนักเรียนทั้งหมดในคลาส Hourly Individual (ผ่าน junction table)
     */
    private List<Student> getStudentsInIndividualClass(Long classId) {
        // ดึงนักเรียนทั้งหมด (รวมทั้ง inactive) เพื่อนับจำนวนจริง
        return individualClassRepository.findById(classId)
                .map(cls -> cls.getClassStudents().stream()
                        .map(HourlyIndividualClassStudent::getStudent)
                        .collect(Collectors.toList()))
                .orElse(List.of());
    }

    private String getGradeFromClass(Long classId, String classType) {
        if ("hourly_group".equalsIgnoreCase(classType)) {
            return groupClassRepository.findById(classId)
                    .map(c -> c.getGrade() != null ? c.getGrade().getGradeName() : "-")
                    .orElse("-");
        } else if ("hourly_individual".equalsIgnoreCase(classType)) {
            return individualClassRepository.findById(classId)
                    .map(c -> c.getGrade() != null ? c.getGrade().getGradeName() : "-")
                    .orElse("-");
        }
        return "-";
    }

    /**
     * Admin แก้ไขชั่วโมงของนักเรียนคนใดคนหนึ่ง (independent)
     */
    public void updateStudentHoursOverride(Long hourFormId, Long studentId, BigDecimal newHours, String newRemark) {
        HourForm form = hourFormRepository.findById(hourFormId)
                .orElseThrow(() -> new RuntimeException("ไม่พบ HourForm"));

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("ไม่พบนักเรียน"));

        // หา override record เดิม (ถ้ามี)
        StudentHourFormOverride override = overrideRepository
                .findByHourFormIdAndStudentId(hourFormId, studentId)
                .orElse(new StudentHourFormOverride());

        // คำนวณส่วนต่างชั่วโมง + เก็บค่าเก่า
        BigDecimal oldHours = override.getHoursOverride() != null
                ? override.getHoursOverride()
                : form.getHoursTaught();
        BigDecimal hoursDiff = newHours.subtract(oldHours);
        String oldRemark = override.getRemarkOverride();

        // อัปเดต override
        override.setHourForm(form);
        override.setStudent(student);
        override.setHoursOverride(newHours);
        override.setRemarkOverride(newRemark);
        overrideRepository.save(override);

        // บันทึกประวัติเฉพาะนักเรียนคนนี้
        User currentUser = getCurrentUser();

        // บันทึกประวัติชั่วโมง (ถ้ามีการเปลี่ยนแปลง)
        if (oldHours.compareTo(newHours) != 0) {
            studentOverrideHistoryService.recordChange(
                    override,
                    form,
                    student,
                    "hoursTaught",
                    oldHours.toString(),
                    newHours.toString(),
                    currentUser
            );
        }

        // บันทึกประวัติ remark (ถ้ามีการเปลี่ยนแปลง)
        if ((oldRemark == null && newRemark != null) ||
                (oldRemark != null && !oldRemark.equals(newRemark))) {
            studentOverrideHistoryService.recordChange(
                    override,
                    form,
                    student,
                    "remark",
                    oldRemark,
                    newRemark,
                    currentUser
            );
        }

        // อัปเดตชั่วโมงในคอร์ส
        Long subjectId = form.getSubject().getId();
        String classType;

        if (form.getClassType().equals("hourly_group")) {
            classType = "GROUP";
        } else {
            List<Student> allStudentsInClass = getStudentsInIndividualClass(form.getClassId());
            int totalStudents = allStudentsInClass.size();
            classType = (totalStudents == 1) ? "INDIVIDUAL" : "INDIVIDUAL_GROUP";
        }

        updateStudentCoursePurchaseHoursWithNotification(
                student, subjectId, hoursDiff, classType, form.getClassId());
    }

    /**
     * Admin แก้ไขหมายเหตุของฟอร์ม
     */
    public void updateFormRemark(Long hourFormId, String newRemark) {
        HourForm form = hourFormRepository.findById(hourFormId)
                .orElseThrow(() -> new RuntimeException("ไม่พบ HourForm"));

        form.setRemark(newRemark);
        hourFormRepository.save(form);
    }

    /**
     * Admin สร้างฟอร์มให้นักเรียนคนเดียว (ไม่กระทบเพื่อน)
     */
    public HourFormDto createHourFormForSpecificStudent(CreateHourFormRequestDto request, Long studentId) {
        // Validate student
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("ไม่พบนักเรียน"));

        // Validate tutor
        User tutor = userRepository.findById(request.getTutorId())
                .orElseThrow(() -> new RuntimeException("ไม่พบข้อมูลครู"));

        // Validate subject
        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new RuntimeException("ไม่พบข้อมูลวิชา"));

        // Validate class exists
        validateClassExists(request.getClassId(), request.getClassType());

        // Create new HourForm
        HourForm form = new HourForm();
        form.setTutor(tutor);
        form.setClassType(request.getClassType());
        form.setClassId(request.getClassId());
        form.setSubject(subject);
        form.setContent(request.getContent());
        form.setTeachingDate(request.getTeachingDate());
        form.setHoursTaught(request.getHoursTaught());
        form.setStudentsPresent(1); // ไม่ใช้ แต่ต้องมี
        form.setStudentsAbsent(0);  // ไม่ใช้ แต่ต้องมี
        form.setRemark(request.getRemark());
        form.setIsAdminCreated(true);

        HourForm saved = hourFormRepository.save(form);

        // บันทึก override เฉพาะนักเรียนคนนี้
        StudentHourFormOverride override = new StudentHourFormOverride();
        override.setHourForm(saved);
        override.setStudent(student);
        override.setHoursOverride(request.getHoursTaught());
        override.setRemarkOverride(request.getRemark());
        overrideRepository.save(override);

        // อัปเดตชั่วโมงเฉพาะนักเรียนคนนี้
        String classType = determineClassTypeForStudent(request.getClassId(), request.getClassType());
        updateStudentCoursePurchaseHoursWithNotification(
                student,
                request.getSubjectId(),
                request.getHoursTaught(),
                classType,
                request.getClassId()
        );

        return convertToDto(saved);
    }

    /**
     * ดึงคลาสที่นักเรียนเรียนอยู่ (สำหรับ dropdown)
     */
    public List<Map<String, Object>> getStudentActiveClasses(Long studentId) {
        List<Map<String, Object>> result = new ArrayList<>();

        // ดึงคลาส Hourly Group
        List<HourlyGroupEnrollment> groupEnrollments =
                enrollmentRepository.findByStudentIdAndIsActiveTrue(studentId);

        for (HourlyGroupEnrollment e : groupEnrollments) {
            Map<String, Object> classInfo = new HashMap<>();
            classInfo.put("classId", e.getHourlyGroupClass().getId());
            classInfo.put("classType", "hourly_group");
            classInfo.put("className", e.getHourlyGroupClass().getClassName());
            classInfo.put("subjectId", e.getHourlyGroupClass().getSubject().getId());
            classInfo.put("subjectName", e.getHourlyGroupClass().getSubject().getSubjectName());
            result.add(classInfo);
        }

        // ดึงคลาส Hourly Individual
        List<HourlyIndividualClassStudent> individualEnrollments =
                hourlyIndividualClassStudentRepository.findByStudentIdAndActive(studentId);

        for (HourlyIndividualClassStudent e : individualEnrollments) {
            Map<String, Object> classInfo = new HashMap<>();
            classInfo.put("classId", e.getHourlyIndividualClass().getId());
            classInfo.put("classType", "hourly_individual");
            classInfo.put("className", e.getHourlyIndividualClass().getClassName());
            classInfo.put("subjectId", e.getHourlyIndividualClass().getSubject().getId());
            classInfo.put("subjectName", e.getHourlyIndividualClass().getSubject().getSubjectName());
            result.add(classInfo);
        }

        return result;
    }

    /**
     * กำหนด classType ที่ถูกต้องสำหรับนักเรียน
     */
    private String determineClassTypeForStudent(Long classId, String classType) {
        if ("hourly_group".equalsIgnoreCase(classType)) {
            return "GROUP";
        } else if ("hourly_individual".equalsIgnoreCase(classType)) {
            List<Student> allStudentsInClass = getStudentsInIndividualClass(classId);
            int totalStudents = allStudentsInClass.size();
            return (totalStudents == 1) ? "INDIVIDUAL" : "INDIVIDUAL_GROUP";
        }
        return classType;
    }

    /**
     * ดึง User ปัจจุบันจาก Security Context
     */
    private User getCurrentUser() {
        org.springframework.security.core.Authentication authentication =
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Current user not found"));
    }
}