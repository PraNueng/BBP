package com.example.backend.services;

import com.example.backend.dtos.monthly.MonthlySubtypeDto;
import com.example.backend.entities.monthly.MonthlySubtype;
import com.example.backend.repositories.monthly.MonthlySubtypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@CacheConfig(cacheNames = "monthlySubtypes")
public class MonthlySubtypeService {

    @Autowired
    private MonthlySubtypeRepository monthlySubtypeRepository;

    /**
     * ดึงประเภทคลาสทั้งหมดที่ active เรียงตาม displayOrder
     */
    @Cacheable(key = "'active'")
    public List<MonthlySubtypeDto> getAllActiveSubtypes() {
        return monthlySubtypeRepository.findByIsActiveTrueOrderByDisplayOrderAsc().stream()
                .map(MonthlySubtypeDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * ดึงประเภทคลาสทั้งหมด (รวมที่ inactive)
     */
    @Cacheable(key = "'all'")
    public List<MonthlySubtypeDto> getAllSubtypes() {
        return monthlySubtypeRepository.findAllByOrderByDisplayOrderAsc().stream()
                .map(MonthlySubtypeDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * ดึงประเภทคลาสตาม ID
     */
    @Cacheable(key = "#id")
    public MonthlySubtypeDto getSubtypeById(Long id) {
        MonthlySubtype subtype = monthlySubtypeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Monthly Subtype not found: " + id));
        return MonthlySubtypeDto.fromEntity(subtype);
    }

    /**
     * เพิ่มประเภทคลาสใหม่
     */
    @Transactional
    @CacheEvict(allEntries = true)
    public MonthlySubtypeDto createSubtype(MonthlySubtypeDto dto) {
        // ตรวจสอบชื่อซ้ำ
        if (monthlySubtypeRepository.existsBySubtypeName(dto.getSubtypeName())) {
            throw new IllegalArgumentException("ประเภทคลาสนี้มีอยู่ในระบบแล้ว");
        }

        MonthlySubtype subtype = new MonthlySubtype();
        subtype.setSubtypeName(dto.getSubtypeName().trim());
        subtype.setIsActive(true);

        // กำหนด displayOrder อัตโนมัติ
        Integer maxOrder = monthlySubtypeRepository.findMaxDisplayOrder();
        subtype.setDisplayOrder(maxOrder != null ? maxOrder + 1 : 1);

        MonthlySubtype saved = monthlySubtypeRepository.save(subtype);
        return MonthlySubtypeDto.fromEntity(saved);
    }

    /**
     * แก้ไขข้อมูลประเภทคลาส
     */
    @Transactional
    @CacheEvict(allEntries = true)
    public MonthlySubtypeDto updateSubtype(Long id, MonthlySubtypeDto dto) {
        MonthlySubtype subtype = monthlySubtypeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Monthly Subtype not found: " + id));

        // ตรวจสอบชื่อซ้ำ (ยกเว้นตัวเอง)
        if (!subtype.getSubtypeName().equals(dto.getSubtypeName()) &&
                monthlySubtypeRepository.existsBySubtypeName(dto.getSubtypeName())) {
            throw new IllegalArgumentException("ประเภทคลาสนี้มีอยู่ในระบบแล้ว");
        }

        subtype.setSubtypeName(dto.getSubtypeName().trim());

        if (dto.getDisplayOrder() != null) {
            subtype.setDisplayOrder(dto.getDisplayOrder());
        }

        MonthlySubtype updated = monthlySubtypeRepository.save(subtype);
        return MonthlySubtypeDto.fromEntity(updated);
    }

    /**
     * เปิด/ปิดการใช้งาน
     */
    @Transactional
    @CacheEvict(allEntries = true)
    public MonthlySubtypeDto toggleActive(Long id) {
        MonthlySubtype subtype = monthlySubtypeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Monthly Subtype not found: " + id));

        subtype.setIsActive(!subtype.getIsActive());
        MonthlySubtype updated = monthlySubtypeRepository.save(subtype);
        return MonthlySubtypeDto.fromEntity(updated);
    }

    /**
     * ลบประเภทคลาส (soft delete)
     */
    @Transactional
    @CacheEvict(allEntries = true)
    public void deleteSubtype(Long id) {
        MonthlySubtype subtype = monthlySubtypeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Monthly Subtype not found: " + id));

        // ตรวจสอบว่ามีคลาสที่ใช้ประเภทนี้อยู่หรือไม่
        if (monthlySubtypeRepository.isSubtypeInUse(id)) {
            throw new IllegalStateException("ไม่สามารถลบประเภทคลาสนี้ได้ เนื่องจากมีคลาสที่ใช้ประเภทนี้อยู่");
        }

        subtype.setIsActive(false);
        monthlySubtypeRepository.save(subtype);
    }

    /**
     * อัพเดทลำดับการแสดงผล
     */
    @Transactional
    @CacheEvict(allEntries = true)
    public MonthlySubtypeDto updateDisplayOrder(Long id, Integer displayOrder) {
        MonthlySubtype subtype = monthlySubtypeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Monthly Subtype not found: " + id));

        subtype.setDisplayOrder(displayOrder);
        MonthlySubtype updated = monthlySubtypeRepository.save(subtype);
        return MonthlySubtypeDto.fromEntity(updated);
    }
}