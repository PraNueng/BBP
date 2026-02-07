package com.example.backend.dtos.hourform;

import com.example.backend.entities.hourform.HourForm;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class HourFormDto {
    private Long id;
    private Long tutorId;
    private String tutorName;
    private String classType; // 'hourly_group', 'hourly_individual'
    private Long classId;
    private String className;
    private String classDisplayName;
    private String grade;
    private Long subjectId;
    private String subjectName;
    private String content;
    private LocalDate teachingDate;
    private BigDecimal hoursTaught;
    private Integer studentsPresent;
    private Integer studentsAbsent;
    private String remark;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean wasStudentActive;
    private String remarkOverride;
    private Boolean isAdminCreated;

    public HourFormDto() {}

    // Static factory method
    public static HourFormDto fromEntity(HourForm entity) {
        HourFormDto dto = new HourFormDto();
        dto.setId(entity.getId());
        dto.setClassType(entity.getClassType());
        dto.setClassId(entity.getClassId());
        dto.setContent(entity.getContent());
        dto.setTeachingDate(entity.getTeachingDate());
        dto.setHoursTaught(entity.getHoursTaught());
        dto.setStudentsPresent(entity.getStudentsPresent());
        dto.setStudentsAbsent(entity.getStudentsAbsent());
        dto.setRemark(entity.getRemark());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setIsAdminCreated(entity.getIsAdminCreated());

        // Tutor
        if (entity.getTutor() != null) {
            dto.setTutorId(entity.getTutor().getId());
            dto.setTutorName(entity.getTutor().getNickname());
        }

        // Subject
        if (entity.getSubject() != null) {
            dto.setSubjectId(entity.getSubject().getId());
            dto.setSubjectName(entity.getSubject().getSubjectName());
        }

        return dto;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTutorId() {
        return tutorId;
    }

    public void setTutorId(Long tutorId) {
        this.tutorId = tutorId;
    }

    public String getTutorName() {
        return tutorName;
    }

    public void setTutorName(String tutorName) {
        this.tutorName = tutorName;
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

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassDisplayName() {
        return classDisplayName;
    }

    public void setClassDisplayName(String classDisplayName) {
        this.classDisplayName = classDisplayName;
    }

    public String getGrade() { return grade; }

    public void setGrade(String grade) { this.grade = grade; }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDate getTeachingDate() {
        return teachingDate;
    }

    public void setTeachingDate(LocalDate teachingDate) {
        this.teachingDate = teachingDate;
    }

    public BigDecimal getHoursTaught() {
        return hoursTaught;
    }

    public void setHoursTaught(BigDecimal hoursTaught) {
        this.hoursTaught = hoursTaught;
    }

    public Integer getStudentsPresent() {
        return studentsPresent;
    }

    public void setStudentsPresent(Integer studentsPresent) {
        this.studentsPresent = studentsPresent;
    }

    public Integer getStudentsAbsent() {
        return studentsAbsent;
    }

    public void setStudentsAbsent(Integer studentsAbsent) {
        this.studentsAbsent = studentsAbsent;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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

    public Boolean getWasStudentActive() { return wasStudentActive; }
    public void setWasStudentActive(Boolean wasStudentActive) { this.wasStudentActive = wasStudentActive; }

    public String getRemarkOverride() { return remarkOverride; }

    public void setRemarkOverride(String remarkOverride) { this.remarkOverride = remarkOverride; }

    public Boolean getIsAdminCreated() { return isAdminCreated; }
    public void setIsAdminCreated(Boolean isAdminCreated) { this.isAdminCreated = isAdminCreated; }
}