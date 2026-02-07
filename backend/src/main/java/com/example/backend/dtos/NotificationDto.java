package com.example.backend.dtos;

import com.example.backend.entities.Notification;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class NotificationDto {
    private Long id;
    private String classType; // 'hourly_group', 'hourly_individual'
    private Long classId;
    private String className;
    private Long studentId;
    private String studentCode;
    private String studentName;
    private String studentNickname;
    private String gradeName;
    private String notificationType; // 'HOURS_MILESTONE'
    private String title;
    private String message;
    private BigDecimal hoursCompleted;
    private BigDecimal hoursTarget;
    private Integer milestoneReached;
    private Boolean isRead;
    private Long readById;
    private String readByName;
    private LocalDateTime readAt;
    private LocalDateTime createdAt;
    private Long hoursAgo;
    private String timeAgo;
    private BigDecimal percentage;
    private Long subjectId;
    private String subjectName;
    private String receiptCode;
    private String schoolName;
    private Boolean isIndividual;

    public NotificationDto() {}

    // Static factory method
    public static NotificationDto fromEntity(Notification entity) {
        NotificationDto dto = new NotificationDto();
        dto.setId(entity.getId());
        dto.setClassType(entity.getClassType());
        dto.setClassId(entity.getClassId());
        dto.setNotificationType(entity.getNotificationType());
        dto.setTitle(entity.getTitle());
        dto.setMessage(entity.getMessage());
        dto.setMilestoneReached(entity.getMilestoneReached());
        dto.setIsRead(entity.getIsRead());
        dto.setReadAt(entity.getReadAt());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setReceiptCode(entity.getReceiptCode());

        // Student info
        if (entity.getStudent() != null) {
            dto.setStudentId(entity.getStudent().getId());
            dto.setStudentCode(entity.getStudent().getStudentCode());
            dto.setStudentNickname(entity.getStudent().getNickname());

            String fullName = entity.getStudent().getFirstName();
            if (entity.getStudent().getLastName() != null && !entity.getStudent().getLastName().isEmpty()) {
                fullName += " " + entity.getStudent().getLastName();
            }
            dto.setStudentName(fullName);

            if (entity.getStudent().getGrade() != null) {
                dto.setGradeName(entity.getStudent().getGrade().getGradeName());
            }

            if (entity.getStudent().getSchoolName() != null) {
                dto.setSchoolName(entity.getStudent().getSchoolName());
            }
        }

        // Read by info
        if (entity.getReadBy() != null) {
            dto.setReadById(entity.getReadBy().getId());
            dto.setReadByName(entity.getReadBy().getNickname());
        }

        // Calculate time ago
        if (entity.getCreatedAt() != null) {
            long hours = ChronoUnit.HOURS.between(entity.getCreatedAt(), LocalDateTime.now());
            dto.setHoursAgo(hours);
            dto.setTimeAgo(formatTimeAgo(hours));
        }

        if (entity.getSubject() != null) {
            dto.setSubjectId(entity.getSubject().getId());
            dto.setSubjectName(entity.getSubject().getSubjectName());
        }

        return dto;
    }

    private static String formatTimeAgo(long hours) {
        if (hours < 1) {
            return "เมื่อสักครู่";
        } else if (hours < 24) {
            return hours + " ชั่วโมงที่แล้ว";
        } else {
            long days = hours / 24;
            if (days == 1) {
                return "เมื่อวาน";
            } else if (days < 7) {
                return days + " วันที่แล้ว";
            } else if (days < 30) {
                long weeks = days / 7;
                return weeks + " สัปดาห์ที่แล้ว";
            } else {
                long months = days / 30;
                return months + " เดือนที่แล้ว";
            }
        }
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getStudentCode() {
        return studentCode;
    }

    public void setStudentCode(String studentCode) {
        this.studentCode = studentCode;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentNickname() {
        return studentNickname;
    }

    public void setStudentNickname(String studentNickname) {
        this.studentNickname = studentNickname;
    }

    public String getGradeName() {
        return gradeName;
    }

    public void setGradeName(String gradeName) {
        this.gradeName = gradeName;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public BigDecimal getHoursCompleted() {
        return hoursCompleted;
    }

    public void setHoursCompleted(BigDecimal hoursCompleted) {
        this.hoursCompleted = hoursCompleted;
    }

    public BigDecimal getHoursTarget() {
        return hoursTarget;
    }

    public void setHoursTarget(BigDecimal hoursTarget) {
        this.hoursTarget = hoursTarget;
    }

    public Integer getMilestoneReached() {
        return milestoneReached;
    }

    public void setMilestoneReached(Integer milestoneReached) {
        this.milestoneReached = milestoneReached;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    public Long getReadById() {
        return readById;
    }

    public void setReadById(Long readById) {
        this.readById = readById;
    }

    public String getReadByName() {
        return readByName;
    }

    public void setReadByName(String readByName) {
        this.readByName = readByName;
    }

    public LocalDateTime getReadAt() {
        return readAt;
    }

    public void setReadAt(LocalDateTime readAt) {
        this.readAt = readAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Long getHoursAgo() {
        return hoursAgo;
    }

    public void setHoursAgo(Long hoursAgo) {
        this.hoursAgo = hoursAgo;
    }

    public String getTimeAgo() {
        return timeAgo;
    }

    public void setTimeAgo(String timeAgo) {
        this.timeAgo = timeAgo;
    }

    public BigDecimal getPercentage() {
        return percentage;
    }

    public void setPercentage(BigDecimal percentage) {
        this.percentage = percentage;
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

    public String getReceiptCode() {
        return receiptCode;
    }

    public void setReceiptCode(String receiptCode) {
        this.receiptCode = receiptCode;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public Boolean getIsIndividual() {
        return isIndividual;
    }

    public void setIsIndividual(Boolean isIndividual) {
        this.isIndividual = isIndividual;
    }
}