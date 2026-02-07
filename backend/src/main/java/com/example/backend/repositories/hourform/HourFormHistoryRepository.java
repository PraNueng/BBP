package com.example.backend.repositories.hourform;

import com.example.backend.entities.hourform.HourFormHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HourFormHistoryRepository extends JpaRepository<HourFormHistory, Long> {

    // ⭐ เพิ่ม method ที่ถูกเรียกใช้ใน HourFormService
    @Query("SELECT h FROM HourFormHistory h WHERE h.hourForm.id = :hourFormId " +
            "ORDER BY h.updatedAt DESC")
    List<HourFormHistory> findByHourFormIdOrderByUpdatedAtDesc(@Param("hourFormId") Long hourFormId);

    // ค้นหาประวัติการแก้ไขของ hour form (method เดิม)
    @Query("SELECT h FROM HourFormHistory h WHERE h.hourForm.id = :hourFormId " +
            "ORDER BY h.updatedAt DESC")
    List<HourFormHistory> findByHourFormId(@Param("hourFormId") Long hourFormId);

    // ค้นหาประวัติการแก้ไขตามฟิลด์
    @Query("SELECT h FROM HourFormHistory h WHERE h.hourForm.id = :hourFormId " +
            "AND h.fieldName = :fieldName ORDER BY h.updatedAt DESC")
    List<HourFormHistory> findByHourFormIdAndFieldName(
            @Param("hourFormId") Long hourFormId,
            @Param("fieldName") String fieldName
    );

    // ค้นหาประวัติการแก้ไขโดย user
    @Query("SELECT h FROM HourFormHistory h WHERE h.updatedBy.id = :userId " +
            "ORDER BY h.updatedAt DESC")
    List<HourFormHistory> findByUpdatedBy(@Param("userId") Long userId);

    // ค้นหาประวัติการแก้ไขในช่วงเวลา
    @Query("SELECT h FROM HourFormHistory h WHERE h.updatedAt BETWEEN :startDate AND :endDate " +
            "ORDER BY h.updatedAt DESC")
    List<HourFormHistory> findByDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    // ค้นหาประวัติการแก้ไข hour forms ของติวเตอร์
    @Query("SELECT h FROM HourFormHistory h " +
            "WHERE h.hourForm.tutor.id = :tutorId " +
            "ORDER BY h.updatedAt DESC")
    List<HourFormHistory> findByTutorId(@Param("tutorId") Long tutorId);

    // ค้นหาประวัติการแก้ไขล่าสุดของ hour form
    @Query("SELECT h FROM HourFormHistory h WHERE h.hourForm.id = :hourFormId " +
            "ORDER BY h.updatedAt DESC")
    List<HourFormHistory> findRecentByHourFormId(@Param("hourFormId") Long hourFormId);

    // นับจำนวนการแก้ไขของ hour form
    @Query("SELECT COUNT(h) FROM HourFormHistory h WHERE h.hourForm.id = :hourFormId")
    long countByHourFormId(@Param("hourFormId") Long hourFormId);

    // นับจำนวนการแก้ไขตามฟิลด์
    @Query("SELECT COUNT(h) FROM HourFormHistory h WHERE h.hourForm.id = :hourFormId " +
            "AND h.fieldName = :fieldName")
    long countByHourFormIdAndFieldName(
            @Param("hourFormId") Long hourFormId,
            @Param("fieldName") String fieldName
    );

    // สถิติการแก้ไขตามฟิลด์
    @Query("SELECT h.fieldName, COUNT(h.id) as totalChanges " +
            "FROM HourFormHistory h " +
            "WHERE h.hourForm.id = :hourFormId " +
            "GROUP BY h.fieldName " +
            "ORDER BY totalChanges DESC")
    List<Object[]> getChangeStatsByHourForm(@Param("hourFormId") Long hourFormId);

    // สถิติการแก้ไขโดย user
    @Query("SELECT h.updatedBy.nickname, COUNT(h.id) as totalChanges " +
            "FROM HourFormHistory h " +
            "GROUP BY h.updatedBy.nickname " +
            "ORDER BY totalChanges DESC")
    List<Object[]> getChangeStatsByUser();

    // สถิติการแก้ไขโดยติวเตอร์
    @Query("SELECT h.hourForm.tutor.nickname, COUNT(h.id) as totalChanges " +
            "FROM HourFormHistory h " +
            "GROUP BY h.hourForm.tutor.nickname " +
            "ORDER BY totalChanges DESC")
    List<Object[]> getChangeStatsByTutor();
}