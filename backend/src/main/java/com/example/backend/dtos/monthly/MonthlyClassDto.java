package com.example.backend.dtos.monthly;

import com.example.backend.dtos.tutor.TutorDto;
import com.example.backend.entities.monthly.MonthlyClass;
import com.example.backend.entities.monthly.MonthlyClassTutor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MonthlyClassDto {
    private Long id;
    private List<TutorDto> tutors;
    private Long subjectId;
    private String subjectName;
    private Long gradeId;
    private String gradeName;
    private Long subtypeId;
    private String subtypeName;
    private String className;
    private String classDisplayName;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long durationDays;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long createdById;
    private String createdByName;
    private Integer totalStudents;
    private Integer totalTutors;
    private List<MonthlyEnrollmentDto> enrollments;

    public MonthlyClassDto() {}

    // Static factory method
    public static MonthlyClassDto fromEntity(MonthlyClass entity) {
        MonthlyClassDto dto = new MonthlyClassDto();
        dto.setId(entity.getId());

        if (entity.getGrade() != null) {
            dto.setGradeId(entity.getGrade().getId());
            dto.setGradeName(entity.getGrade().getGradeName());
        }

        dto.setClassName(entity.getClassName());
        dto.setStartDate(entity.getStartDate());
        dto.setEndDate(entity.getEndDate());
        dto.setIsActive(entity.getIsActive());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        // Calculate duration
        if (entity.getStartDate() != null && entity.getEndDate() != null) {
            dto.setDurationDays(ChronoUnit.DAYS.between(entity.getStartDate(), entity.getEndDate()) + 1);
        }

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

        // Enrollments count
        if (entity.getEnrollments() != null) {
            dto.setTotalStudents(entity.getEnrollments().size());
        }

        return dto;
    }

    // Static factory method with enrollments
    public static MonthlyClassDto fromEntityWithEnrollments(MonthlyClass entity) {
        MonthlyClassDto dto = fromEntity(entity);

        if (entity.getEnrollments() != null) {
            dto.setEnrollments(
                    entity.getEnrollments().stream()
                            .map(MonthlyEnrollmentDto::fromEntity)
                            .collect(Collectors.toList())
            );
        }

        return dto;
    }

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
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public List<TutorDto> getTutors() { return tutors; }
    public void setTutors(List<TutorDto> tutors) { this.tutors = tutors; }

    public Integer getTotalTutors() { return totalTutors; }
    public void setTotalTutors(Integer totalTutors) { this.totalTutors = totalTutors; }

    public Long getSubjectId() { return subjectId; }
    public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }

    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }

    public Long getGradeId() { return gradeId; }
    public void setGradeId(Long gradeId) { this.gradeId = gradeId; }

    public String getGradeName() { return gradeName; }
    public void setGradeName(String gradeName) { this.gradeName = gradeName; }

    public Long getSubtypeId() { return subtypeId; }
    public void setSubtypeId(Long subtypeId) { this.subtypeId = subtypeId; }

    public String getSubtypeName() { return subtypeName; }
    public void setSubtypeName(String subtypeName) { this.subtypeName = subtypeName; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    public String getClassDisplayName() { return classDisplayName; }
    public void setClassDisplayName(String classDisplayName) {
        this.classDisplayName = classDisplayName;
    }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public Long getDurationDays() { return durationDays; }
    public void setDurationDays(Long durationDays) { this.durationDays = durationDays; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Long getCreatedById() { return createdById; }
    public void setCreatedById(Long createdById) { this.createdById = createdById; }

    public String getCreatedByName() { return createdByName; }
    public void setCreatedByName(String createdByName) { this.createdByName = createdByName; }

    public Integer getTotalStudents() { return totalStudents; }
    public void setTotalStudents(Integer totalStudents) { this.totalStudents = totalStudents; }

    public List<MonthlyEnrollmentDto> getEnrollments() { return enrollments; }
    public void setEnrollments(List<MonthlyEnrollmentDto> enrollments) {
        this.enrollments = enrollments;
    }
}