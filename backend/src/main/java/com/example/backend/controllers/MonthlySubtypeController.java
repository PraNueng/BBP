package com.example.backend.controllers;

import com.example.backend.dtos.monthly.MonthlySubtypeDto;
import com.example.backend.services.MonthlySubtypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/monthly-subtypes")
public class MonthlySubtypeController {

    @Autowired
    private MonthlySubtypeService monthlySubtypeService;

    /**
     * ดึงรายการประเภทคลาสรายเดือนทั้งหมด (เฉพาะที่ active)
     */
    @GetMapping
    public ResponseEntity<List<MonthlySubtypeDto>> getAllActiveSubtypes() {
        List<MonthlySubtypeDto> subtypes = monthlySubtypeService.getAllActiveSubtypes();
        return ResponseEntity.ok(subtypes);
    }

    /**
     * ดึงรายการประเภทคลาสรายเดือนทั้งหมด (รวมที่ inactive) - สำหรับ Admin
     */
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<MonthlySubtypeDto>> getAllSubtypes() {
        List<MonthlySubtypeDto> subtypes = monthlySubtypeService.getAllSubtypes();
        return ResponseEntity.ok(subtypes);
    }

    /**
     * ดึงข้อมูลประเภทคลาสตาม ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<MonthlySubtypeDto> getSubtypeById(@PathVariable Long id) {
        MonthlySubtypeDto subtype = monthlySubtypeService.getSubtypeById(id);
        return ResponseEntity.ok(subtype);
    }

    /**
     * เพิ่มประเภทคลาสรายเดือนใหม่
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MonthlySubtypeDto> createSubtype(@RequestBody MonthlySubtypeDto dto) {
        MonthlySubtypeDto created = monthlySubtypeService.createSubtype(dto);
        return ResponseEntity.ok(created);
    }

    /**
     * แก้ไขข้อมูลประเภทคลาส
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MonthlySubtypeDto> updateSubtype(
            @PathVariable Long id,
            @RequestBody MonthlySubtypeDto dto
    ) {
        MonthlySubtypeDto updated = monthlySubtypeService.updateSubtype(id, dto);
        return ResponseEntity.ok(updated);
    }

    /**
     * เปิด/ปิดการใช้งานประเภทคลาส
     */
    @PatchMapping("/{id}/toggle-active")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MonthlySubtypeDto> toggleActive(@PathVariable Long id) {
        MonthlySubtypeDto updated = monthlySubtypeService.toggleActive(id);
        return ResponseEntity.ok(updated);
    }

    /**
     * ลบประเภทคลาส (soft delete - เปลี่ยนเป็น inactive)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteSubtype(@PathVariable Long id) {
        monthlySubtypeService.deleteSubtype(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * อัพเดทลำดับการแสดงผล
     */
    @PatchMapping("/{id}/display-order")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MonthlySubtypeDto> updateDisplayOrder(
            @PathVariable Long id,
            @RequestParam Integer displayOrder
    ) {
        MonthlySubtypeDto updated = monthlySubtypeService.updateDisplayOrder(id, displayOrder);
        return ResponseEntity.ok(updated);
    }
}