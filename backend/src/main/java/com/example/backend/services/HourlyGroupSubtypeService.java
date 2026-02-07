package com.example.backend.services;

import com.example.backend.dtos.hourly.HourlyGroupSubtypeDto;
import com.example.backend.entities.hourly.HourlyGroupSubtype;
import com.example.backend.repositories.hourly.HourlyGroupSubtypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@CacheConfig(cacheNames = "hourlyGroupSubtypes")
public class HourlyGroupSubtypeService {

    @Autowired
    private HourlyGroupSubtypeRepository hourlyGroupSubtypeRepository;

    /**
     * ดึงประเภทกลุ่มทั้งหมดที่ active เรียงตาม displayOrder
     */
    @Cacheable(key = "'active'")
    public List<HourlyGroupSubtypeDto> getAllActiveSubtypes() {
        return hourlyGroupSubtypeRepository.findByIsActiveTrueOrderByDisplayOrderAsc().stream()
                .map(HourlyGroupSubtypeDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * ดึงประเภทกลุ่มทั้งหมด (รวมที่ inactive)
     */
    @Cacheable(key = "'all'")
    public List<HourlyGroupSubtypeDto> getAllSubtypes() {
        return hourlyGroupSubtypeRepository.findAllByOrderByDisplayOrderAsc().stream()
                .map(HourlyGroupSubtypeDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * ดึงประเภทกลุ่มตาม ID
     */
    @Cacheable(key = "#id")
    public HourlyGroupSubtypeDto getSubtypeById(Long id) {
        HourlyGroupSubtype subtype = hourlyGroupSubtypeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Hourly Group Subtype not found: " + id));
        return HourlyGroupSubtypeDto.fromEntity(subtype);
    }

    /**
     * เพิ่มประเภทกลุ่มใหม่
     */
    @Transactional
    @CacheEvict(allEntries = true)
    public HourlyGroupSubtypeDto createSubtype(HourlyGroupSubtypeDto dto) {
        // ตรวจสอบชื่อซ้ำ
        if (hourlyGroupSubtypeRepository.existsBySubtypeName(dto.getSubtypeName())) {
            throw new IllegalArgumentException("ประเภทกลุ่มนี้มีอยู่ในระบบแล้ว");
        }

        HourlyGroupSubtype subtype = new HourlyGroupSubtype();
        subtype.setSubtypeName(dto.getSubtypeName().trim());
        subtype.setIsActive(true);

        // กำหนด displayOrder อัตโนมัติ
        Integer maxOrder = hourlyGroupSubtypeRepository.findMaxDisplayOrder();
        subtype.setDisplayOrder(maxOrder != null ? maxOrder + 1 : 1);

        HourlyGroupSubtype saved = hourlyGroupSubtypeRepository.save(subtype);
        return HourlyGroupSubtypeDto.fromEntity(saved);
    }

    /**
     * แก้ไขข้อมูลประเภทกลุ่ม
     */
    @Transactional
    @CacheEvict(allEntries = true)
    public HourlyGroupSubtypeDto updateSubtype(Long id, HourlyGroupSubtypeDto dto) {
        HourlyGroupSubtype subtype = hourlyGroupSubtypeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Hourly Group Subtype not found: " + id));

        // ตรวจสอบชื่อซ้ำ (ยกเว้นตัวเอง)
        if (!subtype.getSubtypeName().equals(dto.getSubtypeName()) &&
                hourlyGroupSubtypeRepository.existsBySubtypeName(dto.getSubtypeName())) {
            throw new IllegalArgumentException("ประเภทกลุ่มนี้มีอยู่ในระบบแล้ว");
        }

        subtype.setSubtypeName(dto.getSubtypeName().trim());

        if (dto.getDisplayOrder() != null) {
            subtype.setDisplayOrder(dto.getDisplayOrder());
        }

        HourlyGroupSubtype updated = hourlyGroupSubtypeRepository.save(subtype);
        return HourlyGroupSubtypeDto.fromEntity(updated);
    }

    /**
     * เปิด/ปิดการใช้งาน
     */
    @Transactional
    @CacheEvict(allEntries = true)
    public HourlyGroupSubtypeDto toggleActive(Long id) {
        HourlyGroupSubtype subtype = hourlyGroupSubtypeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Hourly Group Subtype not found: " + id));

        subtype.setIsActive(!subtype.getIsActive());
        HourlyGroupSubtype updated = hourlyGroupSubtypeRepository.save(subtype);
        return HourlyGroupSubtypeDto.fromEntity(updated);
    }

    /**
     * ลบประเภทกลุ่ม (soft delete)
     */
    @Transactional
    @CacheEvict(allEntries = true)
    public void deleteSubtype(Long id) {
        HourlyGroupSubtype subtype = hourlyGroupSubtypeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Hourly Group Subtype not found: " + id));

        // ตรวจสอบว่ามีคลาสที่ใช้ประเภทนี้อยู่หรือไม่
        if (hourlyGroupSubtypeRepository.isSubtypeInUse(id)) {
            throw new IllegalStateException("ไม่สามารถลบประเภทกลุ่มนี้ได้ เนื่องจากมีคลาสที่ใช้ประเภทนี้อยู่");
        }

        subtype.setIsActive(false);
        hourlyGroupSubtypeRepository.save(subtype);
    }

    /**
     * อัพเดทลำดับการแสดงผล
     */
    @Transactional
    @CacheEvict(allEntries = true)
    public HourlyGroupSubtypeDto updateDisplayOrder(Long id, Integer displayOrder) {
        HourlyGroupSubtype subtype = hourlyGroupSubtypeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Hourly Group Subtype not found: " + id));

        subtype.setDisplayOrder(displayOrder);
        HourlyGroupSubtype updated = hourlyGroupSubtypeRepository.save(subtype);
        return HourlyGroupSubtypeDto.fromEntity(updated);
    }
}