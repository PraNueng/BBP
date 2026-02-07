package com.example.backend.dtos.monthly;

import com.example.backend.entities.monthly.MonthlyEnrollment;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class MonthlyEnrollmentDto {
    private Long id;
    private Long classId;
    private String className;
    private String classDisplayName;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long studentId;
    private String studentCode;
    private String studentName;
    private String studentNickname;
    private String gradeName;
    private LocalDateTime enrolledAt;

    public MonthlyEnrollmentDto() {}

    // Static factory method
    public static MonthlyEnrollmentDto fromEntity(MonthlyEnrollment entity) {
        MonthlyEnrollmentDto dto = new MonthlyEnrollmentDto();
        dto.setId(entity.getId());
        dto.setEnrolledAt(entity.getEnrolledAt());

        // Class info
        if (entity.getMonthlyClass() != null) {
            dto.setClassId(entity.getMonthlyClass().getId());
            dto.setClassName(entity.getMonthlyClass().getClassName());
            dto.setStartDate(entity.getMonthlyClass().getStartDate());
            dto.setEndDate(entity.getMonthlyClass().getEndDate());
        }

        // Student info
        if (entity.getStudent() != null) {
            dto.setStudentId(entity.getStudent().getId());
            dto.setStudentCode(entity.getStudent().getStudentCode());
            dto.setStudentNickname(entity.getStudent().getNickname());

            String fullName = entity.getStudent().getFirstName();
            if (entity.getStudent().getLastName() != null && !entity.getStudent().getLastName().isEmpty()) {
                fullName += " " + entity.getStudent().getLastName();
            }
            dto.setStudentName(fullName);

            if (entity.getStudent().getGrade() != null) {
                dto.setGradeName(entity.getStudent().getGrade().getGradeName());
            }
        }

        return dto;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getClassDisplayName() {
        return classDisplayName;
    }

    public void setClassDisplayName(String classDisplayName) {
        this.classDisplayName = classDisplayName;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getStudentCode() {
        return studentCode;
    }

    public void setStudentCode(String studentCode) {
        this.studentCode = studentCode;
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

    public String getGradeName() {
        return gradeName;
    }

    public void setGradeName(String gradeName) {
        this.gradeName = gradeName;
    }

    public LocalDateTime getEnrolledAt() {
        return enrolledAt;
    }

    public void setEnrolledAt(LocalDateTime enrolledAt) {
        this.enrolledAt = enrolledAt;
    }
}