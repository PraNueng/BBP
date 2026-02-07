package com.example.backend.dtos;

import com.example.backend.entities.Subject;

public class SubjectDto {
    private Long id;
    private String subjectName;
    private Boolean isActive;
    private Integer displayOrder;

    public SubjectDto() {}

    public SubjectDto(Long id, String subjectName, Boolean isActive, Integer displayOrder) {
        this.id = id;
        this.subjectName = subjectName;
        this.isActive = isActive;
        this.displayOrder = displayOrder;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    // Static factory method
    public static SubjectDto fromEntity(Subject entity) {
        SubjectDto dto = new SubjectDto();
        dto.setId(entity.getId());
        dto.setSubjectName(entity.getSubjectName());
        dto.setIsActive(entity.getIsActive());
        dto.setDisplayOrder(entity.getDisplayOrder());
        return dto;
    }
}