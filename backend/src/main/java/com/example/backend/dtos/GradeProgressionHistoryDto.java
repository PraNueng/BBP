package com.example.backend.dtos;

import com.example.backend.entities.gradeprogression.GradeProgressionHistory;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class GradeProgressionHistoryDto {
    private Long id;
    private Long studentId;
    private String studentCode;
    private String studentName;
    private Long oldGradeId;
    private String oldGradeName;
    private Long newGradeId;
    private String newGradeName;
    private LocalDate progressionDate;
    private String progressionType; // 'auto', 'manual'
    private String academicYear;
    private String notes;
    private Long createdById;
    private String createdByName;
    private LocalDateTime createdAt;

    public GradeProgressionHistoryDto() {}

    // Static factory method
    public static GradeProgressionHistoryDto fromEntity(GradeProgressionHistory entity) {
        GradeProgressionHistoryDto dto = new GradeProgressionHistoryDto();
        dto.setId(entity.getId());
        dto.setProgressionDate(entity.getProgressionDate());
        dto.setProgressionType(entity.getProgressionType());
        dto.setAcademicYear(entity.getAcademicYear());
        dto.setNotes(entity.getNotes());
        dto.setCreatedAt(entity.getCreatedAt());

        // Student info
        if (entity.getStudent() != null) {
            dto.setStudentId(entity.getStudent().getId());
            dto.setStudentCode(entity.getStudent().getStudentCode());

            String fullName = entity.getStudent().getFirstName();
            if (entity.getStudent().getLastName() != null && !entity.getStudent().getLastName().isEmpty()) {
                fullName += " " + entity.getStudent().getLastName();
            }
            dto.setStudentName(fullName);
        }

        // Old grade
        if (entity.getOldGrade() != null) {
            dto.setOldGradeId(entity.getOldGrade().getId());
            dto.setOldGradeName(entity.getOldGrade().getGradeName());
        }

        // New grade
        if (entity.getNewGrade() != null) {
            dto.setNewGradeId(entity.getNewGrade().getId());
            dto.setNewGradeName(entity.getNewGrade().getGradeName());
        }

        // Created by
        if (entity.getCreatedBy() != null) {
            dto.setCreatedById(entity.getCreatedBy().getId());
            dto.setCreatedByName(entity.getCreatedBy().getNickname());
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

    public Long getOldGradeId() {
        return oldGradeId;
    }

    public void setOldGradeId(Long oldGradeId) {
        this.oldGradeId = oldGradeId;
    }

    public String getOldGradeName() {
        return oldGradeName;
    }

    public void setOldGradeName(String oldGradeName) {
        this.oldGradeName = oldGradeName;
    }

    public Long getNewGradeId() {
        return newGradeId;
    }

    public void setNewGradeId(Long newGradeId) {
        this.newGradeId = newGradeId;
    }

    public String getNewGradeName() {
        return newGradeName;
    }

    public void setNewGradeName(String newGradeName) {
        this.newGradeName = newGradeName;
    }

    public LocalDate getProgressionDate() {
        return progressionDate;
    }

    public void setProgressionDate(LocalDate progressionDate) {
        this.progressionDate = progressionDate;
    }

    public String getProgressionType() {
        return progressionType;
    }

    public void setProgressionType(String progressionType) {
        this.progressionType = progressionType;
    }

    public String getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Long getCreatedById() {
        return createdById;
    }

    public void setCreatedById(Long createdById) {
        this.createdById = createdById;
    }

    public String getCreatedByName() {
        return createdByName;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}