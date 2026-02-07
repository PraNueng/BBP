package com.example.backend.entities.monthly;

import com.example.backend.entities.User;
import com.example.backend.entities.student.Student;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "monthly_enrollments",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_class_student", columnNames = {"class_id", "student_id"})
        },
        indexes = {
                @Index(name = "idx_class", columnList = "class_id"),
                @Index(name = "idx_student", columnList = "student_id"),
                @Index(name = "idx_active", columnList = "is_active"),
                @Index(name = "idx_status_changed_at", columnList = "status_changed_at")
        }
)
public class MonthlyEnrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", nullable = false)
    private MonthlyClass monthlyClass;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(name = "enrolled_at")
    private LocalDateTime enrolledAt;

    @Column(name = "enrolled_grade_id")
    private Long enrolledGradeId;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "status_change_reason", columnDefinition = "TEXT")
    private String statusChangeReason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_changed_by")
    private User statusChangedBy;

    @Column(name = "status_changed_at")
    private LocalDateTime statusChangedAt;

    @PrePersist
    protected void onCreate() {
        enrolledAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public MonthlyClass getMonthlyClass() { return monthlyClass; }
    public void setMonthlyClass(MonthlyClass monthlyClass) {
        this.monthlyClass = monthlyClass;
    }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }

    public LocalDateTime getEnrolledAt() { return enrolledAt; }
    public void setEnrolledAt(LocalDateTime enrolledAt) { this.enrolledAt = enrolledAt; }

    public Long getEnrolledGradeId() {
        return enrolledGradeId;
    }

    public void setEnrolledGradeId(Long enrolledGradeId) {
        this.enrolledGradeId = enrolledGradeId;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getStatusChangeReason() {
        return statusChangeReason;
    }
    public void setStatusChangeReason(String statusChangeReason) {
        this.statusChangeReason = statusChangeReason;
    }

    public User getStatusChangedBy() {
        return statusChangedBy;
    }
    public void setStatusChangedBy(User statusChangedBy) {
        this.statusChangedBy = statusChangedBy;
    }

    public LocalDateTime getStatusChangedAt() {
        return statusChangedAt;
    }
    public void setStatusChangedAt(LocalDateTime statusChangedAt) {
        this.statusChangedAt = statusChangedAt;
    }
}