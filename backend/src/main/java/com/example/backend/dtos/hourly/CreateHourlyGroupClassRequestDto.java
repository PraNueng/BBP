package com.example.backend.dtos.hourly;

import com.example.backend.entities.Grade;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public class CreateHourlyGroupClassRequestDto {

    private Long tutorId;

    @NotNull(message = "Subject ID is required")
    private Long subjectId;

    private Long gradeId;

    @NotNull(message = "Subtype ID is required")
    private Long subtypeId;

    private String className;

    // Constructors
    public CreateHourlyGroupClassRequestDto() {}

    // Getters and Setters
    public Long getTutorId() {
        return tutorId;
    }

    public void setTutorId(Long tutorId) {
        this.tutorId = tutorId;
    }

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

    public Long getSubtypeId() {
        return subtypeId;
    }

    public void setSubtypeId(Long subtypeId) {
        this.subtypeId = subtypeId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}