package com.example.backend.repositories.hourly;

import com.example.backend.entities.hourly.HourlyGroupClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HourlyGroupClassRepository extends JpaRepository<HourlyGroupClass, Long> {

    /**
     * ดึงคลาสทั้งหมดพร้อม tutors (EAGER FETCH)
     */
    @Query("SELECT DISTINCT c FROM HourlyGroupClass c " +
            "LEFT JOIN FETCH c.classTutors ct " +
            "LEFT JOIN FETCH ct.tutor " +
            "ORDER BY c.createdAt DESC")
    List<HourlyGroupClass> findAllWithTutors();

    /**
     * ดึงคลาส active พร้อม tutors (EAGER FETCH)
     */
    @Query("SELECT DISTINCT c FROM HourlyGroupClass c " +
            "LEFT JOIN FETCH c.classTutors ct " +
            "LEFT JOIN FETCH ct.tutor " +
            "WHERE c.isActive = true " +
            "ORDER BY c.createdAt DESC")
    List<HourlyGroupClass> findAllActiveWithTutors();

    /**
     * ดึงคลาสเดี่ยวพร้อม tutors (EAGER FETCH)
     */
    @Query("SELECT c FROM HourlyGroupClass c " +
            "LEFT JOIN FETCH c.classTutors ct " +
            "LEFT JOIN FETCH ct.tutor " +
            "WHERE c.id = :id")
    Optional<HourlyGroupClass> findByIdWithTutors(@Param("id") Long id);

    // ============================================
    // EXISTING QUERIES (Keep as is)
    // ============================================

    @Query("SELECT c FROM HourlyGroupClass c WHERE c.isActive = true ORDER BY c.createdAt DESC")
    List<HourlyGroupClass> findAllActive();

    @Query("SELECT c FROM HourlyGroupClass c WHERE c.isActive = true " +
            "ORDER BY c.createdAt DESC")
    List<HourlyGroupClass> findByTutorIdAndActive(@Param("tutorId") Long tutorId);

    @Query("SELECT c FROM HourlyGroupClass c WHERE c.subject.id = :subjectId AND c.isActive = true " +
            "ORDER BY c.createdAt DESC")
    List<HourlyGroupClass> findBySubjectIdAndActive(@Param("subjectId") Long subjectId);

    @Query("SELECT c FROM HourlyGroupClass c WHERE c.subtype.id = :subtypeId AND c.isActive = true " +
            "ORDER BY c.createdAt DESC")
    List<HourlyGroupClass> findBySubtypeIdAndActive(@Param("subtypeId") Long subtypeId);

    @Query("SELECT DISTINCT c FROM HourlyGroupClass c " +
            "LEFT JOIN FETCH c.enrollments " +
            "WHERE c.id = :id")
    Optional<HourlyGroupClass> findByIdWithEnrollments(@Param("id") Long id);

    @Query("SELECT c FROM HourlyGroupClass c WHERE c.isActive = true " +
            "AND LOWER(c.className) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "ORDER BY c.createdAt DESC")
    List<HourlyGroupClass> searchActiveClasses(@Param("search") String search);

    @Query("SELECT c FROM HourlyGroupClass c WHERE c.isActive = true " +
            "AND (:subtypeId IS NULL OR c.subtype.id = :subtypeId) " +
            "ORDER BY c.createdAt DESC")
    List<HourlyGroupClass> findByMultipleCriteria(
            @Param("tutorId") Long tutorId,
            @Param("subjectId") Long subjectId,
            @Param("subtypeId") Long subtypeId
    );

    long countByIsActiveTrue();

    @Query("SELECT COUNT(c) FROM HourlyGroupClass c WHERE c.isActive = true")
    long countByTutorIdAndActive(@Param("tutorId") Long tutorId);

    @Query("SELECT DISTINCT c FROM HourlyGroupClass c " +
            "JOIN c.enrollments e " +
            "WHERE e.student.id = :studentId AND c.isActive = true " +
            "ORDER BY c.createdAt DESC")
    List<HourlyGroupClass> findByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT c FROM HourlyGroupClass c " +
            "JOIN HourlyGroupEnrollment e ON e.id = c.id " +
            "WHERE e.student = :studentId " +
            "ORDER BY c.createdAt DESC")
    List<HourlyGroupClass> findClassesByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT h FROM HourlyGroupClass h " +
            "WHERE h.grade.id = :gradeId " +
            "AND h.subtype.id = :subtypeId " +
            "AND h.isActive = true")
    List<HourlyGroupClass> findByGradeIdAndSubtypeIdAndIsActiveTrue(
            @Param("gradeId") Long gradeId,
            @Param("subtypeId") Long subtypeId
    );

    List<HourlyGroupClass> findByIsActiveTrue();
}