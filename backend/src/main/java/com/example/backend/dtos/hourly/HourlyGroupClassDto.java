package com.example.backend.dtos.hourly;

import com.example.backend.dtos.tutor.TutorDto;
import com.example.backend.entities.hourly.HourlyGroupClass;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HourlyGroupClassDto {
    private Long id;
    private List<TutorDto> tutors;
    private Integer totalTutors;
    private Long subjectId;
    private String subjectName;
    private Long gradeId;
    private String gradeName;
    private Long subtypeId;
    private String subtypeName;
    private String className;
    private String classDisplayName;
    private BigDecimal hoursTarget;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long createdById;
    private String createdByName;
    private Integer totalStudents;
    private List<HourlyGroupEnrollmentDto> enrollments;

    public HourlyGroupClassDto() {}

    // Static factory method - updated
    public static HourlyGroupClassDto fromEntity(HourlyGroupClass entity) {
        HourlyGroupClassDto dto = new HourlyGroupClassDto();
        dto.setId(entity.getId());
        dto.setGradeId(entity.getGrade() != null ? entity.getGrade().getId() : null);
        dto.setGradeName(entity.getGrade() != null ? entity.getGrade().getGradeName() : null);
        dto.setClassName(entity.getClassName());
        dto.setIsActive(entity.getIsActive());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        // Subject
        if (entity.getSubject() != null) {
            dto.setSubjectId(entity.getSubject().getId());
            dto.setSubjectName(entity.getSubject().getSubjectName());
        }

        // Subtype
        if (entity.getSubtype() != null) {
            dto.setSubtypeId(entity.getSubtype().getId());
            dto.setSubtypeName(entity.getSubtype().getSubtypeName());
        }

        // Created by
        if (entity.getCreatedBy() != null) {
            dto.setCreatedById(entity.getCreatedBy().getId());
            dto.setCreatedByName(entity.getCreatedBy().getNickname());
        }

        // Tutors - แปลงจาก classTutors เป็น tutors list
        if (entity.getClassTutors() != null && !entity.getClassTutors().isEmpty()) {
            List<TutorDto> tutorDtos = entity.getClassTutors().stream()
                    .map(ct -> TutorDto.fromUserWithAssignedAt(
                            ct.getTutor(),
                            ct.getAssignedAt()
                    ))
                    .collect(Collectors.toList());
            dto.setTutors(tutorDtos);
            dto.setTotalTutors(tutorDtos.size());
        } else {
            dto.setTutors(new ArrayList<>());
            dto.setTotalTutors(0);
        }

        // Enrollments count
        if (entity.getEnrollments() != null) {
            dto.setTotalStudents(entity.getEnrollments().size());
        }

        return dto;
    }

    // Static factory method with enrollments
    public static HourlyGroupClassDto fromEntityWithEnrollments(HourlyGroupClass entity) {
        HourlyGroupClassDto dto = fromEntity(entity);

        if (entity.getEnrollments() != null) {
            dto.setEnrollments(
                    entity.getEnrollments().stream()
                            .map(HourlyGroupEnrollmentDto::fromEntity)
                            .collect(Collectors.toList())
            );
        }

        return dto;
    }

    // Helper methods
    public Long getFirstTutorId() {
        if (tutors != null && !tutors.isEmpty()) {
            return tutors.get(0).getId();
        }
        return null;
    }

    public String getFirstTutorName() {
        if (tutors != null && !tutors.isEmpty()) {
            return tutors.get(0).getNickname();
        }
        return null;
    }

    public String getTutorNamesString() {
        if (tutors == null || tutors.isEmpty()) {
            return "ยังไม่มีครู";
        }
        return tutors.stream()
                .map(TutorDto::getNickname)
                .collect(Collectors.joining(", "));
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<TutorDto> getTutors() {
        return tutors;
    }

    public void setTutors(List<TutorDto> tutors) {
        this.tutors = tutors;
    }

    public Integer getTotalTutors() {
        return totalTutors;
    }

    public void setTotalTutors(Integer totalTutors) {
        this.totalTutors = totalTutors;
    }

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

    public Long getGradeId() {
        return gradeId;
    }

    public void setGradeId(Long gradeId) {
        this.gradeId = gradeId;
    }

    public String getGradeName() {
        return gradeName;
    }

    public void setGradeName(String gradeName) {
        this.gradeName = gradeName;
    }

    public Long getSubtypeId() {
        return subtypeId;
    }

    public void setSubtypeId(Long subtypeId) {
        this.subtypeId = subtypeId;
    }

    public String getSubtypeName() {
        return subtypeName;
    }

    public void setSubtypeName(String subtypeName) {
        this.subtypeName = subtypeName;
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

    public BigDecimal getHoursTarget() {
        return hoursTarget;
    }

    public void setHoursTarget(BigDecimal hoursTarget) {
        this.hoursTarget = hoursTarget;
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

    public Long getCreatedById() {
        return createdById;
    }

    public void setCreatedById(Long createdById) {
        this.createdById = createdById;
    }

    public String getCreatedByName() {
        return createdByName;
    }

    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    public Integer getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(Integer totalStudents) {
        this.totalStudents = totalStudents;
    }

    public List<HourlyGroupEnrollmentDto> getEnrollments() {
        return enrollments;
    }

    public void setEnrollments(List<HourlyGroupEnrollmentDto> enrollments) {
        this.enrollments = enrollments;
    }
}