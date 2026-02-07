package com.example.backend.repositories;

import com.example.backend.entities.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {

    /**
     * ดึงวิชาทั้งหมดที่ active เรียงตาม displayOrder
     */
    List<Subject> findByIsActiveTrueOrderByDisplayOrderAsc();

    /**
     * ดึงวิชาทั้งหมด เรียงตาม displayOrder
     */
    List<Subject> findAllByOrderByDisplayOrderAsc();

    // ค้นหาด้วยชื่อวิชา
    Optional<Subject> findBySubjectName(String subjectName);

    // ค้นหาวิชาที่ active เรียงตาม displayOrder
    @Query("SELECT s FROM Subject s WHERE s.isActive = true ORDER BY s.displayOrder ASC")
    List<Subject> findAllActive();

    // ค้นหาวิชาทั้งหมดเรียงตาม displayOrder
    @Query("SELECT s FROM Subject s ORDER BY s.displayOrder ASC")
    List<Subject> findAllOrdered();

    // ค้นหาวิชาแบบ partial match (ชื่อวิชา)
    @Query("SELECT s FROM Subject s WHERE s.isActive = true " +
            "AND LOWER(s.subjectName) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "ORDER BY s.displayOrder ASC")
    List<Subject> searchActiveSubjects(@Param("search") String search);

    // นับจำนวนวิชาที่ active
    long countByIsActiveTrue();

    // ตรวจสอบว่ามีชื่อวิชานี้แล้วหรือไม่
    boolean existsBySubjectName(String subjectName);

    // ตรวจสอบว่ามีชื่อวิชานี้แล้วหรือไม่ (ยกเว้น id ที่ระบุ - สำหรับ update)
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM Subject s " +
            "WHERE s.subjectName = :subjectName AND s.id != :subjectId")
    boolean existsBySubjectNameAndIdNot(@Param("subjectName") String subjectName,
                                        @Param("subjectId") Long subjectId);

    // ค้นหา display order ที่ใหญ่ที่สุด (สำหรับเพิ่มวิชาใหม่)
    @Query("SELECT COALESCE(MAX(s.displayOrder), 0) FROM Subject s")
    Integer findMaxDisplayOrder();

    /**
     * ตรวจสอบว่าวิชานี้ถูกใช้ในคลาสหรือไม่
     */
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END " +
            "FROM HourlyGroupClass c WHERE c.subject.id = :subjectId AND c.isActive = true " +
            "OR EXISTS (SELECT 1 FROM HourlyIndividualClass ic WHERE ic.subject.id = :subjectId AND ic.isActive = true) " +
            "OR EXISTS (SELECT 1 FROM MonthlyClass mc WHERE mc.subject.id = :subjectId AND mc.isActive = true)")
    boolean isSubjectInUse(Long subjectId);
}