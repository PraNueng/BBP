package com.example.backend.dtos;

import com.example.backend.entities.gradeprogression.GradeProgressionConfig;

import java.time.LocalDateTime;

public class GradeProgressionConfigDto {
    private Long id;
    private Integer progressionMonth;
    private Integer progressionDay;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public GradeProgressionConfigDto() {}

    public static GradeProgressionConfigDto fromEntity(GradeProgressionConfig e) {
        GradeProgressionConfigDto dto = new GradeProgressionConfigDto();
        dto.setId(e.getId());
        dto.setProgressionMonth(e.getProgressionMonth());
        dto.setProgressionDay(e.getProgressionDay());
        dto.setIsActive(e.getIsActive());
        dto.setCreatedAt(e.getCreatedAt());
        dto.setUpdatedAt(e.getUpdatedAt());
        return dto;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getProgressionMonth() { return progressionMonth; }
    public void setProgressionMonth(Integer progressionMonth) { this.progressionMonth = progressionMonth; }

    public Integer getProgressionDay() { return progressionDay; }
    public void setProgressionDay(Integer progressionDay) { this.progressionDay = progressionDay; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
