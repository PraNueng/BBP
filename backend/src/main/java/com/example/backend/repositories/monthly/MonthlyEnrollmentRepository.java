package com.example.backend.repositories.monthly;

import com.example.backend.entities.monthly.MonthlyEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MonthlyEnrollmentRepository extends JpaRepository<MonthlyEnrollment, Long> {

    // ค้นหา enrollments ของคลาส
    @Query("SELECT e FROM MonthlyEnrollment e WHERE e.monthlyClass.id = :classId " +
            "ORDER BY e.student.firstName ASC")
    List<MonthlyEnrollment> findByClassId(@Param("classId") Long classId);

    // ค้นหา enrollments ของนักเรียน
    @Query("SELECT e FROM MonthlyEnrollment e WHERE e.student.id = :studentId " +
            "ORDER BY e.enrolledAt DESC")
    List<MonthlyEnrollment> findByStudentId(@Param("studentId") Long studentId);

    // ค้นหา enrollment เฉพาะ
    @Query("SELECT e FROM MonthlyEnrollment e WHERE e.monthlyClass.id = :classId " +
            "AND e.student.id = :studentId")
    Optional<MonthlyEnrollment> findByClassIdAndStudentId(
            @Param("classId") Long classId,
            @Param("studentId") Long studentId
    );

    // ตรวจสอบว่านักเรียนลงทะเบียนในคลาสแล้วหรือไม่
    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM MonthlyEnrollment e " +
            "WHERE e.monthlyClass.id = :classId AND e.student.id = :studentId")
    boolean existsByClassIdAndStudentId(
            @Param("classId") Long classId,
            @Param("studentId") Long studentId
    );

    // นับจำนวนนักเรียนในคลาส
    @Query("SELECT COUNT(e) FROM MonthlyEnrollment e WHERE e.monthlyClass.id = :classId")
    long countByClassId(@Param("classId") Long classId);

    // นับจำนวนคลาสของนักเรียน
    @Query("SELECT COUNT(e) FROM MonthlyEnrollment e WHERE e.student.id = :studentId")
    long countByStudentId(@Param("studentId") Long studentId);

    // ค้นหานักเรียนในคลาสที่ active
    @Query("SELECT e FROM MonthlyEnrollment e " +
            "JOIN e.monthlyClass c " +
            "WHERE c.isActive = true AND e.student.id = :studentId " +
            "ORDER BY c.startDate DESC")
    List<MonthlyEnrollment> findActiveEnrollmentsByStudentId(@Param("studentId") Long studentId);

    // ค้นหาคลาสที่มีนักเรียนมากที่สุด
    @Query("SELECT e.monthlyClass.id, COUNT(e.student.id) as studentCount " +
            "FROM MonthlyEnrollment e " +
            "GROUP BY e.monthlyClass.id " +
            "ORDER BY studentCount DESC")
    List<Object[]> findClassesWithMostStudents();

    // ค้นหานักเรียนที่เรียนหลายคลาส
    @Query("SELECT e.student.id, COUNT(e.monthlyClass.id) as classCount " +
            "FROM MonthlyEnrollment e " +
            "GROUP BY e.student.id " +
            "HAVING COUNT(e.monthlyClass.id) > 1 " +
            "ORDER BY classCount DESC")
    List<Object[]> findStudentsWithMultipleClasses();

    // ลบ enrollment
    @Query("DELETE FROM MonthlyEnrollment e WHERE e.monthlyClass.id = :classId " +
            "AND e.student.id = :studentId")
    void deleteByClassIdAndStudentId(
            @Param("classId") Long classId,
            @Param("studentId") Long studentId
    );

    // ลบ enrollments ทั้งหมดของคลาส
    @Query("DELETE FROM MonthlyEnrollment e WHERE e.monthlyClass.id = :classId")
    void deleteAllByClassId(@Param("classId") Long classId);

    @Query("SELECT e FROM MonthlyEnrollment e " +
            "JOIN FETCH e.monthlyClass c " +
            "WHERE e.student.id = :studentId " +
            "ORDER BY e.enrolledAt DESC")
    List<MonthlyEnrollment> findByStudentIdWithClasses(@Param("studentId") Long studentId);

    /**
     * ดึง enrollment ที่ active พร้อมข้อมูล student, class, subject
     */
    @Query("SELECT e FROM MonthlyEnrollment e " +
            "JOIN FETCH e.student s " +
            "JOIN FETCH e.monthlyClass c " +
            "JOIN FETCH c.subject " +
            "WHERE e.isActive = true " +
            "AND c.isActive = true")
    List<MonthlyEnrollment> findAllActiveEnrollmentsWithDetails();
}