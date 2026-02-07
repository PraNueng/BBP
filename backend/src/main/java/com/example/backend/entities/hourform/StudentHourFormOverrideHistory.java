package com.example.backend.entities.hourform;

import com.example.backend.entities.User;
import com.example.backend.entities.student.Student;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "student_hour_form_override_history", indexes = {
        @Index(name = "idx_override_id", columnList = "override_id"),
        @Index(name = "idx_student_id", columnList = "student_id"),
        @Index(name = "idx_hour_form_id", columnList = "hour_form_id"),
        @Index(name = "idx_updated_at", columnList = "updated_at")
})
public class StudentHourFormOverrideHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "override_id", nullable = false)
    private StudentHourFormOverride override;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hour_form_id", nullable = false)
    private HourForm hourForm;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(name = "field_name", nullable = false, length = 255)
    private String fieldName;

    @Column(name = "old_value", columnDefinition = "TEXT")
    private String oldValue;

    @Column(name = "new_value", columnDefinition = "TEXT")
    private String newValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by", nullable = false)
    private User updatedBy;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "action", length = 50)
    private String action;

    @PrePersist
    protected void onCreate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public StudentHourFormOverride getOverride() {
        return override;
    }

    public void setOverride(StudentHourFormOverride override) {
        this.override = override;
    }

    public HourForm getHourForm() {
        return hourForm;
    }

    public void setHourForm(HourForm hourForm) {
        this.hourForm = hourForm;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
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

    public User getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(User updatedBy) {
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