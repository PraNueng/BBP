package com.example.backend.repositories.monthly;

import com.example.backend.entities.monthly.MonthlyClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MonthlyClassRepository extends JpaRepository<MonthlyClass, Long> {

    // ค้นหาคลาสที่ active
    @Query("SELECT c FROM MonthlyClass c WHERE c.isActive = true ORDER BY c.startDate DESC")
    List<MonthlyClass> findAllActive();

    // ค้นหาคลาสตามวิชา
    @Query("SELECT c FROM MonthlyClass c WHERE c.subject.id = :subjectId AND c.isActive = true " +
            "ORDER BY c.startDate DESC")
    List<MonthlyClass> findBySubjectIdAndActive(@Param("subjectId") Long subjectId);

    // ค้นหาคลาสตาม subtype
    @Query("SELECT c FROM MonthlyClass c WHERE c.subtype.id = :subtypeId AND c.isActive = true " +
            "ORDER BY c.startDate DESC")
    List<MonthlyClass> findBySubtypeIdAndActive(@Param("subtypeId") Long subtypeId);

    // ค้นหาคลาสพร้อม enrollments
    @Query("SELECT DISTINCT c FROM MonthlyClass c " +
            "LEFT JOIN FETCH c.enrollments " +
            "WHERE c.id = :id")
    Optional<MonthlyClass> findByIdWithEnrollments(@Param("id") Long id);

    // ค้นหาคลาสแบบ partial match
    @Query("SELECT c FROM MonthlyClass c WHERE c.isActive = true " +
            "AND LOWER(c.className) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "ORDER BY c.startDate DESC")
    List<MonthlyClass> searchActiveClasses(@Param("search") String search);

    // ค้นหาคลาสตามช่วงวันที่
    @Query("SELECT c FROM MonthlyClass c WHERE c.isActive = true " +
            "AND c.startDate <= :endDate AND c.endDate >= :startDate " +
            "ORDER BY c.startDate DESC")
    List<MonthlyClass> findByDateRange(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    // ค้นหาคลาสที่กำลังดำเนินการอยู่
    @Query("SELECT c FROM MonthlyClass c WHERE c.isActive = true " +
            "AND c.startDate <= :currentDate AND c.endDate >= :currentDate " +
            "ORDER BY c.startDate DESC")
    List<MonthlyClass> findActiveOnDate(@Param("currentDate") LocalDate currentDate);

    // ค้นหาคลาสที่จะเริ่มในอนาคต
    @Query("SELECT c FROM MonthlyClass c WHERE c.isActive = true " +
            "AND c.startDate > :currentDate " +
            "ORDER BY c.startDate ASC")
    List<MonthlyClass> findUpcomingClasses(@Param("currentDate") LocalDate currentDate);

    // ค้นหาคลาสที่สิ้นสุดแล้ว
    @Query("SELECT c FROM MonthlyClass c WHERE c.isActive = true " +
            "AND c.endDate < :currentDate " +
            "ORDER BY c.endDate DESC")
    List<MonthlyClass> findPastClasses(@Param("currentDate") LocalDate currentDate);

    // ค้นหาคลาสตามหลายเงื่อนไข
    @Query("SELECT c FROM MonthlyClass c WHERE c.isActive = true " +
            "AND (:subjectId IS NULL OR c.subject.id = :subjectId) " +
            "AND (:subtypeId IS NULL OR c.subtype.id = :subtypeId) " +
            "ORDER BY c.startDate DESC")
    List<MonthlyClass> findByMultipleCriteria(
            @Param("tutorId") Long tutorId,
            @Param("subjectId") Long subjectId,
            @Param("subtypeId") Long subtypeId
    );

    // นับจำนวนคลาสที่ active
    long countByIsActiveTrue();

    // นับจำนวนคลาสของติวเตอร์
    @Query("SELECT COUNT(c) FROM MonthlyClass c WHERE c.isActive = true")
    long countByTutorIdAndActive(@Param("tutorId") Long tutorId);

    // ค้นหาคลาสที่นักเรียนเรียน
    @Query("SELECT DISTINCT c FROM MonthlyClass c " +
            "JOIN c.enrollments e " +
            "WHERE e.student.id = :studentId AND c.isActive = true " +
            "ORDER BY c.startDate DESC")
    List<MonthlyClass> findByStudentId(@Param("studentId") Long studentId);

    // สถิติคลาสรายเดือน
    @Query("SELECT c.id, c.className, c.startDate, c.endDate, " +
            "COUNT(DISTINCT e.student.id) as totalStudents " +
            "FROM MonthlyClass c " +
            "LEFT JOIN c.enrollments e " +
            "WHERE c.isActive = true " +
            "GROUP BY c.id, c.className, c.startDate, c.endDate " +
            "ORDER BY c.startDate DESC")
    List<Object[]> findClassSummary();

    /**
     * หาคลาส Monthly ตาม grade และ subtype ที่ active
     */
    @Query("SELECT m FROM MonthlyClass m " +
            "WHERE m.grade.id = :gradeId " +
            "AND m.subtype.id = :subtypeId " +
            "AND m.isActive = true")
    List<MonthlyClass> findByGradeIdAndSubtypeIdAndIsActiveTrue(
            @Param("gradeId") Long gradeId,
            @Param("subtypeId") Long subtypeId
    );

    /**
     * หาคลาสทั้งหมดที่ active
     */
    List<MonthlyClass> findByIsActiveTrue();

    @Query("SELECT c FROM MonthlyClass c WHERE c.isActive = true " +
            "ORDER BY c.startDate DESC")
    List<MonthlyClass> findClassesWithoutTutor();

    /**
     * ดึงคลาสทั้งหมดพร้อม tutors (EAGER FETCH)
     */
    @Query("SELECT DISTINCT c FROM MonthlyClass c " +
            "LEFT JOIN FETCH c.classTutors ct " +
            "LEFT JOIN FETCH ct.tutor")
    List<MonthlyClass> findAllWithTutors();

    /**
     * ดึงคลาส active พร้อม tutors (EAGER FETCH)
     */
    @Query("SELECT DISTINCT c FROM MonthlyClass c " +
            "LEFT JOIN FETCH c.classTutors ct " +
            "LEFT JOIN FETCH ct.tutor " +
            "WHERE c.isActive = true")
    List<MonthlyClass> findAllActiveWithTutors();

    /**
     * ดึงคลาสเดี่ยวพร้อม tutors
     */
    @Query("SELECT c FROM MonthlyClass c " +
            "LEFT JOIN FETCH c.classTutors ct " +
            "LEFT JOIN FETCH ct.tutor " +
            "WHERE c.id = :id")
    Optional<MonthlyClass> findByIdWithTutors(@Param("id") Long id);
}