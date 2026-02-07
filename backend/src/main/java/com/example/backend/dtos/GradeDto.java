package com.example.backend.dtos;

import com.example.backend.entities.Grade;

public class GradeDto {
    private Long id;
    private String gradeName;
    private Integer gradeOrder;
    private Long nextGradeId;
    private String nextGradeName;

    public GradeDto() {}

    public GradeDto(Long id, String gradeName, Integer gradeOrder, Long nextGradeId, String nextGradeName) {
        this.id = id;
        this.gradeName = gradeName;
        this.gradeOrder = gradeOrder;
        this.nextGradeId = nextGradeId;
        this.nextGradeName = nextGradeName;
    }

    // Static factory method
    public static GradeDto fromEntity(Grade entity) {
        GradeDto dto = new GradeDto();
        dto.setId(entity.getId());
        dto.setGradeName(entity.getGradeName());
        dto.setGradeOrder(entity.getGradeOrder());

        if (entity.getNextGrade() != null) {
            dto.setNextGradeId(entity.getNextGrade().getId());
            dto.setNextGradeName(entity.getNextGrade().getGradeName());
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

    public String getGradeName() {
        return gradeName;
    }

    public void setGradeName(String gradeName) {
        this.gradeName = gradeName;
    }

    public Integer getGradeOrder() {
        return gradeOrder;
    }

    public void setGradeOrder(Integer gradeOrder) {
        this.gradeOrder = gradeOrder;
    }

    public Long getNextGradeId() {
        return nextGradeId;
    }

    public void setNextGradeId(Long nextGradeId) {
        this.nextGradeId = nextGradeId;
    }

    public String getNextGradeName() {
        return nextGradeName;
    }

    public void setNextGradeName(String nextGradeName) {
        this.nextGradeName = nextGradeName;
    }
}