package com.example.backend.dtos.enrollment;

import jakarta.validation.constraints.NotNull;

public class EnrollmentWithStatusRequestDto {
    @NotNull
    private Long classId;

    @NotNull
    private Long studentId;

    @NotNull
    private Boolean isActive;

    // Getters and Setters
    public Long getClassId() {
        return classId;
    }

    public void setClassId(Long classId) {
        this.classId = classId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}