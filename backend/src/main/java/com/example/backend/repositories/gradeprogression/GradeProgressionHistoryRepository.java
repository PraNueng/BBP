package com.example.backend.repositories.gradeprogression;

import com.example.backend.entities.gradeprogression.GradeProgressionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface GradeProgressionHistoryRepository extends JpaRepository<GradeProgressionHistory, Long> {

    // ค้นหาประวัติการขึ้นชั้นของนักเรียน
    @Query("SELECT h FROM GradeProgressionHistory h WHERE h.student.id = :studentId " +
            "ORDER BY h.progressionDate DESC")
    List<GradeProgressionHistory> findByStudentId(@Param("studentId") Long studentId);

    // ค้นหาประวัติการขึ้นชั้นล่าสุดของนักเรียน
    @Query("SELECT h FROM GradeProgressionHistory h WHERE h.student.id = :studentId " +
            "ORDER BY h.progressionDate DESC")
    List<GradeProgressionHistory> findLatestByStudentId(@Param("studentId") Long studentId);

    // ค้นหาประวัติการขึ้นชั้นตามปีการศึกษา
    @Query("SELECT h FROM GradeProgressionHistory h WHERE h.academicYear = :academicYear " +
            "ORDER BY h.progressionDate DESC")
    List<GradeProgressionHistory> findByAcademicYear(@Param("academicYear") String academicYear);

    // ค้นหาประวัติการขึ้นชั้นตามประเภท
    @Query("SELECT h FROM GradeProgressionHistory h WHERE h.progressionType = :progressionType " +
            "ORDER BY h.progressionDate DESC")
    List<GradeProgressionHistory> findByProgressionType(@Param("progressionType") String progressionType);

    // ค้นหาประวัติการขึ้นชั้นในช่วงวันที่
    @Query("SELECT h FROM GradeProgressionHistory h " +
            "WHERE h.progressionDate BETWEEN :startDate AND :endDate " +
            "ORDER BY h.progressionDate DESC")
    List<GradeProgressionHistory> findByDateRange(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    // ค้นหาประวัติการขึ้นชั้นอัตโนมัติ
    @Query("SELECT h FROM GradeProgressionHistory h WHERE h.progressionType = 'auto' " +
            "ORDER BY h.progressionDate DESC")
    List<GradeProgressionHistory> findAutoProgressions();

    // ค้นหาประวัติการขึ้นชั้นด้วยตนเอง
    @Query("SELECT h FROM GradeProgressionHistory h WHERE h.progressionType = 'manual' " +
            "ORDER BY h.progressionDate DESC")
    List<GradeProgressionHistory> findManualProgressions();

    // ค้นหาประวัติการขึ้นชั้นจากระดับชั้นเดิม
    @Query("SELECT h FROM GradeProgressionHistory h WHERE h.oldGrade.id = :gradeId " +
            "ORDER BY h.progressionDate DESC")
    List<GradeProgressionHistory> findByOldGradeId(@Param("gradeId") Long gradeId);

    // ค้นหาประวัติการขึ้นชั้นไประดับชั้นใหม่
    @Query("SELECT h FROM GradeProgressionHistory h WHERE h.newGrade.id = :gradeId " +
            "ORDER BY h.progressionDate DESC")
    List<GradeProgressionHistory> findByNewGradeId(@Param("gradeId") Long gradeId);

    // สถิติการขึ้นชั้นตามปีการศึกษา
    @Query("SELECT h.academicYear, h.progressionType, " +
            "COUNT(h.id) as totalProgressions " +
            "FROM GradeProgressionHistory h " +
            "GROUP BY h.academicYear, h.progressionType " +
            "ORDER BY h.academicYear DESC")
    List<Object[]> getProgressionStatsByAcademicYear();

    // สถิติการขึ้นชั้นระหว่างระดับชั้น
    @Query("SELECT h.oldGrade.gradeName, h.newGrade.gradeName, " +
            "COUNT(h.id) as totalProgressions " +
            "FROM GradeProgressionHistory h " +
            "GROUP BY h.oldGrade.gradeName, h.newGrade.gradeName " +
            "ORDER BY h.oldGrade.gradeOrder")
    List<Object[]> getProgressionStatsByGrades();

    // นับจำนวนการขึ้นชั้นของนักเรียน
    @Query("SELECT COUNT(h) FROM GradeProgressionHistory h WHERE h.student.id = :studentId")
    long countByStudentId(@Param("studentId") Long studentId);

    // นับจำนวนการขึ้นชั้นตามปีการศึกษา
    @Query("SELECT COUNT(h) FROM GradeProgressionHistory h WHERE h.academicYear = :academicYear")
    long countByAcademicYear(@Param("academicYear") String academicYear);

    // ตรวจสอบว่านักเรียนเคยขึ้นชั้นในปีการศึกษานี้แล้วหรือไม่
    @Query("SELECT CASE WHEN COUNT(h) > 0 THEN true ELSE false END FROM GradeProgressionHistory h " +
            "WHERE h.student.id = :studentId AND h.academicYear = :academicYear")
    boolean existsByStudentIdAndAcademicYear(
            @Param("studentId") Long studentId,
            @Param("academicYear") String academicYear
    );
}