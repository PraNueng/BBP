package com.example.backend.dtos.enrollment;

import java.util.List;

/**
 * DTO สำหรับแสดงคลาสทั้งหมดที่นักเรียนลงทะเบียน
 */
public class StudentEnrolledClassesDto {
    private Long studentId;
    private String studentName;
    private String nickname;
    private List<EnrolledClassInfo> hourlyGroupClasses;
    private List<EnrolledClassInfo> monthlyClasses;
    private List<EnrolledClassInfo> hourlyIndividualClasses;

    public StudentEnrolledClassesDto() {}

    public static class EnrolledClassInfo {
        private Long enrollmentId;
        private Long classId;
        private String className;
        private String tutorName;
        private String subjectName;
        private String subtypeName; // สำหรับ group/monthly
        private Double hoursTarget; // สำหรับ hourly
        private Double hoursCompleted; // สำหรับ hourly
        private Boolean isActive;

        public EnrolledClassInfo() {}

        public EnrolledClassInfo(Long enrollmentId, Long classId, String className,
                                 String tutorName, String subjectName, String subtypeName,
                                 Double hoursTarget, Double hoursCompleted, Boolean isActive) {
            this.enrollmentId = enrollmentId;
            this.classId = classId;
            this.className = className;
            this.tutorName = tutorName;
            this.subjectName = subjectName;
            this.subtypeName = subtypeName;
            this.hoursTarget = hoursTarget;
            this.hoursCompleted = hoursCompleted;
            this.isActive = isActive;
        }

        // Getters and Setters
        public Long getEnrollmentId() {
            return enrollmentId;
        }

        public void setEnrollmentId(Long enrollmentId) {
            this.enrollmentId = enrollmentId;
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

        public String getTutorName() {
            return tutorName;
        }

        public void setTutorName(String tutorName) {
            this.tutorName = tutorName;
        }

        public String getSubjectName() {
            return subjectName;
        }

        public void setSubjectName(String subjectName) {
            this.subjectName = subjectName;
        }

        public String getSubtypeName() {
            return subtypeName;
        }

        public void setSubtypeName(String subtypeName) {
            this.subtypeName = subtypeName;
        }

        public Double getHoursTarget() {
            return hoursTarget;
        }

        public void setHoursTarget(Double hoursTarget) {
            this.hoursTarget = hoursTarget;
        }

        public Double getHoursCompleted() {
            return hoursCompleted;
        }

        public void setHoursCompleted(Double hoursCompleted) {
            this.hoursCompleted = hoursCompleted;
        }

        public Boolean getIsActive() {
            return isActive;
        }

        public void setIsActive(Boolean isActive) {
            this.isActive = isActive;
        }
    }

    // Getters and Setters for main class
    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public List<EnrolledClassInfo> getHourlyGroupClasses() {
        return hourlyGroupClasses;
    }

    public void setHourlyGroupClasses(List<EnrolledClassInfo> hourlyGroupClasses) {
        this.hourlyGroupClasses = hourlyGroupClasses;
    }

    public List<EnrolledClassInfo> getMonthlyClasses() {
        return monthlyClasses;
    }

    public void setMonthlyClasses(List<EnrolledClassInfo> monthlyClasses) {
        this.monthlyClasses = monthlyClasses;
    }

    public List<EnrolledClassInfo> getHourlyIndividualClasses() {
        return hourlyIndividualClasses;
    }

    public void setHourlyIndividualClasses(List<EnrolledClassInfo> hourlyIndividualClasses) {
        this.hourlyIndividualClasses = hourlyIndividualClasses;
    }
}