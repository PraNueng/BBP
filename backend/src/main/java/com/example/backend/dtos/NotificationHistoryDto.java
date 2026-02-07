package com.example.backend.dtos;

import com.example.backend.entities.NotificationHistory;
import java.time.LocalDateTime;

public class NotificationHistoryDto {
    private Long id;
    private Long notificationId;
    private String action;
    private String fieldName;
    private String oldValue;
    private String newValue;
    private Long updatedById;
    private String updatedByUsername;
    private String updatedByNickname;
    private LocalDateTime updatedAt;

    public static NotificationHistoryDto fromEntity(NotificationHistory entity) {
        NotificationHistoryDto dto = new NotificationHistoryDto();
        dto.setId(entity.getId());
        dto.setNotificationId(entity.getNotificationId());
        dto.setAction(entity.getAction());
        dto.setFieldName(entity.getFieldName());
        dto.setOldValue(entity.getOldValue());
        dto.setNewValue(entity.getNewValue());
        dto.setUpdatedAt(entity.getUpdatedAt());

        if (entity.getUpdatedBy() != null) {
            dto.setUpdatedById(entity.getUpdatedBy().getId());
            dto.setUpdatedByUsername(entity.getUpdatedBy().getUsername());
            dto.setUpdatedByNickname(entity.getUpdatedBy().getNickname());
        }

        return dto;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getNotificationId() { return notificationId; }
    public void setNotificationId(Long notificationId) { this.notificationId = notificationId; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getFieldName() { return fieldName; }
    public void setFieldName(String fieldName) { this.fieldName = fieldName; }

    public String getOldValue() { return oldValue; }
    public void setOldValue(String oldValue) { this.oldValue = oldValue; }

    public String getNewValue() { return newValue; }
    public void setNewValue(String newValue) { this.newValue = newValue; }

    public Long getUpdatedById() { return updatedById; }
    public void setUpdatedById(Long updatedById) { this.updatedById = updatedById; }

    public String getUpdatedByUsername() { return updatedByUsername; }
    public void setUpdatedByUsername(String updatedByUsername) {
        this.updatedByUsername = updatedByUsername;
    }

    public String getUpdatedByNickname() { return updatedByNickname; }
    public void setUpdatedByNickname(String updatedByNickname) {
        this.updatedByNickname = updatedByNickname;
    }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}