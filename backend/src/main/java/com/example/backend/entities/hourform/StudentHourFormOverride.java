package com.example.backend.entities.hourform;

import com.example.backend.entities.student.Student;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "student_hour_form_overrides")
public class StudentHourFormOverride {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "hour_form_id", nullable = false)
    private HourForm hourForm;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(name = "hours_override", precision = 5, scale = 2)
    private BigDecimal hoursOverride; // ชั่วโมงที่ถูก override (null = ใช้ค่าเดิมจาก HourForm)

    @Column(name = "remark_override", length = 500)
    private String remarkOverride;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public HourForm getHourForm() {
        return hourForm;
    }

    public void setHourForm(HourForm hourForm) {
        this.hourForm = hourForm;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public BigDecimal getHoursOverride() {
        return hoursOverride;
    }

    public void setHoursOverride(BigDecimal hoursOverride) {
        this.hoursOverride = hoursOverride;
    }

    public String getRemarkOverride() {
        return remarkOverride;
    }

    public void setRemarkOverride(String remarkOverride) {
        this.remarkOverride = remarkOverride;
    }
}