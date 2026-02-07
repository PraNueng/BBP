package com.example.backend.services;

import com.example.backend.dtos.SubjectDto;
import com.example.backend.entities.Subject;
import com.example.backend.repositories.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@CacheConfig(cacheNames = "subjects")
public class SubjectService {

    @Autowired
    private SubjectRepository subjectRepository;

    /**
     * ดึงวิชาทั้งหมดที่ active เรียงตาม displayOrder
     */
    @Cacheable(key = "'active'")
    public List<SubjectDto> getAllActiveSubjects() {
        return subjectRepository.findByIsActiveTrueOrderByDisplayOrderAsc().stream()
                .map(SubjectDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * ดึงวิชาทั้งหมด (รวมที่ inactive)
     */
    @Cacheable(key = "'all'")
    public List<SubjectDto> getAllSubjects() {
        return subjectRepository.findAllByOrderByDisplayOrderAsc().stream()
                .map(SubjectDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * ดึงวิชาตาม ID
     */
    @Cacheable(key = "#id")
    public SubjectDto getSubjectById(Long id) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Subject not found: " + id));
        return SubjectDto.fromEntity(subject);
    }

    /**
     * เพิ่มวิชาใหม่
     */
    @Transactional
    @CacheEvict(allEntries = true)
    public SubjectDto createSubject(SubjectDto dto) {
        // ตรวจสอบชื่อซ้ำ
        if (subjectRepository.existsBySubjectName(dto.getSubjectName())) {
            throw new IllegalArgumentException("วิชานี้มีอยู่ในระบบแล้ว");
        }

        Subject subject = new Subject();
        subject.setSubjectName(dto.getSubjectName().trim());
        subject.setIsActive(true);

        // กำหนด displayOrder อัตโนมัติ
        Integer maxOrder = subjectRepository.findMaxDisplayOrder();
        subject.setDisplayOrder(maxOrder != null ? maxOrder + 1 : 1);

        Subject saved = subjectRepository.save(subject);
        return SubjectDto.fromEntity(saved);
    }

    /**
     * แก้ไขข้อมูลวิชา
     */
    @Transactional
    @CacheEvict(allEntries = true)
    public SubjectDto updateSubject(Long id, SubjectDto dto) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Subject not found: " + id));

        // ตรวจสอบชื่อซ้ำ (ยกเว้นตัวเอง)
        if (!subject.getSubjectName().equals(dto.getSubjectName()) &&
                subjectRepository.existsBySubjectName(dto.getSubjectName())) {
            throw new IllegalArgumentException("วิชานี้มีอยู่ในระบบแล้ว");
        }

        subject.setSubjectName(dto.getSubjectName().trim());

        if (dto.getDisplayOrder() != null) {
            subject.setDisplayOrder(dto.getDisplayOrder());
        }

        Subject updated = subjectRepository.save(subject);
        return SubjectDto.fromEntity(updated);
    }

    /**
     * เปิด/ปิดการใช้งาน
     */
    @Transactional
    @CacheEvict(allEntries = true)
    public SubjectDto toggleActive(Long id) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Subject not found: " + id));

        subject.setIsActive(!subject.getIsActive());
        Subject updated = subjectRepository.save(subject);
        return SubjectDto.fromEntity(updated);
    }

    /**
     * ลบวิชา (soft delete)
     */
    @Transactional
    @CacheEvict(allEntries = true)
    public void deleteSubject(Long id) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Subject not found: " + id));

        // ตรวจสอบว่ามีคลาสที่ใช้วิชานี้อยู่หรือไม่
        if (subjectRepository.isSubjectInUse(id)) {
            throw new IllegalStateException("ไม่สามารถลบวิชานี้ได้ เนื่องจากมีคลาสที่ใช้วิชานี้อยู่");
        }

        subject.setIsActive(false);
        subjectRepository.save(subject);
    }

    /**
     * อัพเดทลำดับการแสดงผล
     */
    @Transactional
    @CacheEvict(allEntries = true)
    public SubjectDto updateDisplayOrder(Long id, Integer displayOrder) {
        Subject subject = subjectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Subject not found: " + id));

        subject.setDisplayOrder(displayOrder);
        Subject updated = subjectRepository.save(subject);
        return SubjectDto.fromEntity(updated);
    }
}