package com.example.backend.controllers;

import com.example.backend.dtos.SubjectDto;
import com.example.backend.services.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subjects")
public class SubjectController {

    @Autowired
    private SubjectService subjectService;

    /**
     * ดึงรายการวิชาทั้งหมด (เฉพาะที่ active)
     */
    @GetMapping
    public ResponseEntity<List<SubjectDto>> getAllActiveSubjects() {
        List<SubjectDto> subjects = subjectService.getAllActiveSubjects();
        return ResponseEntity.ok(subjects);
    }

    /**
     * ดึงรายการวิชาทั้งหมด (รวมที่ inactive) - สำหรับ Admin
     */
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SubjectDto>> getAllSubjects() {
        List<SubjectDto> subjects = subjectService.getAllSubjects();
        return ResponseEntity.ok(subjects);
    }

    /**
     * ดึงข้อมูลวิชาตาม ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<SubjectDto> getSubjectById(@PathVariable Long id) {
        SubjectDto subject = subjectService.getSubjectById(id);
        return ResponseEntity.ok(subject);
    }

    /**
     * เพิ่มวิชาใหม่
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SubjectDto> createSubject(@RequestBody SubjectDto dto) {
        SubjectDto created = subjectService.createSubject(dto);
        return ResponseEntity.ok(created);
    }

    /**
     * แก้ไขข้อมูลวิชา
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SubjectDto> updateSubject(
            @PathVariable Long id,
            @RequestBody SubjectDto dto
    ) {
        SubjectDto updated = subjectService.updateSubject(id, dto);
        return ResponseEntity.ok(updated);
    }

    /**
     * เปิด/ปิดการใช้งานวิชา
     */
    @PatchMapping("/{id}/toggle-active")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SubjectDto> toggleActive(@PathVariable Long id) {
        SubjectDto updated = subjectService.toggleActive(id);
        return ResponseEntity.ok(updated);
    }

    /**
     * ลบวิชา (soft delete - เปลี่ยนเป็น inactive)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteSubject(@PathVariable Long id) {
        subjectService.deleteSubject(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * อัพเดทลำดับการแสดงผล
     */
    @PatchMapping("/{id}/display-order")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SubjectDto> updateDisplayOrder(
            @PathVariable Long id,
            @RequestParam Integer displayOrder
    ) {
        SubjectDto updated = subjectService.updateDisplayOrder(id, displayOrder);
        return ResponseEntity.ok(updated);
    }
}