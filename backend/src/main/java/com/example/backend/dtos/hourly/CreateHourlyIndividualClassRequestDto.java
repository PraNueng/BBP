package com.example.backend.dtos.hourly;

import com.example.backend.entities.Grade;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.List;

public class CreateHourlyIndividualClassRequestDto {

    private Long tutorId;

    private Long subjectId;

    private Long gradeId;

    @NotEmpty(message = "ต้องเลือกนักเรียนอย่างน้อย 1 คน")
    private List<Long> studentIds;

    private String className;

    // Constructors
    public CreateHourlyIndividualClassRequestDto() {}

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

    public List<Long> getStudentIds() {
        return studentIds;
    }

    public void setStudentIds(List<Long> studentIds) {
        this.studentIds = studentIds;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}