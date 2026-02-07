package com.example.backend.repositories;

import com.example.backend.entities.ScienceForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ScienceFormRepository extends JpaRepository<ScienceForm, Long> {

    // ค้นหาตาม tutor
    @Query("SELECT s FROM ScienceForm s WHERE s.tutor.id = :tutorId " +
            "AND (s.isAdminCreated IS NULL OR s.isAdminCreated = false) " +
            "ORDER BY s.teachingDate DESC")
    List<ScienceForm> findByTutorId(@Param("tutorId") Long tutorId);

    // ค้นหาตาม class
    @Query("SELECT s FROM ScienceForm s WHERE s.monthlyClass.id = :classId " +
            "ORDER BY s.teachingDate DESC")
    List<ScienceForm> findByClassId(@Param("classId") Long classId);

    // ค้นหาตาม date range
    @Query("SELECT s FROM ScienceForm s WHERE s.teachingDate BETWEEN :startDate AND :endDate " +
            "ORDER BY s.teachingDate DESC")
    List<ScienceForm> findByDateRange(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    // ค้นหาตาม tutor และ date range
    @Query("SELECT s FROM ScienceForm s " +
            "WHERE s.tutor.id = :tutorId " +
            "AND s.teachingDate BETWEEN :startDate AND :endDate " +
            "ORDER BY s.teachingDate DESC")
    List<ScienceForm> findByTutorIdAndDateRange(
            @Param("tutorId") Long tutorId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    // รวมชั่วโมงของติวเตอร์
    @Query("SELECT COALESCE(SUM(s.hoursTaught), 0) FROM ScienceForm s WHERE s.tutor.id = :tutorId")
    BigDecimal sumHoursByTutorId(@Param("tutorId") Long tutorId);

    // นับจำนวน forms
    @Query("SELECT COUNT(s) FROM ScienceForm s WHERE s.tutor.id = :tutorId")
    long countByTutorId(@Param("tutorId") Long tutorId);

    // ค้นหาตาม tutor และ class
    List<ScienceForm> findByTutorIdAndMonthlyClassId(Long tutorId, Long classId);
}