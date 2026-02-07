package com.example.backend.repositories;

import com.example.backend.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TutorRepository extends JpaRepository<User, Long> {

    // Find all tutors (users with role 'TUTOR')
    List<User> findByRoleOrderByCreatedAtDesc(String role);

    // Find active tutors
    @Query("SELECT u FROM User u WHERE u.role = 'TUTOR' AND u.isActive = :isActive ORDER BY u.createdAt DESC")
    List<User> findActiveTeachers(@Param("isActive") Boolean isActive);

    // Find tutor by username
    Optional<User> findByUsernameAndRole(String username, String role);

    // Check if username exists for tutors
    boolean existsByUsernameAndRole(String username, String role);

    // Search tutors by name or nickname
    @Query("SELECT u FROM User u WHERE u.role = 'TUTOR' " +
            "AND (LOWER(u.username) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "OR LOWER(u.nickname) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
            "ORDER BY u.createdAt DESC")
    List<User> searchTutors(@Param("searchTerm") String searchTerm);
}