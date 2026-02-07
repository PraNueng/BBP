package com.example.backend.entities.hourly;

import com.example.backend.dtos.hourly.HourlyIndividualClassDto;
import com.example.backend.entities.Grade;
import com.example.backend.entities.Subject;
import com.example.backend.entities.User;
import com.example.backend.entities.student.Student;
import com.example.backend.repositories.hourly.HourlyIndividualClassStudentRepository;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "hourly_individual_classes", indexes = {
        @Index(name = "idx_student", columnList = "student_id"),
        @Index(name = "idx_tutor", columnList = "tutor_id"),
        @Index(name = "idx_subject", columnList = "subject_id"),
        @Index(name = "idx_active", columnList = "is_active")
})
public class HourlyIndividualClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = true)
    private Student student;

    @OneToMany(mappedBy = "hourlyIndividualClass", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HourlyIndividualClassStudent> classStudents = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id")
    private Subject subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grade_id")
    private Grade grade;

    @Column(name = "class_name", length = 255)
    private String className;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "note", length = 500)
    private String note;

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

    // Static method สำหรับแปลงเป็น DTO
    public static HourlyIndividualClassDto toDto(HourlyIndividualClass entity) {
        if (entity == null) return null;

        HourlyIndividualClassDto dto = new HourlyIndividualClassDto();
        dto.setId(entity.getId());
        dto.setClassName(entity.getClassName());
        dto.setIsActive(entity.getIsActive());
        dto.setNote(entity.getNote());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        // นับนักเรียนทั้งหมด (รวม inactive) เพื่อใช้กำหนดว่าคลาสนี้เป็น PV-เดี่ยว หรือ PV-กลุ่ม
        long totalStudentsEver = entity.getTotalStudentsEverEnrolled();
        dto.setTotalStudents(totalStudentsEver);

        if (!entity.getClassStudents().isEmpty()) {
            Student firstStudent = entity.getClassStudents().get(0).getStudent();
            dto.setStudentId(firstStudent.getId());
            dto.setStudentNickname(firstStudent.getNickname());
            dto.setStudentFirstName(firstStudent.getFirstName());
            dto.setStudentLastName(firstStudent.getLastName());
            dto.setStudentCode(firstStudent.getStudentCode());

            // ถ้ามีหลายคน แสดงจำนวน
            if (totalStudentsEver > 1) {
                dto.setStudentName(firstStudent.getNickname() + " และอีก " + (totalStudentsEver - 1) + " คน");
            } else {
                dto.setStudentName(firstStudent.getFirstName() + " " + firstStudent.getLastName());
            }
        }

        // Subject, Grade, CreatedBy ไม่เปลี่ยน
        return dto;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }

    public List<HourlyIndividualClassStudent> getClassStudents() { return classStudents; }
    public void setClassStudents(List<HourlyIndividualClassStudent> classStudents) {
        this.classStudents = classStudents;
    }

    public Subject getSubject() { return subject; }
    public void setSubject(Subject subject) { this.subject = subject; }

    public Grade getGrade() { return grade; }
    public void setGrade(Grade grade) { this.grade = grade; }

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

    /**
     * นับจำนวนนักเรียนทั้งหมด (รวม inactive) ที่เคยอยู่ในคลาส
     */
    @Transient
    public Long getTotalStudentsEverEnrolled() {
        return classStudents != null ? (long) classStudents.size() : 0L;
    }

    /**
     * นับจำนวนนักเรียนทั้งหมดที่เคยอยู่ในคลาส (รวม inactive)
     * โดยดึงจาก database จริง แทนที่จะนับจาก list ที่อาจถูก filter
     */
    @Transient
    public Long getTotalStudentsEverEnrolledFromDatabase(HourlyIndividualClassStudentRepository repository) {
        return repository.countAllByClassId(this.id);
    }
}