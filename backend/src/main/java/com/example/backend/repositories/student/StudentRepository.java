package com.example.backend.repositories.student;

import com.example.backend.entities.student.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    // ค้นหาด้วย student code
    Optional<Student> findByStudentCode(String studentCode);

    Optional<Student> findByFirstNameAndLastName(String firstName, String lastName);

    // ค้นหานักเรียนที่ active
    List<Student> findByIsActiveTrueOrderByFirstNameAsc();

    // ค้นหานักเรียนตามชั้นเรียน
    @Query("SELECT s FROM Student s WHERE s.grade.id = :gradeId AND s.isActive = true ORDER BY s.firstName ASC")
    List<Student> findByGradeIdAndActiveTrue(@Param("gradeId") Long gradeId);

    // ค้นหานักเรียนตามโรงเรียน
    @Query("SELECT s FROM Student s WHERE s.schoolName = :schoolName AND s.isActive = true ORDER BY s.firstName ASC")
    List<Student> findBySchoolNameAndActiveTrue(@Param("schoolName") String schoolName);

    // ค้นหานักเรียนแบบ partial match (ชื่อ, นามสกุล, ชื่อเล่น)
    @Query("SELECT s FROM Student s WHERE s.isActive = true AND (" +
            "LOWER(s.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(s.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(s.nickname) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(s.studentCode) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "ORDER BY s.firstName ASC")
    List<Student> searchActiveStudents(@Param("search") String search);

    // ค้นหานักเรียนตามหลายเงื่อนไข
    @Query("SELECT s FROM Student s WHERE s.isActive = true " +
            "AND (:gradeId IS NULL OR s.grade.id = :gradeId) " +
            "AND (:schoolName IS NULL OR s.schoolName = :schoolName) " +
            "ORDER BY s.firstName ASC")
    List<Student> findByMultipleCriteria(
            @Param("gradeId") Long gradeId,
            @Param("schoolName") String schoolName
    );

    // นับจำนวนนักเรียนที่ active
    long countByIsActiveTrue();

    // นับจำนวนนักเรียนตามชั้นเรียน
    @Query("SELECT COUNT(s) FROM Student s WHERE s.grade.id = :gradeId AND s.isActive = true")
    long countByGradeIdAndActiveTrue(@Param("gradeId") Long gradeId);

    // ตรวจสอบว่ามี student code นี้แล้วหรือไม่
    boolean existsByStudentCode(String studentCode);

    // ค้นหานักเรียนที่สร้างโดย user คนใด
    @Query("SELECT s FROM Student s WHERE s.createdBy.id = :userId ORDER BY s.createdAt DESC")
    List<Student> findByCreatedBy(@Param("userId") Long userId);

    @Query("""
       SELECT s FROM Student s
       WHERE s.isActive = TRUE 
         AND s.grade.id = :gradeId
         AND (LOWER(s.firstName) LIKE LOWER(CONCAT('%', :search, '%'))
          OR LOWER(s.lastName) LIKE LOWER(CONCAT('%', :search, '%'))
          OR LOWER(s.nickname) LIKE LOWER(CONCAT('%', :search, '%')))
       ORDER BY s.firstName ASC
       """)
    List<Student> findByGradeIdAndSearch(Long gradeId, String search);

    /**
     * หานักเรียนจากชื่อ นามสกุล และ gradeId
     */
    Optional<Student> findByFirstNameAndLastNameAndGradeId(String firstName, String lastName, Long gradeId);

    // สำหรับ StudentCodeGeneratorService
    long countByStudentCodeStartingWith(String prefix);

    // สำหรับ StudentValidationService
    boolean existsByFirstNameAndLastNameAndGradeId(String firstName, String lastName, Long gradeId);
    boolean existsByFirstNameAndLastNameAndGradeIdAndIdNot(String firstName, String lastName, Long gradeId, Long excludeId);

    @Query("SELECT s FROM Student s " +
            "WHERE s.isActive = true AND s.grade.id = :gradeId " +
            "AND (LOWER(s.firstName) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "     OR LOWER(s.lastName) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "     OR LOWER(s.nickname) LIKE LOWER(CONCAT('%', :search, '%')) )")
    List<Student> searchActiveStudentsByGrade(@Param("gradeId") Long gradeId,
                                              @Param("search") String search);
}