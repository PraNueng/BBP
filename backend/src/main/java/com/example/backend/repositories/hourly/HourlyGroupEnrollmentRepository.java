package com.example.backend.repositories.hourly;

import com.example.backend.entities.hourly.HourlyGroupEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface HourlyGroupEnrollmentRepository extends JpaRepository<HourlyGroupEnrollment, Long> {

    // ค้นหา enrollments ของนักเรียน (พร้อม JOIN FETCH)
    @Query("SELECT e FROM HourlyGroupEnrollment e " +
            "JOIN FETCH e.hourlyGroupClass c " +
            "JOIN FETCH c.subject " +
            "JOIN FETCH c.subtype " +
            "WHERE e.student.id = :studentId " +
            "ORDER BY e.enrolledAt DESC")
    List<HourlyGroupEnrollment> findByStudentIdWithClasses(@Param("studentId") Long studentId);

    // ค้นหา enrollments ของนักเรียน (ธรรมดา)
    @Query("SELECT e FROM HourlyGroupEnrollment e WHERE e.student.id = :studentId " +
            "ORDER BY e.enrolledAt DESC")
    List<HourlyGroupEnrollment> findByStudentId(@Param("studentId") Long studentId);

    @Query("""
        SELECT e 
        FROM HourlyGroupEnrollment e 
        WHERE e.student.id = :studentId
          AND e.isActive = true
        ORDER BY e.enrolledAt DESC
    """)
    List<HourlyGroupEnrollment> findByStudentIdAndIsActiveTrue(@Param("studentId") Long studentId);

    // ค้นหา enrollments ของคลาส
    @Query("SELECT e FROM HourlyGroupEnrollment e WHERE e.hourlyGroupClass.id = :classId " +
            "ORDER BY e.student.firstName ASC")
    List<HourlyGroupEnrollment> findByClassId(@Param("classId") Long classId);

    // ค้นหา enrollment เฉพาะ
    @Query("SELECT e FROM HourlyGroupEnrollment e WHERE e.hourlyGroupClass.id = :classId " +
            "AND e.student.id = :studentId")
    Optional<HourlyGroupEnrollment> findByClassIdAndStudentId(
            @Param("classId") Long classId,
            @Param("studentId") Long studentId
    );

    // ตรวจสอบว่านักเรียนลงทะเบียนในคลาสแล้วหรือไม่
    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM HourlyGroupEnrollment e " +
            "WHERE e.hourlyGroupClass.id = :classId AND e.student.id = :studentId")
    boolean existsByClassIdAndStudentId(
            @Param("classId") Long classId,
            @Param("studentId") Long studentId
    );

    // นับจำนวนนักเรียนในคลาส
    @Query("SELECT COUNT(e) FROM HourlyGroupEnrollment e WHERE e.hourlyGroupClass.id = :classId")
    long countByClassId(@Param("classId") Long classId);

    // นับจำนวนคลาสของนักเรียน
    @Query("SELECT COUNT(e) FROM HourlyGroupEnrollment e WHERE e.student.id = :studentId")
    long countByStudentId(@Param("studentId") Long studentId);

    @Query("""
    SELECT e FROM HourlyGroupEnrollment e
    WHERE e.hourlyGroupClass.id = :classId
      AND e.isActive = true
    """)
    List<HourlyGroupEnrollment> findByClassIdAndIsActiveTrue(@Param("classId") Long classId);
}