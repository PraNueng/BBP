package com.example.backend.repositories.hourform;

import com.example.backend.entities.hourform.HourForm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public interface HourFormRepository extends JpaRepository<HourForm, Long> {

    // ===== BASIC QUERIES =====

    // ค้นหาตาม tutor
    @Query("SELECT h FROM HourForm h WHERE h.tutor.id = :tutorId AND (h.isAdminCreated IS NULL OR h.isAdminCreated = false)")
    List<HourForm> findByTutorId(@Param("tutorId") Long tutorId);

    // ค้นหาตาม class
    @Query("SELECT h FROM HourForm h WHERE h.classType = :classType AND h.classId = :classId " +
            "ORDER BY h.teachingDate DESC")
    List<HourForm> findByClassTypeAndClassId(
            @Param("classType") String classType,
            @Param("classId") Long classId
    );

    // ค้นหาตาม date range
    @Query("SELECT h FROM HourForm h WHERE h.teachingDate BETWEEN :startDate AND :endDate " +
            "ORDER BY h.teachingDate DESC")
    List<HourForm> findByDateRange(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    // ===== AGGREGATE QUERIES =====

    // รวมชั่วโมงตาม classType และ classId (สำหรับ Individual Class)
    @Query("SELECT COALESCE(SUM(h.hoursTaught), 0) FROM HourForm h " +
            "WHERE h.classType = :classType AND h.classId = :classId")
    BigDecimal sumHoursByClassTypeAndClassId(
            @Param("classType") String classType,
            @Param("classId") Long classId
    );

    // Alias method for backward compatibility
    default BigDecimal sumHoursByClassTypeAndId(String classType, Long classId) {
        return sumHoursByClassTypeAndClassId(classType, classId);
    }

    // รวมชั่วโมงหลาย classId แบบ batch (แก้ N+1) - สำหรับ getStudentWithClasses
    @Query("SELECT h.classId, SUM(h.hoursTaught) FROM HourForm h " +
            "WHERE h.classType = :classType AND h.classId IN :classIds " +
            "GROUP BY h.classId")
    List<Object[]> sumHoursByClassTypeAndClassIds(
            @Param("classType") String classType,
            @Param("classIds") List<Long> classIds
    );

    // แปลง List<Object[]> เป็น Map (Helper method)
    default Map<Long, BigDecimal> sumHoursByClassIds(String classType, List<Long> classIds) {
        if (classIds == null || classIds.isEmpty()) {
            return Map.of();
        }
        List<Object[]> results = sumHoursByClassTypeAndClassIds(classType, classIds);
        return results.stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (BigDecimal) row[1]
                ));
    }

    // รวมชั่วโมงของนักเรียน (สำหรับ Individual Classes)
    @Query("SELECT COALESCE(SUM(h.hoursTaught), 0) FROM HourForm h " +
            "JOIN HourlyIndividualClass c ON h.classId = c.id " +
            "WHERE c.student.id = :studentId AND h.classType = :classType")
    BigDecimal sumHoursByStudentId(
            @Param("studentId") Long studentId,
            @Param("classType") String classType
    );

    // ===== STATISTICS QUERIES =====

    // สถิติรายเดือนของติวเตอร์
    @Query("SELECT YEAR(h.teachingDate) as year, MONTH(h.teachingDate) as month, " +
            "COUNT(h.id) as totalForms, SUM(h.hoursTaught) as totalHours, " +
            "AVG(h.hoursTaught) as avgHours " +
            "FROM HourForm h " +
            "WHERE h.tutor.id = :tutorId " +
            "GROUP BY YEAR(h.teachingDate), MONTH(h.teachingDate) " +
            "ORDER BY YEAR(h.teachingDate) DESC, MONTH(h.teachingDate) DESC")
    List<Object[]> getTutorMonthlyStats(@Param("tutorId") Long tutorId);

    // สถิติตามวิชา
    @Query("SELECT h.subject.id, h.subject.subjectName, " +
            "COUNT(h.id) as totalForms, SUM(h.hoursTaught) as totalHours " +
            "FROM HourForm h " +
            "WHERE h.tutor.id = :tutorId " +
            "GROUP BY h.subject.id, h.subject.subjectName " +
            "ORDER BY totalHours DESC")
    List<Object[]> getTutorSubjectStats(@Param("tutorId") Long tutorId);

    // สถิติตาม classType
    @Query("SELECT h.classType, COUNT(h.id) as totalForms, SUM(h.hoursTaught) as totalHours " +
            "FROM HourForm h " +
            "WHERE h.tutor.id = :tutorId " +
            "GROUP BY h.classType")
    List<Object[]> getTutorClassTypeStats(@Param("tutorId") Long tutorId);

    // นับจำนวน forms
    @Query("SELECT COUNT(h) FROM HourForm h WHERE h.tutor.id = :tutorId")
    long countByTutorId(@Param("tutorId") Long tutorId);

    // รวมชั่วโมงทั้งหมดของติวเตอร์
    @Query("SELECT COALESCE(SUM(h.hoursTaught), 0) FROM HourForm h WHERE h.tutor.id = :tutorId")
    BigDecimal sumHoursByTutorId(@Param("tutorId") Long tutorId);

    // ===== SEARCH & FILTER =====

    // ค้นหาตามหลายเงื่อนไข
    @Query("SELECT h FROM HourForm h WHERE 1=1 " +
            "AND (:tutorId IS NULL OR h.tutor.id = :tutorId) " +
            "AND (:classType IS NULL OR h.classType = :classType) " +
            "AND (:subjectId IS NULL OR h.subject.id = :subjectId) " +
            "AND (:startDate IS NULL OR h.teachingDate >= :startDate) " +
            "AND (:endDate IS NULL OR h.teachingDate <= :endDate) " +
            "ORDER BY h.teachingDate DESC")
    List<HourForm> findByMultipleCriteria(
            @Param("tutorId") Long tutorId,
            @Param("classType") String classType,
            @Param("subjectId") Long subjectId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    // ===== RECENT & UPCOMING =====

    // Forms ล่าสุด
    @Query("SELECT h FROM HourForm h WHERE h.tutor.id = :tutorId " +
            "ORDER BY h.teachingDate DESC, h.createdAt DESC")
    List<HourForm> findRecentByTutorId(@Param("tutorId") Long tutorId);

    // Forms ในช่วงเวลา
    @Query("SELECT h FROM HourForm h " +
            "WHERE h.tutor.id = :tutorId " +
            "AND h.teachingDate BETWEEN :startDate AND :endDate " +
            "ORDER BY h.teachingDate ASC")
    List<HourForm> findByTutorIdAndDateRange(
            @Param("tutorId") Long tutorId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("""
    SELECT h FROM HourForm h
    JOIN HourlyIndividualClass c ON h.classId = c.id
    WHERE h.classType = 'hourly_individual'
    AND c.student.id = :studentId
    AND c.student.grade.id = :gradeId
    """)
    List<HourForm> findByStudentIdAndGradeId(
            @Param("studentId") Long studentId,
            @Param("gradeId") Long gradeId
    );

    /**
     * ค้นหา Hour Forms ด้วย search term (ค้นจาก content, remark, tutor name, subject name)
     */
    @Query("SELECT h FROM HourForm h " +
            "JOIN h.tutor t " +
            "JOIN h.subject s " +
            "WHERE LOWER(h.content) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(h.remark) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(t.nickname) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(s.subjectName) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<HourForm> findBySearch(@Param("search") String search, Pageable pageable);

    /**
     * กรองตามเดือน
     */
    @Query("SELECT h FROM HourForm h " +
            "WHERE MONTH(h.teachingDate) IN :months")
    Page<HourForm> findByMonths(@Param("months") List<Integer> months, Pageable pageable);

    /**
     * ค้นหาและกรองตามเดือน
     */
    @Query("SELECT h FROM HourForm h " +
            "JOIN h.tutor t " +
            "JOIN h.subject s " +
            "WHERE (LOWER(h.content) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(h.remark) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(t.nickname) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(s.subjectName) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "AND MONTH(h.teachingDate) IN :months")
    Page<HourForm> findBySearchAndMonths(
            @Param("search") String search,
            @Param("months") List<Integer> months,
            Pageable pageable
    );

    List<HourForm> findByClassIdAndClassType(Long classId, String classType);

    List<HourForm> findByTutorIdAndClassIdAndClassType(Long tutorId, Long classId, String classType);

    List<HourForm> findByTeachingDateBetween(LocalDate startDate, LocalDate endDate);

    List<HourForm> findByTutorIdAndTeachingDateBetween(Long tutorId, LocalDate startDate, LocalDate endDate);

    List<HourForm> findBySubjectId(Long subjectId);
}