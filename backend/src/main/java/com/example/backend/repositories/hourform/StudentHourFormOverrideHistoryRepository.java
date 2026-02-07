package com.example.backend.repositories.hourform;

import com.example.backend.entities.hourform.StudentHourFormOverrideHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentHourFormOverrideHistoryRepository extends JpaRepository<StudentHourFormOverrideHistory, Long> {
    List<StudentHourFormOverrideHistory> findByHourFormIdAndStudentIdOrderByUpdatedAtDesc(Long hourFormId, Long studentId);
}