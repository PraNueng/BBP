package com.example.backend.repositories;

import com.example.backend.entities.EnrollmentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnrollmentHistoryRepository extends JpaRepository<EnrollmentHistory, Long> {

    List<EnrollmentHistory> findByEnrollmentTypeAndEnrollmentIdOrderByUpdatedAtDesc(
            String enrollmentType,
            Long enrollmentId
    );
}