package com.example.backend.entities.gradeprogression;

import com.example.backend.entities.Grade;
import com.example.backend.entities.student.Student;
import com.example.backend.entities.User;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "grade_progression_history", indexes = {
        @Index(name = "idx_student", columnList = "student_id"),
        @Index(name = "idx_progression_date", columnList = "progression_date"),
        @Index(name = "idx_academic_year", columnList = "academic_year")
})
public class GradeProgressionHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "old_grade_id", nullable = false)
    private Grade oldGrade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "new_grade_id", nullable = false)
    private Grade newGrade;

    @Column(name = "progression_date", nullable = false)
    private LocalDate progressionDate;

    @Column(name = "progression_type", length = 20)
    private String progressionType = "auto"; // 'auto', 'manual'

    @Column(name = "academic_year", length = 10)
    private String academicYear;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }

    public Grade getOldGrade() { return oldGrade; }
    public void setOldGrade(Grade oldGrade) { this.oldGrade = oldGrade; }

    public Grade getNewGrade() { return newGrade; }
    public void setNewGrade(Grade newGrade) { this.newGrade = newGrade; }

    public LocalDate getProgressionDate() { return progressionDate; }
    public void setProgressionDate(LocalDate progressionDate) {
        this.progressionDate = progressionDate;
    }

    public String getProgressionType() { return progressionType; }
    public void setProgressionType(String progressionType) {
        this.progressionType = progressionType;
    }

    public String getAcademicYear() { return academicYear; }
    public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public User getCreatedBy() { return createdBy; }
    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}