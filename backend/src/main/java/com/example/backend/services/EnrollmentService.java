package com.example.backend.services;

import com.example.backend.dtos.enrollment.*;

import java.util.List;

public interface EnrollmentService {

    // ดึงคลาสทั้งหมดที่นักเรียนลงทะเบียน
    StudentEnrolledClassesDto getStudentEnrollments(Long studentId);

    // ดึงนักเรียนในคลาส
    List<EnrollmentResponseDto> getHourlyGroupStudents(Long classId);
    List<EnrollmentResponseDto> getMonthlyStudents(Long classId);

    // เพิ่มนักเรียนเข้าคลาส
    EnrollmentResponseDto enrollHourlyGroup(EnrollmentRequestDto request);
    EnrollmentResponseDto enrollMonthly(EnrollmentRequestDto request);

    // ลบนักเรียนออกจากคลาส
    void unenrollHourlyGroup(Long classId, Long studentId);
    void unenrollMonthly(Long classId, Long studentId);

    /**
     * เปิดการใช้งาน Monthly Enrollment
     */
    void activateMonthlyEnrollment(Long enrollmentId, String reason);

    /**
     * ปิดการใช้งาน Monthly Enrollment
     */
    void deactivateMonthlyEnrollment(Long enrollmentId, String reason);

    /**
     * เปิดการใช้งาน Hourly Group Enrollment
     */
    void activateHourlyGroupEnrollment(Long enrollmentId, String reason);

    /**
     * ปิดการใช้งาน Hourly Group Enrollment
     */
    void deactivateHourlyGroupEnrollment(Long enrollmentId, String reason);

    List<EnrollmentResponseDto> getHourlyIndividualStudents(Long classId);
    EnrollmentResponseDto enrollHourlyIndividual(EnrollmentRequestDto request);
    void unenrollHourlyIndividual(Long classId, Long studentId);

    /**
     * เปิดการใช้งาน Hourly Individual Enrollment
     */
    void activateHourlyIndividualEnrollment(Long enrollmentId, String reason);

    /**
     * ปิดการใช้งาน Hourly Individual Enrollment
     */
    void deactivateHourlyIndividualEnrollment(Long enrollmentId, String reason);

    List<EnrollmentHistoryDto> getEnrollmentHistory(String enrollmentType, Long enrollmentId);

    List<EnrollmentResponseDto> getAllMonthlyStudents(Long classId);
    List<EnrollmentResponseDto> getAllHourlyGroupStudents(Long classId);
    List<EnrollmentResponseDto> getAllHourlyIndividualStudents(Long classId);

    EnrollmentResponseDto enrollMonthlyWithStatus(EnrollmentWithStatusRequestDto request);
    EnrollmentResponseDto enrollHourlyGroupWithStatus(EnrollmentWithStatusRequestDto request);
}