package com.example.backend.entities.student;

import com.example.backend.entities.Receipt;
import com.example.backend.entities.Subject;
import com.example.backend.entities.User;
import com.example.backend.entities.monthly.MonthlySubtype;
import com.example.backend.entities.student.Student;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "student_course_purchases",
        indexes = {
                @Index(name = "idx_student", columnList = "student_id"),
                @Index(name = "idx_subject", columnList = "subject_id"),
                @Index(name = "idx_active", columnList = "is_active"),
                @Index(name = "idx_purchase_date", columnList = "purchase_date"),
                @Index(name = "idx_class_lookup", columnList = "class_type, class_id"),
                @Index(name = "idx_student_subject_active", columnList = "student_id, subject_id, is_active"),
                @Index(name = "idx_student_class", columnList = "student_id, class_type, class_id, is_active"),
                @Index(name = "idx_monthly_subtype", columnList = "monthly_subtype_id")
        }
)
public class StudentCoursePurchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* =========================
       RELATIONS
       ========================= */

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receipt_id")
    private Receipt receipt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    /* =========================
       CLASS INFO (optional)
       ========================= */

    @Column(name = "class_type", length = 50)
    private String classType;

    @Column(name = "class_id")
    private Long classId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "monthly_subtype_id")
    private MonthlySubtype monthlySubtype;

    /* =========================
       HOURS INFO
       ========================= */

    @Column(name = "hours_purchased", nullable = true, precision = 7, scale = 2)
    private BigDecimal hoursPurchased;

    @Column(name = "hours_completed", precision = 7, scale = 2)
    private BigDecimal hoursCompleted = BigDecimal.ZERO;

    /* =========================
       METADATA
       ========================= */

    @Column(name = "purchase_date", nullable = false)
    private LocalDate purchaseDate;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "deleted_reason", columnDefinition = "TEXT")
    private String deletedReason;

    @Column(name = "edit_reason", columnDefinition = "TEXT")
    private String editReason;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /* =========================
       LIFECYCLE
       ========================= */

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;

        if (this.hoursCompleted == null) {
            this.hoursCompleted = BigDecimal.ZERO;
        }
        if (this.isActive == null) {
            this.isActive = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /* =========================
       GETTERS & SETTERS
       ========================= */

    public Long getId() {
        return id;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public Receipt getReceipt() {
        return receipt;
    }

    public void setReceipt(Receipt receipt) {
        this.receipt = receipt;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public String getClassType() {
        return classType;
    }

    public void setClassType(String classType) {
        this.classType = classType;
    }

    public Long getClassId() {
        return classId;
    }

    public void setClassId(Long classId) {
        this.classId = classId;
    }

    public MonthlySubtype getMonthlySubtype() {
        return monthlySubtype;
    }

    public void setMonthlySubtype(MonthlySubtype monthlySubtype) {
        this.monthlySubtype = monthlySubtype;
    }

    public BigDecimal getHoursPurchased() {
        return hoursPurchased;
    }

    public void setHoursPurchased(BigDecimal hoursPurchased) {
        this.hoursPurchased = hoursPurchased;
    }

    public BigDecimal getHoursCompleted() {
        return hoursCompleted;
    }

    public void setHoursCompleted(BigDecimal hoursCompleted) {
        this.hoursCompleted = hoursCompleted;
    }

    public LocalDate getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(LocalDate purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getDeletedReason() {
        return deletedReason;
    }

    public void setDeletedReason(String deletedReason) {
        this.deletedReason = deletedReason;
    }

    public String getEditReason(){
        return editReason;
    }

    public void setEditReason(String editReason){
        this.editReason = editReason;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean active) {
        isActive = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
