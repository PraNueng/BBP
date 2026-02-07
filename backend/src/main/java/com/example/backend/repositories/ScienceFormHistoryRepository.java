package com.example.backend.repositories;

import com.example.backend.entities.ScienceFormHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScienceFormHistoryRepository extends JpaRepository<ScienceFormHistory, Long> {

    @Query("SELECT h FROM ScienceFormHistory h WHERE h.scienceForm.id = :scienceFormId " +
            "ORDER BY h.updatedAt DESC")
    List<ScienceFormHistory> findByScienceFormIdOrderByUpdatedAtDesc(@Param("scienceFormId") Long scienceFormId);
}