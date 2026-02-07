package com.example.backend.dtos.hourly;

import com.example.backend.entities.hourly.HourlyGroupEnrollment;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class HourlyGroupEnrollmentDto {
    private Long id;
    private Long classId;
    private String className;
    private String classDisplayName;
    private Long studentId;
    private String studentCode;
    private String studentName;
    private String studentNickname;
    private String gradeName;
    private BigDecimal hoursCompleted;
    private BigDecimal hoursTarget;
    private BigDecimal completionPercentage;
    private Integer milestonesReached;
    private LocalDateTime enrolledAt;

    public HourlyGroupEnrollmentDto() {}

    // Static factory method
    public static HourlyGroupEnrollmentDto fromEntity(HourlyGroupEnrollment entity) {
        HourlyGroupEnrollmentDto dto = new HourlyGroupEnrollmentDto();
        dto.setId(entity.getId());
        dto.setEnrolledAt(entity.getEnrolledAt());

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

    public BigDecimal getHoursCompleted() {
        return hoursCompleted;
    }

    public void setHoursCompleted(BigDecimal hoursCompleted) {
        this.hoursCompleted = hoursCompleted;
    }

    public BigDecimal getHoursTarget() {
        return hoursTarget;
    }

    public void setHoursTarget(BigDecimal hoursTarget) {
        this.hoursTarget = hoursTarget;
    }

    public BigDecimal getCompletionPercentage() {
        return completionPercentage;
    }

    public void setCompletionPercentage(BigDecimal completionPercentage) {
        this.completionPercentage = completionPercentage;
    }

    public Integer getMilestonesReached() {
        return milestonesReached;
    }

    public void setMilestonesReached(Integer milestonesReached) {
        this.milestonesReached = milestonesReached;
    }

    public LocalDateTime getEnrolledAt() {
        return enrolledAt;
    }

    public void setEnrolledAt(LocalDateTime enrolledAt) {
        this.enrolledAt = enrolledAt;
    }
}