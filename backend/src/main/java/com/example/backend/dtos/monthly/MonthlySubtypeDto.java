package com.example.backend.dtos.monthly;

import com.example.backend.entities.monthly.MonthlySubtype;
import java.time.LocalDateTime;

public class MonthlySubtypeDto {
    private Long id;
    private String subtypeName;
    private Boolean isActive;
    private Integer displayOrder;
    private LocalDateTime createdAt;

    public MonthlySubtypeDto() {}

    public MonthlySubtypeDto(Long id, String subtypeName, Boolean isActive, Integer displayOrder) {
        this.id = id;
        this.subtypeName = subtypeName;
        this.isActive = isActive;
        this.displayOrder = displayOrder;
    }

    // Static factory method
    public static MonthlySubtypeDto fromEntity(MonthlySubtype entity) {
        MonthlySubtypeDto dto = new MonthlySubtypeDto();
        dto.setId(entity.getId());
        dto.setSubtypeName(entity.getSubtypeName());
        dto.setIsActive(entity.getIsActive());
        dto.setDisplayOrder(entity.getDisplayOrder());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSubtypeName() {
        return subtypeName;
    }

    public void setSubtypeName(String subtypeName) {
        this.subtypeName = subtypeName;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}