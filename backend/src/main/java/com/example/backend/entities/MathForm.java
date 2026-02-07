package com.example.backend.entities;

import com.example.backend.entities.Subject;
import com.example.backend.entities.User;
import com.example.backend.entities.monthly.MonthlyClass;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "math_forms", indexes = {
        @Index(name = "idx_tutor", columnList = "tutor_id"),
        @Index(name = "idx_class", columnList = "class_id"),
        @Index(name = "idx_teaching_date", columnList = "teaching_date"),
        @Index(name = "idx_tutor_date", columnList = "tutor_id, teaching_date")
})
public class MathForm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tutor_id", nullable = false)
    private User tutor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id", nullable = false)
    private MonthlyClass monthlyClass;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "teaching_date", nullable = false)
    private LocalDate teachingDate;

    @Column(name = "hours_taught", nullable = false, precision = 7, scale = 2)
    private BigDecimal hoursTaught;

    @Column(name = "students_present", nullable = false)
    private Integer studentsPresent = 0;

    @Column(name = "students_absent", nullable = false)
    private Integer studentsAbsent = 0;

    @Column(columnDefinition = "TEXT")
    private String remark;

    @Column(name = "is_admin_created")
    private Boolean isAdminCreated = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

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

    public User getTutor() { return tutor; }
    public void setTutor(User tutor) { this.tutor = tutor; }

    public MonthlyClass getMonthlyClass() { return monthlyClass; }
    public void setMonthlyClass(MonthlyClass monthlyClass) { this.monthlyClass = monthlyClass; }

    public Subject getSubject() { return subject; }
    public void setSubject(Subject subject) { this.subject = subject; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalDate getTeachingDate() { return teachingDate; }
    public void setTeachingDate(LocalDate teachingDate) { this.teachingDate = teachingDate; }

    public BigDecimal getHoursTaught() { return hoursTaught; }
    public void setHoursTaught(BigDecimal hoursTaught) { this.hoursTaught = hoursTaught; }

    public Integer getStudentsPresent() { return studentsPresent; }
    public void setStudentsPresent(Integer studentsPresent) { this.studentsPresent = studentsPresent; }

    public Integer getStudentsAbsent() { return studentsAbsent; }
    public void setStudentsAbsent(Integer studentsAbsent) { this.studentsAbsent = studentsAbsent; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    public Boolean getIsAdminCreated() { return isAdminCreated; }
    public void setIsAdminCreated(Boolean isAdminCreated) { this.isAdminCreated = isAdminCreated; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public User getCreatedBy() { return createdBy; }
    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }
}