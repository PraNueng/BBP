package com.example.backend.entities.hourform;

import com.example.backend.entities.Subject;
import com.example.backend.entities.User;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "hour_forms", indexes = {
        @Index(name = "idx_tutor_date", columnList = "tutor_id, teaching_date"),
        @Index(name = "idx_class", columnList = "class_type, class_id"),
        @Index(name = "idx_teaching_date", columnList = "teaching_date"),
        @Index(name = "idx_subject", columnList = "subject_id")
})
public class HourForm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tutor_id", nullable = false)
    private User tutor;

    @Column(name = "class_type", nullable = false, length = 50)
    private String classType; // 'hourly_group', 'hourly_individual'

    @Column(name = "class_id", nullable = false)
    private Long classId;

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

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "is_admin_created")
    private Boolean isAdminCreated = false;

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

    public String getClassType() { return classType; }
    public void setClassType(String classType) { this.classType = classType; }

    public Long getClassId() { return classId; }
    public void setClassId(Long classId) { this.classId = classId; }

    public Subject getSubject() { return subject; }
    public void setSubject(Subject subject) { this.subject = subject; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalDate getTeachingDate() { return teachingDate; }
    public void setTeachingDate(LocalDate teachingDate) { this.teachingDate = teachingDate; }

    public BigDecimal getHoursTaught() { return hoursTaught; }
    public void setHoursTaught(BigDecimal hoursTaught) { this.hoursTaught = hoursTaught; }

    public Integer getStudentsPresent() { return studentsPresent; }
    public void setStudentsPresent(Integer studentsPresent) {
        this.studentsPresent = studentsPresent;
    }

    public Integer getStudentsAbsent() { return studentsAbsent; }
    public void setStudentsAbsent(Integer studentsAbsent) {
        this.studentsAbsent = studentsAbsent;
    }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Boolean getIsAdminCreated() { return isAdminCreated; }
    public void setIsAdminCreated(Boolean isAdminCreated) { this.isAdminCreated = isAdminCreated; }
}