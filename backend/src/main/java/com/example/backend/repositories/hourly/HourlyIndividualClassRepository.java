package com.example.backend.repositories.hourly;

import com.example.backend.entities.hourly.HourlyIndividualClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HourlyIndividualClassRepository extends JpaRepository<HourlyIndividualClass, Long> {

    // ===== จากเดิม =====

    // ค้นหาคลาสที่ active
    @Query("SELECT c FROM HourlyIndividualClass c WHERE c.isActive = true ORDER BY c.createdAt DESC")
    List<HourlyIndividualClass> findAllActive();

    // ค้นหาคลาสของนักเรียน (ธรรมดา)
    @Query("SELECT c FROM HourlyIndividualClass c WHERE c.student.id = :studentId AND c.isActive = true " +
            "ORDER BY c.createdAt DESC")
    List<HourlyIndividualClass> findByStudentIdAndActive(@Param("studentId") Long studentId);

    // ค้นหาคลาสตามวิชา
    @Query("SELECT c FROM HourlyIndividualClass c WHERE c.subject.id = :subjectId AND c.isActive = true " +
            "ORDER BY c.createdAt DESC")
    List<HourlyIndividualClass> findBySubjectIdAndActive(@Param("subjectId") Long subjectId);

    // ค้นหาคลาสแบบ partial match
    @Query("SELECT c FROM HourlyIndividualClass c WHERE c.isActive = true " +
            "AND LOWER(c.className) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "ORDER BY c.createdAt DESC")
    List<HourlyIndividualClass> searchActiveClasses(@Param("search") String search);

    // ค้นหาคลาสตามหลายเงื่อนไข
    @Query("SELECT c FROM HourlyIndividualClass c WHERE c.isActive = true " +
            "AND (:subjectId IS NULL OR c.subject.id = :subjectId) " +
            "AND (:studentId IS NULL OR c.student.id = :studentId) " +
            "ORDER BY c.createdAt DESC")
    List<HourlyIndividualClass> findByMultipleCriteria(
            @Param("tutorId") Long tutorId,
            @Param("subjectId") Long subjectId,
            @Param("studentId") Long studentId
    );

    // นับจำนวนคลาสที่ active
    long countByIsActiveTrue();

    // นับจำนวนคลาสของติวเตอร์
    @Query("SELECT COUNT(c) FROM HourlyIndividualClass c WHERE c.isActive = true")
    long countByTutorIdAndActive(@Param("tutorId") Long tutorId);

    // นับจำนวนคลาสของนักเรียน
    @Query("SELECT COUNT(c) FROM HourlyIndividualClass c WHERE c.student.id = :studentId AND c.isActive = true")
    long countByStudentIdAndActive(@Param("studentId") Long studentId);

    // ค้นหาคลาสของนักเรียน
    @Query("SELECT c FROM HourlyIndividualClass c WHERE c.student.id = :studentId " +
            "ORDER BY c.createdAt DESC")
    List<HourlyIndividualClass> findByStudentId(@Param("studentId") Long studentId);
}