package com.example.backend.controllers;

import com.example.backend.dtos.hourly.HourlyGroupSubtypeDto;
import com.example.backend.services.HourlyGroupSubtypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hourly-group-subtypes")
public class HourlyGroupSubtypeController {

    @Autowired
    private HourlyGroupSubtypeService hourlyGroupSubtypeService;

    /**
     * ดึงรายการประเภทกลุ่มรายชั่วโมงทั้งหมด (เฉพาะที่ active)
     */
    @GetMapping
    public ResponseEntity<List<HourlyGroupSubtypeDto>> getAllActiveSubtypes() {
        List<HourlyGroupSubtypeDto> subtypes = hourlyGroupSubtypeService.getAllActiveSubtypes();
        return ResponseEntity.ok(subtypes);
    }

    /**
     * ดึงรายการประเภทกลุ่มรายชั่วโมงทั้งหมด (รวมที่ inactive) - สำหรับ Admin
     */
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<HourlyGroupSubtypeDto>> getAllSubtypes() {
        List<HourlyGroupSubtypeDto> subtypes = hourlyGroupSubtypeService.getAllSubtypes();
        return ResponseEntity.ok(subtypes);
    }

    /**
     * ดึงข้อมูลประเภทกลุ่มตาม ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<HourlyGroupSubtypeDto> getSubtypeById(@PathVariable Long id) {
        HourlyGroupSubtypeDto subtype = hourlyGroupSubtypeService.getSubtypeById(id);
        return ResponseEntity.ok(subtype);
    }

    /**
     * เพิ่มประเภทกลุ่มรายชั่วโมงใหม่
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<HourlyGroupSubtypeDto> createSubtype(@RequestBody HourlyGroupSubtypeDto dto) {
        HourlyGroupSubtypeDto created = hourlyGroupSubtypeService.createSubtype(dto);
        return ResponseEntity.ok(created);
    }

    /**
     * แก้ไขข้อมูลประเภทกลุ่ม
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<HourlyGroupSubtypeDto> updateSubtype(
            @PathVariable Long id,
            @RequestBody HourlyGroupSubtypeDto dto
    ) {
        HourlyGroupSubtypeDto updated = hourlyGroupSubtypeService.updateSubtype(id, dto);
        return ResponseEntity.ok(updated);
    }

    /**
     * เปิด/ปิดการใช้งานประเภทกลุ่ม
     */
    @PatchMapping("/{id}/toggle-active")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<HourlyGroupSubtypeDto> toggleActive(@PathVariable Long id) {
        HourlyGroupSubtypeDto updated = hourlyGroupSubtypeService.toggleActive(id);
        return ResponseEntity.ok(updated);
    }

    /**
     * ลบประเภทกลุ่ม (soft delete - เปลี่ยนเป็น inactive)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteSubtype(@PathVariable Long id) {
        hourlyGroupSubtypeService.deleteSubtype(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * อัพเดทลำดับการแสดงผล
     */
    @PatchMapping("/{id}/display-order")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<HourlyGroupSubtypeDto> updateDisplayOrder(
            @PathVariable Long id,
            @RequestParam Integer displayOrder
    ) {
        HourlyGroupSubtypeDto updated = hourlyGroupSubtypeService.updateDisplayOrder(id, displayOrder);
        return ResponseEntity.ok(updated);
    }
}