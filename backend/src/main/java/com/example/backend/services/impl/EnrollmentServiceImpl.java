package com.example.backend.services.impl;

import com.example.backend.dtos.enrollment.*;
import com.example.backend.entities.EnrollmentHistory;
import com.example.backend.entities.User;
import com.example.backend.entities.hourly.HourlyGroupClass;
import com.example.backend.entities.hourly.HourlyIndividualClass;
import com.example.backend.entities.hourly.HourlyIndividualClassStudent;
import com.example.backend.entities.monthly.MonthlyClass;
import com.example.backend.entities.student.Student;
import com.example.backend.entities.hourly.HourlyGroupEnrollment;
import com.example.backend.entities.monthly.MonthlyEnrollment;
import com.example.backend.repositories.EnrollmentHistoryRepository;
import com.example.backend.repositories.UserRepository;
import com.example.backend.repositories.hourly.HourlyGroupClassRepository;
import com.example.backend.repositories.hourly.HourlyGroupEnrollmentRepository;
import com.example.backend.repositories.hourly.HourlyIndividualClassRepository;
import com.example.backend.repositories.hourly.HourlyIndividualClassStudentRepository;
import com.example.backend.repositories.monthly.MonthlyClassRepository;
import com.example.backend.repositories.monthly.MonthlyEnrollmentRepository;
import com.example.backend.repositories.student.StudentRepository;
import com.example.backend.services.EnrollmentService;
import com.example.backend.services.student.StudentHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EnrollmentServiceImpl implements EnrollmentService {

    @Autowired
    private HourlyGroupEnrollmentRepository hourlyGroupEnrollmentRepository;

    @Autowired
    private MonthlyEnrollmentRepository monthlyEnrollmentRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private StudentHistoryService historyService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HourlyGroupClassRepository hourlyGroupClassRepository;

    @Autowired
    private MonthlyClassRepository monthlyClassRepository;


    @Autowired
    private HourlyIndividualClassRepository hourlyIndividualClassRepository;

    @Autowired
    private HourlyIndividualClassStudentRepository hourlyIndividualClassStudentRepository;

    @Autowired
    private EnrollmentHistoryRepository enrollmentHistoryRepository;

    @Override
    public StudentEnrolledClassesDto getStudentEnrollments(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        StudentEnrolledClassesDto dto = new StudentEnrolledClassesDto();
        dto.setStudentId(student.getId());
        dto.setStudentName(student.getFirstName() + " " + (student.getLastName() != null ? student.getLastName() : ""));
        dto.setNickname(student.getNickname());

        return dto;
    }

    @Override
    public List<EnrollmentResponseDto> getHourlyGroupStudents(Long classId) {
        return List.of();
    }

    @Override
    public List<EnrollmentResponseDto> getMonthlyStudents(Long classId) {
        return List.of();
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = "students", key = "'student-with-classes-' + #request.studentId")
    public EnrollmentResponseDto enrollHourlyGroup(EnrollmentRequestDto request) {
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new RuntimeException("ไม่พบนักเรียนดังกล่าว"));

        HourlyGroupClass classEntity = hourlyGroupClassRepository.findById(request.getClassId())
                .orElseThrow(() -> new RuntimeException("ไม่พบคลาสดังกล่าว"));

        if (hourlyGroupEnrollmentRepository.existsByClassIdAndStudentId(
                request.getClassId(), request.getStudentId())) {
            throw new RuntimeException("นักเรียนที่คุณเลือกอยู่ในคลาสอยู่แล้ว");
        }

        HourlyGroupEnrollment enrollment = new HourlyGroupEnrollment();
        enrollment.setHourlyGroupClass(classEntity);
        enrollment.setStudent(student);
        enrollment.setEnrolledGradeId(student.getGrade().getId());
        enrollment.setIsActive(true);

        HourlyGroupEnrollment saved = hourlyGroupEnrollmentRepository.save(enrollment);

        return EnrollmentResponseDto.fromEntity(saved);
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = "students", key = "'student-with-classes-' + #request.studentId")
    public EnrollmentResponseDto enrollMonthly(EnrollmentRequestDto request) {
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new RuntimeException("ไม่พบนักเรียนดังกล่าว"));

        MonthlyClass classEntity = monthlyClassRepository.findById(request.getClassId())
                .orElseThrow(() -> new RuntimeException("ไม่พบคลาสดังกล่าว"));

        if (monthlyEnrollmentRepository.existsByClassIdAndStudentId(
                request.getClassId(), request.getStudentId())) {
            throw new RuntimeException("นักเรียนที่คุณเลือกอยู่ในคลาสอยู่แล้ว");
        }

        MonthlyEnrollment enrollment = new MonthlyEnrollment();
        enrollment.setMonthlyClass(classEntity);
        enrollment.setStudent(student);
        enrollment.setEnrolledGradeId(student.getGrade().getId());
        enrollment.setIsActive(true);

        MonthlyEnrollment saved = monthlyEnrollmentRepository.save(enrollment);

        return EnrollmentResponseDto.fromEntity(saved);
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = "students", key = "'student-with-classes-' + #studentId")
    public void unenrollHourlyGroup(Long classId, Long studentId) {
        HourlyGroupEnrollment enrollment = hourlyGroupEnrollmentRepository
                .findByClassIdAndStudentId(classId, studentId)
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));

        Student student = enrollment.getStudent();
        HourlyGroupClass clazz = enrollment.getHourlyGroupClass();
        User currentUser = getCurrentUser();

        historyService.trackClassRemoved(
                student,
                "คลาสรายชั่วโมง (กลุ่ม)",
                clazz.getClassName(),
                clazz.getSubject() != null ? clazz.getSubject().getSubjectName() : "",
                currentUser
        );

        hourlyGroupEnrollmentRepository.delete(enrollment);
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = "students", key = "'student-with-classes-' + #studentId")
    public void unenrollMonthly(Long classId, Long studentId) {
        MonthlyEnrollment enrollment = monthlyEnrollmentRepository
                .findByClassIdAndStudentId(classId, studentId)
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));

        Student student = enrollment.getStudent();
        MonthlyClass clazz = enrollment.getMonthlyClass();
        User currentUser = getCurrentUser();

        historyService.trackClassRemoved(
                student,
                "คลาสรายเดือน",
                clazz.getClassName(),
                clazz.getSubject() != null ? clazz.getSubject().getSubjectName() : "",
                currentUser
        );

        monthlyEnrollmentRepository.delete(enrollment);
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = "students", allEntries = true)
    public void activateMonthlyEnrollment(Long enrollmentId, String reason) {
        MonthlyEnrollment enrollment = monthlyEnrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new RuntimeException("Monthly enrollment not found"));

        User currentUser = getCurrentUser();

        enrollment.setIsActive(true);
        enrollment.setStatusChangeReason(reason);
        enrollment.setStatusChangedBy(currentUser);
        enrollment.setStatusChangedAt(LocalDateTime.now());
        monthlyEnrollmentRepository.save(enrollment);

        EnrollmentHistory history = new EnrollmentHistory(
                "monthly",
                enrollmentId,
                enrollment.getMonthlyClass().getId(),
                enrollment.getStudent().getId(),
                "UPDATE",
                "is_active",
                "false",
                "true",
                currentUser
        );
        history.setStatusChangeReason(reason);
        enrollmentHistoryRepository.save(history);

        Student student = enrollment.getStudent();
        MonthlyClass clazz = enrollment.getMonthlyClass();

        historyService.trackClassReactivated(
                student,
                "คลาสรายเดือน",
                clazz.getClassName(),
                clazz.getSubject() != null ? clazz.getSubject().getSubjectName() : "",
                currentUser
        );
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = "students", allEntries = true)
    public void deactivateMonthlyEnrollment(Long enrollmentId, String reason) {
        MonthlyEnrollment enrollment = monthlyEnrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new RuntimeException("Monthly enrollment not found"));

        User currentUser = getCurrentUser();

        enrollment.setIsActive(false);
        enrollment.setStatusChangeReason(reason);
        enrollment.setStatusChangedBy(currentUser);
        enrollment.setStatusChangedAt(LocalDateTime.now());
        monthlyEnrollmentRepository.save(enrollment);

        EnrollmentHistory history = new EnrollmentHistory(
                "monthly",
                enrollmentId,
                enrollment.getMonthlyClass().getId(),
                enrollment.getStudent().getId(),
                "UPDATE",
                "is_active",
                "true",
                "false",
                currentUser
        );
        history.setStatusChangeReason(reason);
        enrollmentHistoryRepository.save(history);

        Student student = enrollment.getStudent();
        MonthlyClass clazz = enrollment.getMonthlyClass();

        historyService.trackClassRemoved(
                student,
                "คลาสรายเดือน",
                clazz.getClassName(),
                clazz.getSubject() != null ? clazz.getSubject().getSubjectName() : "",
                currentUser
        );
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = "students", allEntries = true)
    public void activateHourlyGroupEnrollment(Long enrollmentId, String reason) {
        HourlyGroupEnrollment enrollment = hourlyGroupEnrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new RuntimeException("Hourly group enrollment not found"));

        User currentUser = getCurrentUser();

        enrollment.setIsActive(true);
        enrollment.setStatusChangeReason(reason);
        enrollment.setStatusChangedBy(currentUser);
        enrollment.setStatusChangedAt(LocalDateTime.now());
        hourlyGroupEnrollmentRepository.save(enrollment);

        EnrollmentHistory history = new EnrollmentHistory(
                "hourly-group",
                enrollmentId,
                enrollment.getHourlyGroupClass().getId(),
                enrollment.getStudent().getId(),
                "UPDATE",
                "is_active",
                "false",
                "true",
                currentUser
        );
        history.setStatusChangeReason(reason);
        enrollmentHistoryRepository.save(history);

        Student student = enrollment.getStudent();
        HourlyGroupClass clazz = enrollment.getHourlyGroupClass();

        historyService.trackClassReactivated(
                student,
                "คลาสรายชั่วโมง (กลุ่ม)",
                clazz.getClassName(),
                clazz.getSubject() != null ? clazz.getSubject().getSubjectName() : "",
                currentUser
        );
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = "students", allEntries = true)
    public void deactivateHourlyGroupEnrollment(Long enrollmentId, String reason) {
        HourlyGroupEnrollment enrollment = hourlyGroupEnrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new RuntimeException("Hourly group enrollment not found"));

        User currentUser = getCurrentUser();

        enrollment.setIsActive(false);
        enrollment.setStatusChangeReason(reason);
        enrollment.setStatusChangedBy(currentUser);
        enrollment.setStatusChangedAt(LocalDateTime.now());
        hourlyGroupEnrollmentRepository.save(enrollment);

        EnrollmentHistory history = new EnrollmentHistory(
                "hourly-group",
                enrollmentId,
                enrollment.getHourlyGroupClass().getId(),
                enrollment.getStudent().getId(),
                "UPDATE",
                "is_active",
                "true",
                "false",
                currentUser
        );
        history.setStatusChangeReason(reason);
        enrollmentHistoryRepository.save(history);

        Student student = enrollment.getStudent();
        HourlyGroupClass clazz = enrollment.getHourlyGroupClass();

        historyService.trackClassRemoved(
                student,
                "คลาสรายชั่วโมง (กลุ่ม)",
                clazz.getClassName(),
                clazz.getSubject() != null ? clazz.getSubject().getSubjectName() : "",
                currentUser
        );
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Current user not found"));
    }

    @Override
    public List<EnrollmentResponseDto> getHourlyIndividualStudents(Long classId) {

        List<HourlyIndividualClassStudent> students =
                hourlyIndividualClassStudentRepository.findByClassIdAndActive(classId);

        return students.stream()
                .map(cs -> {
                    EnrollmentResponseDto dto = new EnrollmentResponseDto();

                    dto.setEnrollmentId(cs.getId());
                    dto.setClassId(cs.getHourlyIndividualClass().getId());
                    dto.setClassName(cs.getHourlyIndividualClass().getClassName());

                    dto.setStudentId(cs.getStudent().getId());

                    String studentName =
                            cs.getStudent().getFirstName() + " " +
                                    (cs.getStudent().getLastName() != null
                                            ? cs.getStudent().getLastName()
                                            : "");

                    dto.setStudentName(studentName);
                    dto.setStudentNickname(cs.getStudent().getNickname());
                    dto.setIsActive(cs.getIsActive());

                    return dto;
                })
                .toList();
    }


    @Override
    @Transactional
    @CacheEvict(cacheNames = "students", key = "'student-with-classes-' + #request.studentId")
    public EnrollmentResponseDto enrollHourlyIndividual(EnrollmentRequestDto request) {
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new RuntimeException("ไม่พบบนักเรียนดังกล่าว"));

        HourlyIndividualClass classEntity = hourlyIndividualClassRepository.findById(request.getClassId())
                .orElseThrow(() -> new RuntimeException("ไม่พบคลาสดังกล่าว"));

        if (hourlyIndividualClassStudentRepository.existsByClassIdAndStudentId(
                request.getClassId(), request.getStudentId())) {
            throw new RuntimeException("นักเรียนที่คุณเลือกอยู่ในคลาสอยู่แล้ว");
        }

        HourlyIndividualClassStudent enrollment = new HourlyIndividualClassStudent();
        enrollment.setHourlyIndividualClass(classEntity);
        enrollment.setStudent(student);
        enrollment.setIsActive(true);

        HourlyIndividualClassStudent saved = hourlyIndividualClassStudentRepository.save(enrollment);

        EnrollmentResponseDto dto = new EnrollmentResponseDto();
        dto.setEnrollmentId(saved.getId());
        dto.setClassId(classEntity.getId());
        dto.setClassName(classEntity.getClassName());

        dto.setStudentId(student.getId());
        dto.setStudentName(
                student.getFirstName() + " " +
                        (student.getLastName() != null ? student.getLastName() : "")
        );
        dto.setStudentNickname(student.getNickname());
        dto.setIsActive(saved.getIsActive());

        return dto;
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = "students", key = "'student-with-classes-' + #studentId")
    public void unenrollHourlyIndividual(Long classId, Long studentId) {
        List<HourlyIndividualClassStudent> enrollments = hourlyIndividualClassStudentRepository
                .findByClassIdAndActive(classId);

        HourlyIndividualClassStudent enrollment = enrollments.stream()
                .filter(e -> e.getStudent().getId().equals(studentId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));

        Student student = enrollment.getStudent();
        HourlyIndividualClass clazz = enrollment.getHourlyIndividualClass();
        User currentUser = getCurrentUser();

        historyService.trackClassRemoved(
                student,
                "คลาสรายชั่วโมง (เดี่ยว)",
                clazz.getClassName(),
                clazz.getSubject() != null ? clazz.getSubject().getSubjectName() : "",
                currentUser
        );

        hourlyIndividualClassStudentRepository.delete(enrollment);
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = "students", allEntries = true)
    public void activateHourlyIndividualEnrollment(Long enrollmentId, String reason) {
        HourlyIndividualClassStudent enrollment = hourlyIndividualClassStudentRepository
                .findById(enrollmentId)
                .orElseThrow(() -> new RuntimeException("Hourly individual enrollment not found"));

        User currentUser = getCurrentUser();

        enrollment.setStatusChangeReason(reason);
        enrollment.setStatusChangedBy(currentUser);
        enrollment.setStatusChangedAt(LocalDateTime.now());
        enrollment.setIsActive(true);
        hourlyIndividualClassStudentRepository.save(enrollment);

        EnrollmentHistory history = new EnrollmentHistory(
                "hourly-individual",
                enrollmentId,
                enrollment.getHourlyIndividualClass().getId(),
                enrollment.getStudent().getId(),
                "UPDATE",
                "is_active",
                "false",
                "true",
                currentUser
        );
        history.setStatusChangeReason(reason);
        enrollmentHistoryRepository.save(history);

        Student student = enrollment.getStudent();
        HourlyIndividualClass clazz = enrollment.getHourlyIndividualClass();

        historyService.trackClassReactivated(
                student,
                "คลาสรายชั่วโมง (เดี่ยว)",
                clazz.getClassName(),
                clazz.getSubject() != null ? clazz.getSubject().getSubjectName() : "",
                currentUser
        );
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = "students", allEntries = true)
    public void deactivateHourlyIndividualEnrollment(Long enrollmentId, String reason) {
        HourlyIndividualClassStudent enrollment = hourlyIndividualClassStudentRepository
                .findById(enrollmentId)
                .orElseThrow(() -> new RuntimeException("Hourly individual enrollment not found"));

        User currentUser = getCurrentUser();

        enrollment.setIsActive(false);
        enrollment.setStatusChangeReason(reason);
        enrollment.setStatusChangedBy(currentUser);
        enrollment.setStatusChangedAt(LocalDateTime.now());
        hourlyIndividualClassStudentRepository.save(enrollment);

        EnrollmentHistory history = new EnrollmentHistory(
                "hourly-individual",
                enrollmentId,
                enrollment.getHourlyIndividualClass().getId(),
                enrollment.getStudent().getId(),
                "UPDATE",
                "is_active",
                "true",
                "false",
                currentUser
        );
        history.setStatusChangeReason(reason);
        enrollmentHistoryRepository.save(history);

        Student student = enrollment.getStudent();
        HourlyIndividualClass clazz = enrollment.getHourlyIndividualClass();

        historyService.trackClassRemoved(
                student,
                "คลาสรายชั่วโมง (เดี่ยว)",
                clazz.getClassName(),
                clazz.getSubject() != null ? clazz.getSubject().getSubjectName() : "",
                currentUser
        );
    }

    @Override
    public List<EnrollmentHistoryDto> getEnrollmentHistory(String enrollmentType, Long enrollmentId) {
        return enrollmentHistoryRepository
                .findByEnrollmentTypeAndEnrollmentIdOrderByUpdatedAtDesc(enrollmentType, enrollmentId)
                .stream()
                .filter(h -> "UPDATE".equals(h.getAction()) && "is_active".equals(h.getFieldName()))
                .map(h -> new EnrollmentHistoryDto(
                        h.getId(),
                        "true".equals(h.getNewValue()),
                        h.getStatusChangeReason(),
                        h.getUpdatedBy() != null ? h.getUpdatedBy().getNickname() : "ไม่ทราบ",
                        h.getUpdatedAt()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<EnrollmentResponseDto> getAllMonthlyStudents(Long classId) {
        List<MonthlyEnrollment> enrollments = monthlyEnrollmentRepository
                .findByClassId(classId);  // ไม่ filter active

        return enrollments.stream()
                .map(EnrollmentResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<EnrollmentResponseDto> getAllHourlyGroupStudents(Long classId) {
        List<HourlyGroupEnrollment> enrollments = hourlyGroupEnrollmentRepository
                .findByClassId(classId);  // ไม่ filter active

        return enrollments.stream()
                .map(EnrollmentResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<EnrollmentResponseDto> getAllHourlyIndividualStudents(Long classId) {
        List<HourlyIndividualClassStudent> students =
                hourlyIndividualClassStudentRepository.findByClassId(classId);

        return students.stream()
                .map(cs -> {
                    EnrollmentResponseDto dto = new EnrollmentResponseDto();
                    dto.setEnrollmentId(cs.getId());
                    dto.setClassId(cs.getHourlyIndividualClass().getId());
                    dto.setClassName(cs.getHourlyIndividualClass().getClassName());
                    dto.setStudentId(cs.getStudent().getId());
                    dto.setStudentName(cs.getStudent().getFirstName() + " " +
                            (cs.getStudent().getLastName() != null ? cs.getStudent().getLastName() : ""));
                    dto.setStudentNickname(cs.getStudent().getNickname());
                    dto.setIsActive(cs.getIsActive());
                    return dto;
                })
                .toList();
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = "students", key = "'student-with-classes-' + #request.studentId")
    public EnrollmentResponseDto enrollMonthlyWithStatus(EnrollmentWithStatusRequestDto request) {
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new RuntimeException("ไม่พบนักเรียนดังกล่าว"));

        MonthlyClass classEntity = monthlyClassRepository.findById(request.getClassId())
                .orElseThrow(() -> new RuntimeException("ไม่พบคลาสดังกล่าว"));

        // เช็คว่ามี enrollment อยู่แล้วหรือไม่ (active หรือ inactive)
        boolean exists = monthlyEnrollmentRepository.existsByClassIdAndStudentId(
                request.getClassId(), request.getStudentId());

        if (exists) {
            // หาก enrollment มีอยู่แล้ว ให้ activate/deactivate ตามที่ส่งมา
            MonthlyEnrollment existingEnrollment = monthlyEnrollmentRepository
                    .findByClassIdAndStudentId(request.getClassId(), request.getStudentId())
                    .orElseThrow();

            existingEnrollment.setIsActive(request.getIsActive());
            existingEnrollment.setStatusChangedAt(LocalDateTime.now());
            existingEnrollment.setStatusChangedBy(getCurrentUser());

            MonthlyEnrollment updated = monthlyEnrollmentRepository.save(existingEnrollment);
            return EnrollmentResponseDto.fromEntity(updated);
        }

        // ถ้าไม่มี ให้สร้างใหม่
        MonthlyEnrollment enrollment = new MonthlyEnrollment();
        enrollment.setMonthlyClass(classEntity);
        enrollment.setStudent(student);
        enrollment.setEnrolledGradeId(student.getGrade().getId());
        enrollment.setIsActive(request.getIsActive());

        MonthlyEnrollment saved = monthlyEnrollmentRepository.save(enrollment);
        return EnrollmentResponseDto.fromEntity(saved);
    }

    @Override
    @Transactional
    @CacheEvict(cacheNames = "students", key = "'student-with-classes-' + #request.studentId")
    public EnrollmentResponseDto enrollHourlyGroupWithStatus(EnrollmentWithStatusRequestDto request) {
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new RuntimeException("ไม่พบนักเรียนดังกล่าว"));

        HourlyGroupClass classEntity = hourlyGroupClassRepository.findById(request.getClassId())
                .orElseThrow(() -> new RuntimeException("ไม่พบคลาสดังกล่าว"));

        // เช็คว่ามี enrollment อยู่แล้วหรือไม่
        boolean exists = hourlyGroupEnrollmentRepository.existsByClassIdAndStudentId(
                request.getClassId(), request.getStudentId());

        if (exists) {
            // หาก enrollment มีอยู่แล้ว ให้ activate/deactivate ตามที่ส่งมา
            HourlyGroupEnrollment existingEnrollment = hourlyGroupEnrollmentRepository
                    .findByClassIdAndStudentId(request.getClassId(), request.getStudentId())
                    .orElseThrow();

            existingEnrollment.setIsActive(request.getIsActive());
            existingEnrollment.setStatusChangedAt(LocalDateTime.now());
            existingEnrollment.setStatusChangedBy(getCurrentUser());

            HourlyGroupEnrollment updated = hourlyGroupEnrollmentRepository.save(existingEnrollment);
            return EnrollmentResponseDto.fromEntity(updated);
        }

        // ถ้าไม่มี ให้สร้างใหม่
        HourlyGroupEnrollment enrollment = new HourlyGroupEnrollment();
        enrollment.setHourlyGroupClass(classEntity);
        enrollment.setStudent(student);
        enrollment.setEnrolledGradeId(student.getGrade().getId());
        enrollment.setIsActive(request.getIsActive());

        HourlyGroupEnrollment saved = hourlyGroupEnrollmentRepository.save(enrollment);
        return EnrollmentResponseDto.fromEntity(saved);
    }
}