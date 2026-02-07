package com.example.backend.dtos.student;

import java.time.LocalDate;
import java.util.List;

public class StudentDetailDto extends StudentDto {
    private List<MonthlyClassInfo> monthlyEnrollments;
    private List<HourlyGroupClassInfo> hourlyGroupEnrollments;
    private List<HourlyIndividualClassInfo> hourlyIndividualClasses;

    private List<CoursePurchaseInfo> coursePurchases;

    public List<CoursePurchaseInfo> getCoursePurchases() {
        return coursePurchases;
    }

    public void setCoursePurchases(List<CoursePurchaseInfo> coursePurchases) {
        this.coursePurchases = coursePurchases;
    }


    // Getters and Setters
    public List<MonthlyClassInfo> getMonthlyEnrollments() {
        return monthlyEnrollments;
    }

    public void setMonthlyEnrollments(List<MonthlyClassInfo> monthlyEnrollments) {
        this.monthlyEnrollments = monthlyEnrollments;
    }

    public List<HourlyGroupClassInfo> getHourlyGroupEnrollments() {
        return hourlyGroupEnrollments;
    }

    public void setHourlyGroupEnrollments(List<HourlyGroupClassInfo> hourlyGroupEnrollments) {
        this.hourlyGroupEnrollments = hourlyGroupEnrollments;
    }

    public List<HourlyIndividualClassInfo> getHourlyIndividualClasses() {
        return hourlyIndividualClasses;
    }

    public void setHourlyIndividualClasses(List<HourlyIndividualClassInfo> hourlyIndividualClasses) {
        this.hourlyIndividualClasses = hourlyIndividualClasses;
    }

    // Factory method
    public static StudentDetailDto fromEntity(com.example.backend.entities.student.Student student) {
        StudentDetailDto dto = new StudentDetailDto();
        dto.setId(student.getId());
        dto.setStudentCode(student.getStudentCode());
        dto.setFirstName(student.getFirstName());
        dto.setLastName(student.getLastName());
        dto.setNickname(student.getNickname());
        dto.setSchoolName(student.getSchoolName());
        dto.setGradeId(student.getGrade().getId());
        dto.setGradeName(student.getGrade().getGradeName());
        dto.setPhoneStudent(student.getPhoneStudent());
        dto.setPhoneParent(student.getPhoneParent());
        dto.setStudyPlan(student.getStudyPlan());
        dto.setIsActive(student.getIsActive());
        return dto;
    }

    // Inner class สำหรับ Monthly Class
    public static class MonthlyClassInfo {
        private Long classId;  // เก็บเป็น enrollment ID
        private Long enrollmentId;
        private String className;
        private String tutorNickname;
        private Long subjectId;
        private String subjectName;
        private String subtypeName;
        private LocalDate startDate;
        private LocalDate endDate;
        private Boolean isActive;

        public MonthlyClassInfo(Long enrollmentId, String className, String tutorNickname, Long subjectId,
                                String subjectName, String subtypeName, LocalDate startDate,
                                LocalDate endDate, Boolean isActive) {
            this.classId = enrollmentId;
            this.enrollmentId = enrollmentId;
            this.className = className;
            this.tutorNickname = tutorNickname;
            this.subjectId = subjectId;
            this.subjectName = subjectName;
            this.subtypeName = subtypeName;
            this.startDate = startDate;
            this.endDate = endDate;
            this.isActive = isActive;
        }

        public Long getId() { return classId; }

        public Long getClassId() { return classId; }
        public void setClassId(Long classId) { this.classId = classId; }

        public Long getEnrollmentId() { return enrollmentId; }
        public void setEnrollmentId(Long enrollmentId) { this.enrollmentId = enrollmentId; }

        public String getClassName() { return className; }
        public void setClassName(String className) { this.className = className; }

        public String getTutorName() { return tutorNickname; }
        public String getTutorNickname() { return tutorNickname; }
        public void setTutorNickname(String tutorNickname) { this.tutorNickname = tutorNickname; }

        public Long getSubjectId() { return subjectId; }
        public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }

        public String getSubjectName() { return subjectName; }
        public void setSubjectName(String subjectName) { this.subjectName = subjectName; }

        public String getSubtypeName() { return subtypeName; }
        public void setSubtypeName(String subtypeName) { this.subtypeName = subtypeName; }

        public LocalDate getStartDate() { return startDate; }
        public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

        public LocalDate getEndDate() { return endDate; }
        public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

        public Boolean getIsActive() { return isActive; }
        public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    }

    // Inner class สำหรับ Hourly Group Class
    public static class HourlyGroupClassInfo {
        private Long classId;  // เก็บเป็น enrollment ID
        private Long enrollmentId;
        private String className;
        private String tutorNickname;
        private List<String> tutorNicknames;
        private Long subjectId;
        private String subjectName;
        private String subtypeName;
        private Double hoursTarget;
        private Double hoursCompleted;
        private Double completionPercentage;
        private Integer milestonesReached;
        private Boolean isActive;

        public HourlyGroupClassInfo(
                Long enrollmentId,
                String className,
                String tutorNickname,
                List<String> tutorNicknames,
                Long subjectId,
                String subjectName,
                String subtypeName,
                Double hoursTarget,
                Double hoursCompleted,
                Double completionPercentage,
                Integer milestonesReached,
                Boolean isActive)
        {
            this.classId = enrollmentId;
            this.enrollmentId = enrollmentId;
            this.className = className;
            this.tutorNickname = tutorNickname;
            this.tutorNicknames = tutorNicknames;
            this.subjectId = subjectId;
            this.subjectName = subjectName;
            this.subtypeName = subtypeName;
            this.hoursTarget = hoursTarget;
            this.hoursCompleted = hoursCompleted;
            this.completionPercentage = completionPercentage;
            this.milestonesReached = milestonesReached;
            this.isActive = isActive;
        }

        public Long getId() { return classId; }

        public Long getClassId() { return classId; }
        public void setClassId(Long classId) { this.classId = classId; }

        public Long getEnrollmentId() { return enrollmentId; }
        public void setEnrollmentId(Long enrollmentId) { this.enrollmentId = enrollmentId; }

        public String getClassName() { return className; }
        public void setClassName(String className) { this.className = className; }

        public List<String> getTutorNicknames() { return tutorNicknames; }
        public void setTutorNicknames(List<String> tutorNicknames) {}

        public String getTutorName() { return tutorNickname; }
        public String getTutorNickname() { return tutorNickname; }
        public void setTutorNickname(String tutorNickname) { this.tutorNickname = tutorNickname; }

        public Long getSubjectId() { return subjectId; }
        public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }

        public String getSubjectName() { return subjectName; }
        public void setSubjectName(String subjectName) { this.subjectName = subjectName; }

        public String getSubtypeName() { return subtypeName; }
        public void setSubtypeName(String subtypeName) { this.subtypeName = subtypeName; }

        public Double getHoursTarget() { return hoursTarget; }
        public void setHoursTarget(Double hoursTarget) { this.hoursTarget = hoursTarget; }

        public Double getHoursCompleted() { return hoursCompleted; }
        public void setHoursCompleted(Double hoursCompleted) { this.hoursCompleted = hoursCompleted; }

        public Double getCompletionPercentage() { return completionPercentage; }
        public void setCompletionPercentage(Double completionPercentage) {
            this.completionPercentage = completionPercentage;
        }

        public Integer getMilestonesReached() { return milestonesReached; }
        public void setMilestonesReached(Integer milestonesReached) {
            this.milestonesReached = milestonesReached;
        }

        public Boolean getIsActive() { return isActive; }
        public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    }

    // Inner class สำหรับ Hourly Individual Class
    public static class HourlyIndividualClassInfo {
        private Long classId;
        private String className;
        private Long tutorId;
        private String tutorNickname;
        private Long subjectId;
        private String subjectName;
        private Double hoursTarget;
        private Double hoursCompleted;
        private Boolean isActive;
        private Long totalStudents;
        private Long enrollmentId;

        public HourlyIndividualClassInfo(Long classId, String className,
                                         Long subjectId, String subjectName, Double hoursTarget, Double hoursCompleted,
                                         Boolean isActive, Long totalStudents, Long enrollmentId) {
            this.classId = classId;
            this.className = className;
            this.subjectId = subjectId;
            this.subjectName = subjectName;
            this.hoursTarget = hoursTarget;
            this.hoursCompleted = hoursCompleted;
            this.isActive = isActive;
            this.totalStudents = totalStudents;
            this.enrollmentId = enrollmentId;
        }

        public Long getId() { return classId; }

        public Long getClassId() { return classId; }
        public void setClassId(Long classId) { this.classId = classId; }

        public String getClassName() { return className; }
        public void setClassName(String className) { this.className = className; }

        public String getTutorName() { return tutorNickname; }
        public String getTutorNickname() { return tutorNickname; }
        public void setTutorNickname(String tutorNickname) { this.tutorNickname = tutorNickname; }

        public Long getTutorId() { return tutorId; }
        public void setTutorId(Long tutorId) { this.tutorId = tutorId; }

        public Long getSubjectId() { return subjectId; }
        public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }

        public String getSubjectName() { return subjectName; }
        public void setSubjectName(String subjectName) { this.subjectName = subjectName; }

        public Double getHoursTarget() { return hoursTarget; }
        public void setHoursTarget(Double hoursTarget) { this.hoursTarget = hoursTarget; }

        public Double getHoursCompleted() { return hoursCompleted; }
        public void setHoursCompleted(Double hoursCompleted) { this.hoursCompleted = hoursCompleted; }

        public Boolean getIsActive() { return isActive; }
        public void setIsActive(Boolean isActive) { this.isActive = isActive; }

        public Long getTotalStudents() { return totalStudents; }
        public void setTotalStudents(Long totalStudents) { this.totalStudents = totalStudents; }

        public Long getEnrollmentId() { return enrollmentId; }
        public void setEnrollmentId(Long enrollmentId) {}
    }

    // ================================
    // Inner class สำหรับ Course Purchase
    // ================================
    public static class CoursePurchaseInfo {

        private Long purchaseId;
        private Long studentId;
        private Long subjectId;
        private String subjectName;
        private Double hoursPurchased;
        private Double hoursCompleted;
        private LocalDate purchaseDate;
        private Boolean isActive;
        private String classType;
        private Long monthlySubtypeId;
        private String monthlySubtypeName;
        private String deletedReason;
        private String editReason;

        public CoursePurchaseInfo(
                Long purchaseId,
                Long studentId,
                Long subjectId,
                String subjectName,
                Double hoursPurchased,
                Double hoursCompleted,
                LocalDate purchaseDate,
                Boolean isActive,
                String classType,
                Long monthlySubtypeId,
                String monthlySubtypeName,
                String deletedReason
        ) {
            this.purchaseId = purchaseId;
            this.studentId = studentId;
            this.subjectId = subjectId;
            this.subjectName = subjectName;
            this.hoursPurchased = hoursPurchased;
            this.hoursCompleted = hoursCompleted;
            this.purchaseDate = purchaseDate;
            this.isActive = isActive;
            this.classType = classType;
            this.monthlySubtypeId = monthlySubtypeId;
            this.monthlySubtypeName = monthlySubtypeName;
            this.deletedReason = deletedReason;
        }

        public Long getPurchaseId() {
            return purchaseId;
        }

        public Long getStudentId() {
            return studentId;
        }

        public Long getSubjectId() {
            return subjectId;
        }

        public String getSubjectName() {
            return subjectName;
        }

        public Double getHoursPurchased() {
            return hoursPurchased;
        }

        public Double getHoursCompleted() {
            return hoursCompleted;
        }

        public Double getHoursRemaining() {
            // ถ้าเป็นคอร์สรายเดือน (hoursPurchased = null) ให้ return null
            if (hoursPurchased == null) {
                return null;
            }
            return hoursPurchased - hoursCompleted;
        }

        public LocalDate getPurchaseDate() {
            return purchaseDate;
        }

        public Boolean getIsActive() {
            return isActive;
        }

        public String getClassType() {
            return classType;
        }

        public Long getMonthlySubtypeId() {
            return monthlySubtypeId;
        }

        public String getMonthlySubtypeName() {
            return monthlySubtypeName;
        }

        public String getDeletedReason() {
            return deletedReason;
        }

        public String getEditReason() {
            return editReason;
        }

        public void setEditReason(String editReason) {
            this.editReason = editReason;
        }
    }
}