package com.example.backend.repositories;

import com.example.backend.entities.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {

    // ค้นหาด้วยชื่อระดับชั้น
    Optional<Grade> findByGradeName(String gradeName);

    // ค้นหาทั้งหมดเรียงตาม gradeOrder
    @Query("SELECT g FROM Grade g ORDER BY g.gradeOrder ASC")
    List<Grade> findAllOrdered();

    // ค้นหาระดับชั้นถัดไป
    @Query("SELECT g FROM Grade g WHERE g.id = :gradeId")
    Optional<Grade> findNextGrade(@Param("gradeId") Long gradeId);

    // ค้นหาระดับชั้นที่มี nextGrade
    @Query("SELECT g FROM Grade g WHERE g.nextGrade IS NOT NULL ORDER BY g.gradeOrder ASC")
    List<Grade> findGradesWithNextGrade();

    // นับจำนวนนักเรียนในแต่ละระดับชั้น
    @Query("SELECT g.id, g.gradeName, COUNT(s.id) " +
            "FROM Grade g LEFT JOIN Student s ON g.id = s.grade.id AND s.isActive = true " +
            "GROUP BY g.id, g.gradeName ORDER BY g.gradeOrder ASC")
    List<Object[]> countStudentsByGrade();

    // ตรวจสอบว่ามีชื่อระดับชั้นนี้แล้วหรือไม่
    boolean existsByGradeName(String gradeName);

    // ค้นหา gradeOrder ที่ใหญ่ที่สุด
    @Query("SELECT COALESCE(MAX(g.gradeOrder), 0) FROM Grade g")
    Integer findMaxGradeOrder();
}