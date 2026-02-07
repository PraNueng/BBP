package com.example.backend.dtos.enrollment;

import com.example.backend.entities.hourly.HourlyGroupEnrollment;
import com.example.backend.entities.monthly.MonthlyEnrollment;

import java.time.LocalDateTime;

public class EnrollmentResponseDto {
    private Long enrollmentId;
    private Long classId;
    private String className;
    private Long studentId;
    private String studentName;
    private String studentNickname;
    private LocalDateTime enrolledAt;
    private Boolean isActive;

    public EnrollmentResponseDto() {}

    public EnrollmentResponseDto(Long enrollmentId, Long classId, String className,
                                 Long studentId, String studentName, String studentNickname,
                                 LocalDateTime enrolledAt, Boolean isActive) {
        this.enrollmentId = enrollmentId;
        this.classId = classId;
        this.className = className;
        this.studentId = studentId;
        this.studentName = studentName;
        this.studentNickname = studentNickname;
        this.enrolledAt = enrolledAt;
        this.isActive = isActive;
    }

    // Getters and Setters
    public Long getEnrollmentId() {
        return enrollmentId;
    }

    public void setEnrollmentId(Long enrollmentId) {
        this.enrollmentId = enrollmentId;
    }

    public Long getClassId() {
        return classId;
    }

    public void setClassId(Long classId) {
        this.classId = classId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentNickname() {
        return studentNickname;
    }

    public void setStudentNickname(String studentNickname) {
        this.studentNickname = studentNickname;
    }

    public LocalDateTime getEnrolledAt() {
        return enrolledAt;
    }

    public void setEnrolledAt(LocalDateTime enrolledAt) {
        this.enrolledAt = enrolledAt;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public static EnrollmentResponseDto fromEntity(HourlyGroupEnrollment enrollment) {
        String studentName = enrollment.getStudent().getFirstName() + " " +
                (enrollment.getStudent().getLastName() != null ?
                        enrollment.getStudent().getLastName() : "");

        return new EnrollmentResponseDto(
                enrollment.getId(),
                enrollment.getHourlyGroupClass().getId(),
                enrollment.getHourlyGroupClass().getClassName(),
                enrollment.getStudent().getId(),
                studentName,
                enrollment.getStudent().getNickname(),
                enrollment.getEnrolledAt(),
                enrollment.getIsActive()
        );
    }

    public static EnrollmentResponseDto fromEntity(MonthlyEnrollment enrollment) {
        String studentName = enrollment.getStudent().getFirstName() + " " +
                (enrollment.getStudent().getLastName() != null ?
                        enrollment.getStudent().getLastName() : "");

        return new EnrollmentResponseDto(
                enrollment.getId(),
                enrollment.getMonthlyClass().getId(),
                enrollment.getMonthlyClass().getClassName(),
                enrollment.getStudent().getId(),
                studentName,
                enrollment.getStudent().getNickname(),
                enrollment.getEnrolledAt(),
                enrollment.getIsActive()
        );
    }
}