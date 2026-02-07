package com.example.backend.entities.gradeprogression;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "grade_progression_config")
public class GradeProgressionConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "progression_month", nullable = false)
    private Integer progressionMonth = 5;

    @Column(name = "progression_day", nullable = false)
    private Integer progressionDay = 1;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getProgressionMonth() { return progressionMonth; }
    public void setProgressionMonth(Integer progressionMonth) {
        this.progressionMonth = progressionMonth;
    }

    public Integer getProgressionDay() { return progressionDay; }
    public void setProgressionDay(Integer progressionDay) {
        this.progressionDay = progressionDay;
    }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}