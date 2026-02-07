package com.example.backend.repositories;

import com.example.backend.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // ค้นหาด้วย username (สำหรับ login)
    Optional<User> findByUsername(String username);

    // ค้นหาด้วย username และ active status
    Optional<User> findByUsernameAndIsActiveTrue(String username);

    // ค้นหา users ที่ active
    List<User> findByIsActiveTrueOrderByNicknameAsc();

    // ค้นหา users ตาม role
    @Query("SELECT u FROM User u WHERE u.role = :role AND u.isActive = true ORDER BY u.nickname ASC")
    List<User> findByRoleAndIsActiveTrue(@Param("role") String role);

    // ค้นหา tutors ทั้งหมด (role = 'tutor' หรือ 'tutor_head')
    @Query("SELECT u FROM User u WHERE u.role IN ('tutor', 'tutor_head') AND u.isActive = true ORDER BY u.nickname ASC")
    List<User> findAllTutors();

    // ค้นหา admins
    @Query("SELECT u FROM User u WHERE u.role = 'admin' AND u.isActive = true ORDER BY u.nickname ASC")
    List<User> findAllAdmins();

    // ค้นหา users แบบ partial match (username, nickname)
    @Query("SELECT u FROM User u WHERE u.isActive = true AND (" +
            "LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(u.nickname) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "ORDER BY u.nickname ASC")
    List<User> searchActiveUsers(@Param("search") String search);

    // นับจำนวน users ที่ active
    long countByIsActiveTrue();

    // นับจำนวน users ตาม role
    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role AND u.isActive = true")
    long countByRoleAndIsActiveTrue(@Param("role") String role);

    // ตรวจสอบว่ามี username นี้แล้วหรือไม่
    boolean existsByUsername(String username);

    // ตรวจสอบว่ามี username นี้แล้วหรือไม่ (ยกเว้น user id ที่ระบุ - สำหรับ update)
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u " +
            "WHERE u.username = :username AND u.id != :userId")
    boolean existsByUsernameAndIdNot(@Param("username") String username, @Param("userId") Long userId);

    // ค้นหา users พร้อมจำนวนนักเรียนที่สร้าง
    @Query("SELECT u, COUNT(s.id) as studentCount FROM User u " +
            "LEFT JOIN Student s ON u.id = s.createdBy.id " +
            "WHERE u.isActive = true " +
            "GROUP BY u.id " +
            "ORDER BY u.nickname ASC")
    List<Object[]> findUsersWithStudentCount();

    // ค้นหา tutors พร้อมจำนวนชั่วโมงสอน
    @Query("SELECT u FROM User u WHERE u.role IN ('tutor', 'tutor_head') AND u.isActive = true ORDER BY u.nickname ASC")
    List<Object[]> findTutorsWithTotalHours();

    // ค้นหา user ตาม role และ search
    @Query("SELECT u FROM User u WHERE u.isActive = true " +
            "AND (:role IS NULL OR u.role = :role) " +
            "AND (:search IS NULL OR LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(u.nickname) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "ORDER BY u.nickname ASC")
    List<User> findByRoleAndSearch(
            @Param("role") String role,
            @Param("search") String search
    );

    // Search methods
    List<User> findByIsActiveTrueAndUsernameContainingIgnoreCaseOrNicknameContainingIgnoreCase(
            String username, String nickname);

    List<User> findByRoleAndIsActiveTrueAndUsernameOrNicknameContaining(
            String role, String username, String nickname);
}