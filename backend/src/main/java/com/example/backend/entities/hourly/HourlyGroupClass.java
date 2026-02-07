package com.example.backend.entities.hourly;

import com.example.backend.entities.Grade;
import com.example.backend.entities.Subject;
import com.example.backend.entities.User;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "hourly_group_classes", indexes = {
        @Index(name = "idx_subject", columnList = "subject_id"),
        @Index(name = "idx_subtype", columnList = "subtype_id"),
        @Index(name = "idx_active", columnList = "is_active")
})
public class HourlyGroupClass {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grade_id")
    private Grade grade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subtype_id", nullable = false)
    private HourlyGroupSubtype subtype;

    @Column(name = "class_name", length = 255)
    private String className;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "note", length = 255)
    private String note;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @OneToMany(mappedBy = "hourlyGroupClass", cascade = CascadeType.ALL)
    private Set<HourlyGroupEnrollment> enrollments = new HashSet<>();

    @OneToMany(mappedBy = "hourlyGroupClass", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<HourlyGroupClassTutor> classTutors = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void addTutor(User tutor, User assignedBy) {
        HourlyGroupClassTutor classTutor = new HourlyGroupClassTutor(this, tutor, assignedBy);
        classTutors.add(classTutor);
    }

    public void addTutor(User tutor) {
        HourlyGroupClassTutor classTutor = new HourlyGroupClassTutor(this, tutor);
        classTutors.add(classTutor);
    }

    public void removeTutor(User tutor) {
        classTutors.removeIf(ct -> ct.getTutor().equals(tutor));
    }

    public boolean hasTutor(User tutor) {
        return classTutors.stream()
                .anyMatch(ct -> ct.getTutor().equals(tutor));
    }

    public Set<User> getTutors() {
        Set<User> tutors = new HashSet<>();
        for (HourlyGroupClassTutor ct : classTutors) {
            tutors.add(ct.getTutor());
        }
        return tutors;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Subject getSubject() { return subject; }
    public void setSubject(Subject subject) { this.subject = subject; }

    public Grade getGrade() { return grade; }
    public void setGrade(Grade grade) { this.grade = grade; }

    public HourlyGroupSubtype getSubtype() { return subtype; }
    public void setSubtype(HourlyGroupSubtype subtype) { this.subtype = subtype; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public User getCreatedBy() { return createdBy; }
    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }

    public Set<HourlyGroupEnrollment> getEnrollments() { return enrollments; }
    public void setEnrollments(Set<HourlyGroupEnrollment> enrollments) {
        this.enrollments = enrollments;
    }

    public Set<HourlyGroupClassTutor> getClassTutors() { return classTutors; }
    public void setClassTutors(Set<HourlyGroupClassTutor> classTutors) {
        this.classTutors = classTutors;
    }
}