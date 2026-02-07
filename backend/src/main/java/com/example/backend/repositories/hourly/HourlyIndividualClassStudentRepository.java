package com.example.backend.repositories.hourly;

import com.example.backend.entities.hourly.HourlyIndividualClass;
import com.example.backend.entities.hourly.HourlyIndividualClassStudent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HourlyIndividualClassStudentRepository extends JpaRepository<HourlyIndividualClassStudent, Long> {

    // ดึงนักเรียนทั้งหมดในคลาส
    @Query("SELECT cs FROM HourlyIndividualClassStudent cs WHERE cs.hourlyIndividualClass.id = :classId AND cs.isActive = true")
    List<HourlyIndividualClassStudent> findByClassIdAndActive(@Param("classId") Long classId);

    // เช็คว่านักเรียนอยู่ในคลาสหรือยัง
    @Query("SELECT COUNT(cs) > 0 FROM HourlyIndividualClassStudent cs " +
            "WHERE cs.hourlyIndividualClass.id = :classId AND cs.student.id = :studentId AND cs.isActive = true")
    boolean existsByClassIdAndStudentId(@Param("classId") Long classId, @Param("studentId") Long studentId);

    // ลบนักเรียนออกจากคลาส
    @Query("DELETE FROM HourlyIndividualClassStudent cs " +
            "WHERE cs.hourlyIndividualClass.id = :classId AND cs.student.id = :studentId")
    void deleteByClassIdAndStudentId(@Param("classId") Long classId, @Param("studentId") Long studentId);

    // นับจำนวนนักเรียนในคลาส
    @Query("SELECT COUNT(cs) FROM HourlyIndividualClassStudent cs " +
            "WHERE cs.hourlyIndividualClass.id = :classId AND cs.isActive = true")
    long countByClassId(@Param("classId") Long classId);

    // ค้นหาคลาสตามนักเรียน (ใช้ junction table)
    @Query("SELECT DISTINCT c FROM HourlyIndividualClass c " +
            "JOIN c.classStudents cs " +
            "WHERE cs.student.id = :studentId AND c.isActive = true " +
            "ORDER BY c.createdAt DESC")
    List<HourlyIndividualClass> findByStudentIdViaJunction(@Param("studentId") Long studentId);

    @Query("SELECT DISTINCT hic FROM HourlyIndividualClass hic " +
            "JOIN hic.classStudents cs " +
            "WHERE cs.student.id = :studentId AND cs.isActive = true")
    List<HourlyIndividualClass> findByStudentIdAndIsActiveViaJunction(@Param("studentId") Long studentId);

    // นับจำนวนนักเรียนทั้งหมด (รวม inactive)
    @Query("SELECT COUNT(cs) FROM HourlyIndividualClassStudent cs " +
            "WHERE cs.hourlyIndividualClass.id = :classId")
    long countAllByClassId(@Param("classId") Long classId);

    @Query("""
        SELECT cs
        FROM HourlyIndividualClassStudent cs
        WHERE cs.student.id = :studentId
          AND cs.isActive = true
    """)
    List<HourlyIndividualClassStudent> findByStudentIdAndActive(
            @Param("studentId") Long studentId
    );

    @Query("""
    SELECT cs
    FROM HourlyIndividualClassStudent cs
    WHERE cs.hourlyIndividualClass.id = :classId
""")
    List<HourlyIndividualClassStudent> findByClassId(
            @Param("classId") Long classId
    );
}