package com.example.backend.dtos.enrollment;

import jakarta.validation.constraints.NotNull;

public class EnrollmentRequestDto {
    @NotNull(message = "Class ID is required")
    private Long classId;

    @NotNull(message = "Student ID is required")
    private Long studentId;

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
}