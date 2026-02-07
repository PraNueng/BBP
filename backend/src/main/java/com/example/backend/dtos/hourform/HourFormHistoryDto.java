package com.example.backend.dtos.hourform;

import com.example.backend.entities.hourform.HourFormHistory;
import java.time.LocalDateTime;

public class HourFormHistoryDto {
    private Long id;
    private String fieldName;
    private String oldValue;
    private String newValue;
    private Long hourFormId;
    private String updatedBy;
    private LocalDateTime updatedAt;

    public HourFormHistoryDto() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFieldName() { return fieldName; }
    public void setFieldName(String fieldName) { this.fieldName = fieldName; }

    public String getOldValue() { return oldValue; }
    public void setOldValue(String oldValue) { this.oldValue = oldValue; }

    public Long getHourFormId() { return hourFormId; }
    public void setHourFormId(Long hourFormId) { this.hourFormId = hourFormId; }

    public String getNewValue() { return newValue; }
    public void setNewValue(String newValue) { this.newValue = newValue; }

    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // Mapper method only - NO business logic
    public static HourFormHistoryDto fromEntity(HourFormHistory e) {
        HourFormHistoryDto d = new HourFormHistoryDto();
        d.setId(e.getId());
        d.setFieldName(e.getFieldName());
        d.setOldValue(e.getOldValue());
        d.setNewValue(e.getNewValue());

        if (e.getHourForm() != null) {
            d.setHourFormId(e.getHourForm().getId());
        }

        d.setUpdatedAt(e.getUpdatedAt());

        if (e.getUpdatedBy() != null) {
            d.setUpdatedBy(e.getUpdatedBy().getNickname() != null
                    ? e.getUpdatedBy().getNickname()
                    : e.getUpdatedBy().getUsername());
        }

        return d;
    }
}