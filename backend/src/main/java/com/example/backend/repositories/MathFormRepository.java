package com.example.backend.repositories;

import com.example.backend.entities.MathForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface MathFormRepository extends JpaRepository<MathForm, Long> {

    // ค้นหาตาม tutor
    @Query("SELECT m FROM MathForm m WHERE m.tutor.id = :tutorId " +
            "AND (m.isAdminCreated IS NULL OR m.isAdminCreated = false) " +
            "ORDER BY m.teachingDate DESC")
    List<MathForm> findByTutorId(@Param("tutorId") Long tutorId);

    // ค้นหาตาม class
    @Query("SELECT m FROM MathForm m WHERE m.monthlyClass.id = :classId " +
            "ORDER BY m.teachingDate DESC")
    List<MathForm> findByClassId(@Param("classId") Long classId);

    // ค้นหาตาม date range
    @Query("SELECT m FROM MathForm m WHERE m.teachingDate BETWEEN :startDate AND :endDate " +
            "ORDER BY m.teachingDate DESC")
    List<MathForm> findByDateRange(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    // ค้นหาตาม tutor และ date range
    @Query("SELECT m FROM MathForm m " +
            "WHERE m.tutor.id = :tutorId " +
            "AND m.teachingDate BETWEEN :startDate AND :endDate " +
            "ORDER BY m.teachingDate DESC")
    List<MathForm> findByTutorIdAndDateRange(
            @Param("tutorId") Long tutorId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    // รวมชั่วโมงของติวเตอร์
    @Query("SELECT COALESCE(SUM(m.hoursTaught), 0) FROM MathForm m WHERE m.tutor.id = :tutorId")
    BigDecimal sumHoursByTutorId(@Param("tutorId") Long tutorId);

    // นับจำนวน forms
    @Query("SELECT COUNT(m) FROM MathForm m WHERE m.tutor.id = :tutorId")
    long countByTutorId(@Param("tutorId") Long tutorId);

    // ค้นหาตาม tutor และ class
    List<MathForm> findByTutorIdAndMonthlyClassId(Long tutorId, Long classId);
}