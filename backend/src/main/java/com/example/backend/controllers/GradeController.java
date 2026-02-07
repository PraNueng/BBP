package com.example.backend.controllers;

import com.example.backend.dtos.GradeDto;
import com.example.backend.services.GradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/grades")
public class GradeController {

    @Autowired
    private GradeService gradeService;

    /**
     * ดึงทุก Grade (ใช้สำหรับ dropdown)
     */
    @GetMapping
    public ResponseEntity<List<GradeDto>> getAllGrades() {
        List<GradeDto> grades = gradeService.getAllGrades();
        return ResponseEntity.ok(grades);
    }

    /**
     * ดึงเฉพาะ Grade ที่มีนักเรียน active
     * ใช้สำหรับหน้า Learning History Grade Selection
     */
    @GetMapping("/with-students")
    public ResponseEntity<List<GradeDto>> getGradesWithActiveStudents() {
        List<GradeDto> grades = gradeService.getGradesWithActiveStudents();
        return ResponseEntity.ok(grades);
    }

    /**
     * ดึง Grade ตาม ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<GradeDto> getGradeById(@PathVariable Long id) {
        GradeDto grade = gradeService.getGradeById(id);
        return ResponseEntity.ok(grade);
    }

    /**
     * ดึง Grade ตามชื่อ
     */
    @GetMapping("/name/{gradeName}")
    public ResponseEntity<GradeDto> getGradeByName(@PathVariable String gradeName) {
        GradeDto grade = gradeService.getGradeByName(gradeName);
        return ResponseEntity.ok(grade);
    }

    /**
     * นับจำนวนนักเรียนใน Grade
     */
    @GetMapping("/{id}/student-count")
    public ResponseEntity<Long> getStudentCount(@PathVariable Long id) {
        Long count = gradeService.countStudentsByGrade(id);
        return ResponseEntity.ok(count);
    }
}