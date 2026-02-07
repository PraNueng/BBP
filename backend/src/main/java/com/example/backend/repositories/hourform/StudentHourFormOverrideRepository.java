package com.example.backend.repositories.hourform;

import com.example.backend.entities.hourform.StudentHourFormOverride;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface StudentHourFormOverrideRepository extends JpaRepository<StudentHourFormOverride, Long> {
    Optional<StudentHourFormOverride> findByHourFormIdAndStudentId(Long hourFormId, Long studentId);
    List<StudentHourFormOverride> findByHourFormId(Long hourFormId);
}