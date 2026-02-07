package com.example.backend.dtos;

import com.example.backend.entities.ScienceForm;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ScienceFormDto {
    private Long id;
    private Long tutorId;
    private String tutorName;
    private Long classId;
    private String className;
    private String grade;
    private Long subjectId;
    private String subjectName;
    private String content;
    private LocalDate teachingDate;
    private BigDecimal hoursTaught;
    private Integer studentsPresent;
    private Integer studentsAbsent;
    private String remark;
    private Boolean isAdminCreated;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ScienceFormDto fromEntity(ScienceForm entity) {
        ScienceFormDto dto = new ScienceFormDto();
        dto.setId(entity.getId());
        dto.setClassId(entity.getMonthlyClass().getId());
        dto.setContent(entity.getContent());
        dto.setTeachingDate(entity.getTeachingDate());
        dto.setHoursTaught(entity.getHoursTaught());
        dto.setStudentsPresent(entity.getStudentsPresent());
        dto.setStudentsAbsent(entity.getStudentsAbsent());
        dto.setRemark(entity.getRemark());
        dto.setIsAdminCreated(entity.getIsAdminCreated());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        if (entity.getTutor() != null) {
            dto.setTutorId(entity.getTutor().getId());
            dto.setTutorName(entity.getTutor().getNickname());
        }

        if (entity.getSubject() != null) {
            dto.setSubjectId(entity.getSubject().getId());
            dto.setSubjectName(entity.getSubject().getSubjectName());
        }

        return dto;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getTutorId() { return tutorId; }
    public void setTutorId(Long tutorId) { this.tutorId = tutorId; }

    public String getTutorName() { return tutorName; }
    public void setTutorName(String tutorName) { this.tutorName = tutorName; }

    public Long getClassId() { return classId; }
    public void setClassId(Long classId) { this.classId = classId; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }

    public Long getSubjectId() { return subjectId; }
    public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }

    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }

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
}