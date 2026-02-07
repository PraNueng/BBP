package com.example.backend.dtos.hourly;

import com.example.backend.entities.Grade;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public class UpdateHourlyIndividualClassRequestDto {

    private Long subjectId;
    private Long gradeId;
    private String className;

    @Positive(message = "Hours target must be positive")
    private BigDecimal hoursTarget;

    private Boolean isActive;

    // Constructors
    public UpdateHourlyIndividualClassRequestDto() {}

    // Getters and Setters
    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public Long getGradeId() {
        return gradeId;
    }

    public void setGradeId(Long gradeId) {
        this.gradeId = gradeId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public BigDecimal getHoursTarget() {
        return hoursTarget;
    }

    public void setHoursTarget(BigDecimal hoursTarget) {
        this.hoursTarget = hoursTarget;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}