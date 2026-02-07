package com.example.backend.entities.student;

import com.example.backend.entities.Grade;
import com.example.backend.entities.User;
import com.example.backend.entities.hourly.HourlyGroupEnrollment;
import com.example.backend.entities.hourly.HourlyIndividualClass;
import com.example.backend.entities.monthly.MonthlyEnrollment;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "students", indexes = {
        @Index(name = "idx_name", columnList = "first_name, last_name"),
        @Index(name = "idx_grade_active", columnList = "grade_id, is_active"),
        @Index(name = "idx_active", columnList = "is_active"),
        @Index(name = "idx_school", columnList = "school_name")
})
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_code", nullable = false, unique = true, length = 50)
    private String studentCode;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", length = 100)
    private String lastName;

    @Column(length = 100)
    private String nickname;

    @Column(name = "school_name", length = 255)
    private String schoolName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grade_id", nullable = false)
    private Grade grade;

    @Column(name = "phone_student", length = 50)
    private String phoneStudent;

    @Column(name = "phone_parent", length = 50)
    private String phoneParent;

    @Column(name = "study_plan", length = 255)
    private String studyPlan;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    private Set<HourlyIndividualClass> hourlyIndividualClasses = new HashSet<>();

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    private Set<HourlyGroupEnrollment> hourlyGroupEnrollments = new HashSet<>();

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    private Set<MonthlyEnrollment> monthlyEnrollments = new HashSet<>();

    // Constructors
    public Student() {
    }

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
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStudentCode() {
        return studentCode;
    }

    public void setStudentCode(String studentCode) {
        this.studentCode = studentCode;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public Grade getGrade() {
        return grade;
    }

    public void setGrade(Grade grade) {
        this.grade = grade;
    }

    public String getPhoneStudent() {
        return phoneStudent;
    }

    public void setPhoneStudent(String phoneStudent) {
        this.phoneStudent = phoneStudent;
    }

    public String getPhoneParent() {
        return phoneParent;
    }

    public void setPhoneParent(String phoneParent) {
        this.phoneParent = phoneParent;
    }

    public String getStudyPlan() {
        return studyPlan;
    }

    public void setStudyPlan(String studyPlan) {
        this.studyPlan = studyPlan;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public Set<HourlyIndividualClass> getHourlyIndividualClasses() {
        return hourlyIndividualClasses;
    }

    public void setHourlyIndividualClasses(Set<HourlyIndividualClass> hourlyIndividualClasses) {
        this.hourlyIndividualClasses = hourlyIndividualClasses;
    }

    public Set<HourlyGroupEnrollment> getHourlyGroupEnrollments() {
        return hourlyGroupEnrollments;
    }

    public void setHourlyGroupEnrollments(Set<HourlyGroupEnrollment> hourlyGroupEnrollments) {
        this.hourlyGroupEnrollments = hourlyGroupEnrollments;
    }

    public Set<MonthlyEnrollment> getMonthlyEnrollments() {
        return monthlyEnrollments;
    }

    public void setMonthlyEnrollments(Set<MonthlyEnrollment> monthlyEnrollments) {
        this.monthlyEnrollments = monthlyEnrollments;
    }
}