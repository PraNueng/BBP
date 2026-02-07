package com.example.backend.dtos.hourform;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public class CreateHourFormRequestDto {

    @NotNull(message = "กรุณาเลือกคลาสที่สอน")
    private Long classId;

    @NotBlank(message = "กรุณาระบุประเภทคลาส")
    private String classType; // "hourly_group" หรือ "hourly_individual"

    private Long tutorId;

    @NotNull(message = "กรุณาระบุวิชา")
    private Long subjectId;

    @NotBlank(message = "กรุณากรอกเนื้อหาที่สอน")
    private String content;

    @NotNull(message = "กรุณาเลือกวันที่สอน")
    private LocalDate teachingDate;

    @NotNull(message = "กรุณากรอกจำนวนชั่วโมงที่สอน")
    @DecimalMin(value = "0.5", message = "จำนวนชั่วโมงต้องมากกว่า 0")
    private BigDecimal hoursTaught;

    @Min(value = 0, message = "จำนวนน้องที่มาต้องไม่ติดลบ")
    private Integer studentsPresent;

    @Min(value = 0, message = "จำนวนน้องที่ขาดต้องไม่ติดลบ")
    private Integer studentsAbsent;

    private String remark;

    // Constructors
    public CreateHourFormRequestDto() {}

    // Getters and Setters
    public Long getClassId() {
        return classId;
    }

    public void setClassId(Long classId) {
        this.classId = classId;
    }

    public String getClassType() {
        return classType;
    }

    public void setClassType(String classType) {
        this.classType = classType;
    }

    public Long getTutorId() {
        return tutorId;
    }

    public void setTutorId(Long tutorId) {
        this.tutorId = tutorId;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
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
}