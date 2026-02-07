package com.example.backend.dtos.hourform;

import java.math.BigDecimal;
import java.time.LocalDate;

public class UpdateHourFormRequestDto {

    private String content;
    private LocalDate teachingDate;
    private BigDecimal hoursTaught;
    private Integer studentsPresent;
    private Integer studentsAbsent;
    private String remark;

    // Constructors
    public UpdateHourFormRequestDto() {}

    // Getters and Setters
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