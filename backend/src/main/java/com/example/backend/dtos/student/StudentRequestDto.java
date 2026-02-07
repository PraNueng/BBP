package com.example.backend.dtos.student;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;

public class StudentRequestDto {

    @NotBlank(message = "First name is required")
    @Size(max = 100, message = "First name must be less than 100 characters")
    private String firstName;

    @Size(max = 100, message = "Last name must be less than 100 characters")
    private String lastName;

    @NotBlank(message = "Nickname is required")
    @Size(max = 50, message = "Nickname must be less than 50 characters")
    private String nickname;

    @Size(max = 50, message = "Student code must be less than 50 characters")
    private String studentCode;

    @Size(max = 200, message = "School name must be less than 200 characters")
    private String schoolName;

    @NotNull(message = "Grade ID is required")
    private Long gradeId;

    @Pattern(regexp = "^$|^0\\d{8,9}$", message = "Invalid phone number format")
    private String phoneStudent;

    @Pattern(regexp = "^$|^0\\d{8,9}$", message = "Invalid phone number format")
    private String phoneParent;

    @Size(max = 500, message = "Study plan must be less than 500 characters")
    private String studyPlan;

    @Valid
    private List<CoursePurchaseDto> coursePurchases;

    private List<CoursePurchaseDto> updatedCoursePurchases;
    private List<Long> deletedCoursePurchaseIds;

    // คลาสเรียน (optional)
    @Valid
    private List<ClassEnrollmentDto> classes;

    /**
     * DTO ข้อมูลการซื้อคอร์ส (แบบง่าย - เก็บแค่วิชาและชั่วโมง)
     */
    public static class CoursePurchaseDto {
        private Long id;
        private Long monthlySubtypeId;
        private String deletedReason;
        private String editReason;

        @NotNull(message = "Subject ID is required")
        private Long subjectId;

        @Positive(message = "Hours must be greater than 0")
        private Double hoursPurchased;

        @Pattern(regexp = "^$|^GROUP$|^INDIVIDUAL$|^INDIVIDUAL_GROUP$|^MONTHLY$|^hourly_group$|^hourly_individual$|^hourly_individual_group$|^monthly$",
                message = "Class type must be GROUP, INDIVIDUAL, INDIVIDUAL_GROUP, MONTHLY, hourly_group, hourly_individual, hourly_individual_group, or monthly")
        private String classType;

        // Getters and Setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Long getMonthlySubtypeId() {
            return monthlySubtypeId;
        }

        public void setMonthlySubtypeId(Long monthlySubtypeId) {
            this.monthlySubtypeId = monthlySubtypeId;
        }

        public String getDeletedReason() {
            return deletedReason;
        }

        public void setDeletedReason(String deletedReason) {
            this.deletedReason = deletedReason;
        }

        public String getEditReason(){
            return editReason;
        }

        public void setEditReason(String editReason){
            this.editReason = editReason;
        }

        public Long getSubjectId() {
            return subjectId;
        }

        public void setSubjectId(Long subjectId) {
            this.subjectId = subjectId;
        }

        public Double getHoursPurchased() {
            return hoursPurchased;
        }

        public void setHoursPurchased(Double hoursPurchased) {
            this.hoursPurchased = hoursPurchased;
        }

        public String getClassType() {
            return classType;
        }

        public void setClassType(String classType) {
            this.classType = classType;
        }
    }

    /**
     * DTO ข้อมูลคลาสที่จะลงทะเบียน
     */
    public static class ClassEnrollmentDto {
        private Long id; // สำหรับการแก้ไข

        @NotNull(message = "Subject ID is required")
        private Long subjectId;

        @NotBlank(message = "Class type is required")
        @Pattern(regexp = "MONTH|HOUR", message = "Class type must be MONTH or HOUR")
        private String classType;

        @NotBlank(message = "Mode is required")
        @Pattern(regexp = "GROUP|PRIVATE|PRIVATE_GROUP", message = "Mode must be GROUP, PRIVATE, or PRIVATE_GROUP")
        private String mode;

        private Long tutorId;

        // สำหรับ MONTH + GROUP - ใช้ subtypeId แทน schedule
        private Long subtypeId;

        // (หรือจะเก็บทั้งสองก็ได้ เพื่อ backward compatibility)
        private String schedule;  // เก็บไว้เพื่อ backward compatibility

        // สำหรับ HOUR + GROUP - ใช้ subtypeId เหมือนกัน
        private String groupType;

        // สำหรับ HOUR
        private Integer hours;

        // Getters and Setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Long getSubjectId() {
            return subjectId;
        }

        public void setSubjectId(Long subjectId) {
            this.subjectId = subjectId;
        }

        public String getClassType() {
            return classType;
        }

        public void setClassType(String classType) {
            this.classType = classType;
        }

        public String getMode() {
            return mode;
        }

        public void setMode(String mode) {
            this.mode = mode;
        }

        public Long getTutorId() {
            return tutorId;
        }

        public void setTutorId(Long tutorId) {
            this.tutorId = tutorId;
        }

        public Long getSubtypeId() {
            return subtypeId;
        }

        public void setSubtypeId(Long subtypeId) {
            this.subtypeId = subtypeId;
        }

        public String getSchedule() {
            return schedule;
        }

        public void setSchedule(String schedule) {
            this.schedule = schedule;
        }

        public String getGroupType() {
            return groupType;
        }

        public void setGroupType(String groupType) {
            this.groupType = groupType;
        }

        public Integer getHours() {
            return hours;
        }

        public void setHours(Integer hours) {
            this.hours = hours;
        }
    }

    // Getters and Setters for main class
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

    public String getStudentCode() {
        return studentCode;
    }

    public void setStudentCode(String studentCode) {
        this.studentCode = studentCode;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public Long getGradeId() {
        return gradeId;
    }

    public void setGradeId(Long gradeId) {
        this.gradeId = gradeId;
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

    public List<CoursePurchaseDto> getCoursePurchases() {
        return coursePurchases;
    }

    public void setCoursePurchases(List<CoursePurchaseDto> coursePurchases) {
        this.coursePurchases = coursePurchases;
    }

    public List<CoursePurchaseDto> getUpdatedCoursePurchases() {
        return updatedCoursePurchases;
    }

    public void setUpdatedCoursePurchases(List<CoursePurchaseDto> updatedCoursePurchases) {
        this.updatedCoursePurchases = updatedCoursePurchases;
    }

    public List<Long> getDeletedCoursePurchaseIds() {
        return deletedCoursePurchaseIds;
    }

    public void setDeletedCoursePurchaseIds(List<Long> deletedCoursePurchaseIds) {
        this.deletedCoursePurchaseIds = deletedCoursePurchaseIds;
    }

    public List<ClassEnrollmentDto> getClasses() {
        return classes;
    }

    public void setClasses(List<ClassEnrollmentDto> classes) {
        this.classes = classes;
    }
}