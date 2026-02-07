package com.example.backend.dtos;

import com.example.backend.entities.MathFormHistory;
import java.time.LocalDateTime;

public class MathFormHistoryDto {
    private Long id;
    private String fieldName;
    private String oldValue;
    private String newValue;
    private Long mathFormId;
    private String updatedBy;
    private LocalDateTime updatedAt;

    public MathFormHistoryDto() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFieldName() { return fieldName; }
    public void setFieldName(String fieldName) { this.fieldName = fieldName; }

    public String getOldValue() { return oldValue; }
    public void setOldValue(String oldValue) { this.oldValue = oldValue; }

    public Long getMathFormId() { return mathFormId; }
    public void setMathFormId(Long mathFormId) { this.mathFormId = mathFormId; }

    public String getNewValue() { return newValue; }
    public void setNewValue(String newValue) { this.newValue = newValue; }

    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // Mapper method only - NO business logic
    public static MathFormHistoryDto fromEntity(MathFormHistory e) {
        MathFormHistoryDto d = new MathFormHistoryDto();
        d.setId(e.getId());
        d.setFieldName(e.getFieldName());
        d.setOldValue(e.getOldValue());
        d.setNewValue(e.getNewValue());

        if (e.getMathForm() != null) {
            d.setMathFormId(e.getMathForm().getId());
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