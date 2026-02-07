package com.example.backend.dtos.tutor;

import com.example.backend.entities.User;

import java.time.LocalDateTime;
import java.util.List;

public class TutorDto {

    private Long id;
    private String username;
    private String nickname;
    private LocalDateTime assignedAt;

    // 🔥 Fields ที่ convertToDto() ใช้ แต่ไม่ได้มีใน DTO เดิม
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Integer totalMonthlyClasses;
    private Integer totalHourlyGroupClasses;
    private Integer totalHourlyIndividualClasses;
    private Integer totalActiveClasses;

    private List<SubjectDTO> subjects;

    public TutorDto() {}

    public TutorDto(Long id, String username, String nickname, LocalDateTime assignedAt) {
        this.id = id;
        this.username = username;
        this.nickname = nickname;
        this.assignedAt = assignedAt;
    }

    // Static factory
    public static TutorDto fromUser(User user) {
        return new TutorDto(
                user.getId(),
                user.getUsername(),
                user.getNickname(),
                null
        );
    }

    public static TutorDto fromUserWithAssignedAt(User user, LocalDateTime assignedAt) {
        return new TutorDto(
                user.getId(),
                user.getUsername(),
                user.getNickname(),
                assignedAt
        );
    }

    // --- Getters & Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public LocalDateTime getAssignedAt() { return assignedAt; }
    public void setAssignedAt(LocalDateTime assignedAt) { this.assignedAt = assignedAt; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean active) { isActive = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Integer getTotalMonthlyClasses() { return totalMonthlyClasses; }
    public void setTotalMonthlyClasses(Integer totalMonthlyClasses) { this.totalMonthlyClasses = totalMonthlyClasses; }

    public Integer getTotalHourlyGroupClasses() { return totalHourlyGroupClasses; }
    public void setTotalHourlyGroupClasses(Integer totalHourlyGroupClasses) { this.totalHourlyGroupClasses = totalHourlyGroupClasses; }

    public Integer getTotalHourlyIndividualClasses() { return totalHourlyIndividualClasses; }
    public void setTotalHourlyIndividualClasses(Integer totalHourlyIndividualClasses) { this.totalHourlyIndividualClasses = totalHourlyIndividualClasses; }

    public Integer getTotalActiveClasses() { return totalActiveClasses; }
    public void setTotalActiveClasses(Integer totalActiveClasses) { this.totalActiveClasses = totalActiveClasses; }

    public List<SubjectDTO> getSubjects() { return subjects; }
    public void setSubjects(List<SubjectDTO> subjects) { this.subjects = subjects; }

    // ------ Nested SubjectDTO ------
    public static class SubjectDTO {
        private Long id;
        private String name;
        private Integer classCount;

        public SubjectDTO(Long id, String name, Integer classCount) {
            this.id = id;
            this.name = name;
            this.classCount = classCount;
        }

        public Long getId() { return id; }
        public String getName() { return name; }
        public Integer getClassCount() { return classCount; }
        public void setClassCount(Integer classCount) { this.classCount = classCount; }
    }
}
