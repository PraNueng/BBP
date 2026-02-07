package com.example.backend.dtos.hourly;

import com.example.backend.entities.hourly.HourlyIndividualClass;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HourlyIndividualClassDto {
    private Long id;
    private Long studentId;
    private String studentName;
    private String studentNickname;
    private String studentFirstName;
    private String studentLastName;
    private String studentCode;
    private Long tutorId;
    private String tutorName;
    private List<TutorInfo> tutors;
    private Long subjectId;
    private String subjectName;
    private Long gradeId;
    private String gradeName;
    private String className;
    private Double hoursTarget;
    private Boolean isActive;
    private String note;
    private Long totalStudents;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long createdById;
    private String createdByName;

    public HourlyIndividualClassDto() {
    }

    public HourlyIndividualClassDto(
            Long id,
            Long studentId,
            String studentName,
            String studentNickname,
            String studentFirstName,
            String studentLastName,
            String studentCode,
            Long tutorId,
            String tutorName,
            Long subjectId,
            String subjectName,
            Long gradeId,
            String gradeName,
            String className,
            Double hoursTarget,
            Boolean isActive,
            String note,
            Long totalStudents,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            Long createdById,
            String createdByName
    ) {
        this.id = id;
        this.studentId = studentId;
        this.studentName = studentName;
        this.studentNickname = studentNickname;
        this.studentFirstName = studentFirstName;
        this.studentLastName = studentLastName;
        this.studentCode = studentCode;
        this.tutorId = tutorId;
        this.tutorName = tutorName;
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.gradeId = gradeId;
        this.gradeName = gradeName;
        this.className = className;
        this.hoursTarget = hoursTarget;
        this.isActive = isActive;
        this.note = note;
        this.totalStudents = totalStudents;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.createdById = createdById;
        this.createdByName = createdByName;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getStudentNickname() { return studentNickname; }
    public void setStudentNickname(String studentNickname) { this.studentNickname = studentNickname; }

    public String getStudentFirstName() { return studentFirstName; }
    public void setStudentFirstName(String studentFirstName) { this.studentFirstName = studentFirstName; }

    public String getStudentLastName() { return studentLastName; }
    public void setStudentLastName(String studentLastName) { this.studentLastName = studentLastName; }

    public String getStudentCode() { return studentCode; }
    public void setStudentCode(String studentCode) { this.studentCode = studentCode; }

    public Long getTutorId() { return tutorId; }
    public void setTutorId(Long tutorId) { this.tutorId = tutorId; }

    public String getTutorName() { return tutorName; }
    public void setTutorName(String tutorName) { this.tutorName = tutorName; }

    public List<TutorInfo> getTutors() {
        return tutors;
    }

    public void setTutors(List<TutorInfo> tutors) {
        this.tutors = tutors;
    }

    public Long getSubjectId() { return subjectId; }
    public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }

    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }

    public Long getGradeId() { return gradeId; }
    public void setGradeId(Long gradeId) { this.gradeId = gradeId; }

    public String getGradeName() { return gradeName; }
    public void setGradeName(String gradeName) { this.gradeName = gradeName; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    public Double getHoursTarget() { return hoursTarget; }
    public void setHoursTarget(Double hoursTarget) { this.hoursTarget = hoursTarget; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public Long getTotalStudents() { return totalStudents; }
    public void setTotalStudents(Long totalStudents) { this.totalStudents = totalStudents; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Long getCreatedById() { return createdById; }
    public void setCreatedById(Long createdById) { this.createdById = createdById; }

    public String getCreatedByName() { return createdByName; }
    public void setCreatedByName(String createdByName) { this.createdByName = createdByName; }

    public static HourlyIndividualClassDto fromEntity(HourlyIndividualClass entity) {
        if (entity == null) {
            return null;
        }

        HourlyIndividualClassDto dto = new HourlyIndividualClassDto();
        dto.setId(entity.getId());
        dto.setClassName(entity.getClassName());
        dto.setIsActive(entity.getIsActive());
        dto.setNote(entity.getNote());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        if (entity.getStudent() != null) {
            dto.setStudentId(entity.getStudent().getId());
            dto.setStudentNickname(entity.getStudent().getNickname());
            dto.setStudentFirstName(entity.getStudent().getFirstName());
            dto.setStudentLastName(entity.getStudent().getLastName());
            dto.setStudentCode(entity.getStudent().getStudentCode());
            dto.setStudentName(entity.getStudent().getFirstName() + " " + entity.getStudent().getLastName());
            // คลาสเดี่ยวมีนักเรียน 1 คน
            dto.setTotalStudents(1L);

            // ดึง Grade จากนักเรียนแทน (สำหรับคลาสเดี่ยว)
            if (entity.getStudent().getGrade() != null) {
                dto.setGradeId(entity.getStudent().getGrade().getId());
                dto.setGradeName(entity.getStudent().getGrade().getGradeName());
            }
        } else {
            dto.setTotalStudents(0L);
        }

        // Subject info
        if (entity.getSubject() != null) {
            dto.setSubjectId(entity.getSubject().getId());
            dto.setSubjectName(entity.getSubject().getSubjectName());
        }

        // Grade info
        if (entity.getGrade() != null) {
            dto.setGradeId(entity.getGrade().getId());
            dto.setGradeName(entity.getGrade().getGradeName());
        }

        // Created by info
        if (entity.getCreatedBy() != null) {
            dto.setCreatedById(entity.getCreatedBy().getId());
            dto.setCreatedByName(entity.getCreatedBy().getNickname());
        }

        return dto;
    }


    public static class TutorInfo {
        private Long id;
        private String nickname;

        public TutorInfo(Long id, String nickname) {
            this.id = id;
            this.nickname = nickname;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getNickname() { return nickname; }
        public void setNickname(String nickname) { this.nickname = nickname; }
    }
}