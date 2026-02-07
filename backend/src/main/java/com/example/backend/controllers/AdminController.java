package com.example.backend.controllers;

import com.example.backend.dtos.student.StudentDto;
import com.example.backend.dtos.student.StudentDetailDto;
import com.example.backend.dtos.hourform.HourFormDto;
import com.example.backend.services.student.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private StudentService studentService;

    // Grade string to ID mapping
    private static final Map<String, Long> GRADE_MAP = Map.of(
            "m1", 1L,
            "m2", 2L,
            "m3", 3L,
            "m4", 4L,
            "m5", 5L,
            "m6", 6L,
            "others", 7L
    );

    @GetMapping("/dashboard")
    public ResponseEntity<String> getDashboard() {
        return ResponseEntity.ok("This is admin dashboard");
    }

    /**
     * ดึงนักเรียนทั้งหมดตามชั่นเรียน (ใช้ grade name เช่น m1, m2)
     */
    @GetMapping("/students/by-grade/{gradeName}")
    public ResponseEntity<List<StudentDto>> getStudentsByGradeName(
            @PathVariable String gradeName) {

        Long gradeId = GRADE_MAP.get(gradeName.toLowerCase());
        if (gradeId == null) {
            throw new IllegalArgumentException("Invalid grade name: " + gradeName);
        }

        List<StudentDto> students = studentService.getStudentsByGrade(gradeId);
        return ResponseEntity.ok(students);
    }

    /**
     * ดึงข้อมูลนักเรียนพร้อมคลาสที่เรียนทั้งหมด (รองรับทั้ง gradeId และ gradeName)
     */
    @GetMapping("/students/{grade}/{studentId}")
    public ResponseEntity<StudentDetailDto> getStudentDetail(
            @PathVariable String grade,
            @PathVariable Long studentId) {

        Long gradeId = parseGradeId(grade);
        StudentDetailDto studentDetail = studentService.getStudentWithClasses(studentId, gradeId);
        return ResponseEntity.ok(studentDetail);
    }

    /**
     * ดึงสถิติชั่วโมงสอนของนักเรียนในคลาสรายชั่วโมง
     */
    @GetMapping("/students/{studentId}/hours-summary")
    public ResponseEntity<Map<String, Object>> getStudentHoursSummary(
            @PathVariable Long studentId) {

        Map<String, Object> summary = studentService.getStudentHoursSummary(studentId);
        return ResponseEntity.ok(summary);
    }

    // ==================== Helper Methods ====================

    /**
     * แปลง grade string เป็น gradeId
     * รองรับทั้ง: "1", "2", "3" (gradeId) และ "m1", "m2", "m3" (gradeName)
     */
    private Long parseGradeId(String grade) {
        try {
            // ลองแปลงเป็น Long ก่อน (ถ้าส่งมาเป็นตัวเลข)
            return Long.parseLong(grade);
        } catch (NumberFormatException e) {
            // ถ้าไม่ใช่ตัวเลข ให้หาจาก GRADE_MAP
            Long gradeId = GRADE_MAP.get(grade.toLowerCase());
            if (gradeId == null) {
                throw new IllegalArgumentException("Invalid grade: " + grade);
            }
            return gradeId;
        }
    }

    /**
     * ตรวจสอบว่า classType ถูกต้องหรือไม่
     */
    private boolean isValidClassType(String classType) {
        return "hourly_group".equals(classType) ||
                "hourly_individual".equals(classType) ||
                "monthly".equals(classType);
    }
}