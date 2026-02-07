package com.example.backend.dtos;

import com.example.backend.entities.ScienceFormHistory;
import com.example.backend.entities.hourform.HourFormHistory;
import java.time.LocalDateTime;

public class ScienceFormHistoryDto {
    private Long id;
    private String fieldName;
    private String oldValue;
    private String newValue;
    private Long scienceFormId;
    private String updatedBy;
    private LocalDateTime updatedAt;

    public ScienceFormHistoryDto() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFieldName() { return fieldName; }
    public void setFieldName(String fieldName) { this.fieldName = fieldName; }

    public String getOldValue() { return oldValue; }
    public void setOldValue(String oldValue) { this.oldValue = oldValue; }

    public Long getScienceFormId() { return scienceFormId; }
    public void setScienceFormId(Long hourFormId) { this.scienceFormId = hourFormId; }

    public String getNewValue() { return newValue; }
    public void setNewValue(String newValue) { this.newValue = newValue; }

    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // Mapper method only - NO business logic
    public static ScienceFormHistoryDto fromEntity(ScienceFormHistory e) {
        ScienceFormHistoryDto d = new ScienceFormHistoryDto();
        d.setId(e.getId());
        d.setFieldName(e.getFieldName());
        d.setOldValue(e.getOldValue());
        d.setNewValue(e.getNewValue());

        if (e.getScienceForm() != null) {
            d.setScienceFormId(e.getScienceForm().getId());
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