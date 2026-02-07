package com.example.backend.entities.hourly;

import com.example.backend.entities.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "hourly_individual_class_tutors",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_class_tutor", columnNames = {"class_id", "tutor_id"})
        },
        indexes = {
                @Index(name = "idx_class", columnList = "class_id"),
                @Index(name = "idx_tutor", columnList = "tutor_id"),
                @Index(name = "idx_assigned_at", columnList = "assigned_at")
        }
)
public class HourlyIndividualClassTutor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", nullable = false)
    private HourlyIndividualClass hourlyIndividualClass;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tutor_id", nullable = false)
    private User tutor;

    @Column(name = "assigned_at")
    private LocalDateTime assignedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_by")
    private User assignedBy;

    @PrePersist
    protected void onCreate() {
        if (assignedAt == null) {
            assignedAt = LocalDateTime.now();
        }
    }

    // Constructors
    public HourlyIndividualClassTutor() {}

    public HourlyIndividualClassTutor(HourlyIndividualClass hourlyIndividualClass, User tutor) {
        this.hourlyIndividualClass = hourlyIndividualClass;
        this.tutor = tutor;
        this.assignedAt = LocalDateTime.now();
    }

    public HourlyIndividualClassTutor(HourlyIndividualClass hourlyIndividualClass, User tutor, User assignedBy) {
        this.hourlyIndividualClass = hourlyIndividualClass;
        this.tutor = tutor;
        this.assignedBy = assignedBy;
        this.assignedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public HourlyIndividualClass getHourlyIndividualClass() { return hourlyIndividualClass; }
    public void setHourlyIndividualClass(HourlyIndividualClass hourlyIndividualClass) {
        this.hourlyIndividualClass = hourlyIndividualClass;
    }

    public User getTutor() { return tutor; }
    public void setTutor(User tutor) { this.tutor = tutor; }

    public LocalDateTime getAssignedAt() { return assignedAt; }
    public void setAssignedAt(LocalDateTime assignedAt) { this.assignedAt = assignedAt; }

    public User getAssignedBy() { return assignedBy; }
    public void setAssignedBy(User assignedBy) { this.assignedBy = assignedBy; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HourlyIndividualClassTutor)) return false;
        HourlyIndividualClassTutor that = (HourlyIndividualClassTutor) o;
        return hourlyIndividualClass != null && hourlyIndividualClass.equals(that.hourlyIndividualClass) &&
                tutor != null && tutor.equals(that.tutor);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}