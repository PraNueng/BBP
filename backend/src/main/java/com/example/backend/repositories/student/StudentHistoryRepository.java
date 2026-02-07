package com.example.backend.repositories.student;

import com.example.backend.entities.student.StudentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StudentHistoryRepository extends JpaRepository<StudentHistory, Long> {

    // ค้นหาประวัติการแก้ไขของนักเรียน
    @Query("SELECT h FROM StudentHistory h WHERE h.student.id = :studentId " +
            "ORDER BY h.updatedAt DESC")
    List<StudentHistory> findByStudentId(@Param("studentId") Long studentId);

    // ค้นหาประวัติการแก้ไขตามฟิลด์
    @Query("SELECT h FROM StudentHistory h WHERE h.student.id = :studentId " +
            "AND h.fieldName = :fieldName ORDER BY h.updatedAt DESC")
    List<StudentHistory> findByStudentIdAndFieldName(
            @Param("studentId") Long studentId,
            @Param("fieldName") String fieldName
    );

    // ค้นหาประวัติการแก้ไขโดย user
    @Query("SELECT h FROM StudentHistory h WHERE h.updatedBy.id = :userId " +
            "ORDER BY h.updatedAt DESC")
    List<StudentHistory> findByUpdatedBy(@Param("userId") Long userId);

    // ค้นหาประวัติการแก้ไขในช่วงเวลา
    @Query("SELECT h FROM StudentHistory h WHERE h.updatedAt BETWEEN :startDate AND :endDate " +
            "ORDER BY h.updatedAt DESC")
    List<StudentHistory> findByDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    // ค้นหาประวัติการแก้ไขล่าสุดของนักเรียน (จำกัดจำนวน)
    @Query("SELECT h FROM StudentHistory h WHERE h.student.id = :studentId " +
            "ORDER BY h.updatedAt DESC")
    List<StudentHistory> findRecentByStudentId(@Param("studentId") Long studentId);

    // นับจำนวนการแก้ไขของนักเรียน
    @Query("SELECT COUNT(h) FROM StudentHistory h WHERE h.student.id = :studentId")
    long countByStudentId(@Param("studentId") Long studentId);

    // นับจำนวนการแก้ไขตามฟิลด์
    @Query("SELECT COUNT(h) FROM StudentHistory h WHERE h.student.id = :studentId " +
            "AND h.fieldName = :fieldName")
    long countByStudentIdAndFieldName(
            @Param("studentId") Long studentId,
            @Param("fieldName") String fieldName
    );

    // สถิติการแก้ไขตามฟิลด์
    @Query("SELECT h.fieldName, COUNT(h.id) as totalChanges " +
            "FROM StudentHistory h " +
            "WHERE h.student.id = :studentId " +
            "GROUP BY h.fieldName " +
            "ORDER BY totalChanges DESC")
    List<Object[]> getChangeStatsByStudent(@Param("studentId") Long studentId);

    // สถิติการแก้ไขโดย user
    @Query("SELECT h.updatedBy.nickname, COUNT(h.id) as totalChanges " +
            "FROM StudentHistory h " +
            "GROUP BY h.updatedBy.nickname " +
            "ORDER BY totalChanges DESC")
    List<Object[]> getChangeStatsByUser();
}