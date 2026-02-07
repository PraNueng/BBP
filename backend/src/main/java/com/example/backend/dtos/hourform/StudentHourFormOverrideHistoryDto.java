package com.example.backend.dtos.hourform;

import com.example.backend.entities.hourform.StudentHourFormOverrideHistory;
import java.time.LocalDateTime;

public class StudentHourFormOverrideHistoryDto {
    private Long id;
    private String fieldName;
    private String oldValue;
    private String newValue;
    private String updatedBy;
    private LocalDateTime updatedAt;
    private String action;

    public static StudentHourFormOverrideHistoryDto fromEntity(StudentHourFormOverrideHistory entity) {
        StudentHourFormOverrideHistoryDto dto = new StudentHourFormOverrideHistoryDto();
        dto.setId(entity.getId());
        dto.setFieldName(entity.getFieldName());
        dto.setOldValue(entity.getOldValue());
        dto.setNewValue(entity.getNewValue());
        dto.setUpdatedBy(entity.getUpdatedBy().getNickname());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setAction(entity.getAction());
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}