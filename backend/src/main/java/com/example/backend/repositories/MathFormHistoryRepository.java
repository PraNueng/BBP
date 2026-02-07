package com.example.backend.repositories;

import com.example.backend.entities.MathFormHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MathFormHistoryRepository extends JpaRepository<MathFormHistory, Long> {

    @Query("SELECT h FROM MathFormHistory h WHERE h.mathForm.id = :mathFormId " +
            "ORDER BY h.updatedAt DESC")
    List<MathFormHistory> findByMathFormIdOrderByUpdatedAtDesc(@Param("mathFormId") Long mathFormId);
}