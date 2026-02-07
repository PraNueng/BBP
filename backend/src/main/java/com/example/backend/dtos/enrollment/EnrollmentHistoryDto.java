package com.example.backend.dtos.enrollment;

import java.time.LocalDateTime;

public class EnrollmentHistoryDto {
    private Long id;
    private Boolean isActive;
    private String statusChangeReason;
    private String changedByNickname;
    private LocalDateTime statusChangedAt;

    public EnrollmentHistoryDto() {}

    public EnrollmentHistoryDto(Long id, Boolean isActive, String statusChangeReason,
                                String changedByNickname, LocalDateTime statusChangedAt) {
        this.id = id;
        this.isActive = isActive;
        this.statusChangeReason = statusChangeReason;
        this.changedByNickname = changedByNickname;
        this.statusChangedAt = statusChangedAt;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public String getStatusChangeReason() { return statusChangeReason; }
    public void setStatusChangeReason(String statusChangeReason) { this.statusChangeReason = statusChangeReason; }

    public String getChangedByNickname() { return changedByNickname; }
    public void setChangedByNickname(String changedByNickname) { this.changedByNickname = changedByNickname; }

    public LocalDateTime getStatusChangedAt() { return statusChangedAt; }
    public void setStatusChangedAt(LocalDateTime statusChangedAt) { this.statusChangedAt = statusChangedAt; }
}