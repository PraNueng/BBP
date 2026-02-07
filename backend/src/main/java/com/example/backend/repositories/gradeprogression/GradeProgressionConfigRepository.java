package com.example.backend.repositories.gradeprogression;

import com.example.backend.entities.gradeprogression.GradeProgressionConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GradeProgressionConfigRepository extends JpaRepository<GradeProgressionConfig, Long> {

    // ค้นหาการตั้งค่าที่ active
    @Query("SELECT c FROM GradeProgressionConfig c WHERE c.isActive = true")
    Optional<GradeProgressionConfig> findActiveConfig();

    // ค้นหาการตั้งค่าล่าสุด
    @Query("SELECT c FROM GradeProgressionConfig c ORDER BY c.updatedAt DESC")
    Optional<GradeProgressionConfig> findLatestConfig();

    // ตรวจสอบว่ามีการตั้งค่าที่ active หรือไม่
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM GradeProgressionConfig c " +
            "WHERE c.isActive = true")
    boolean hasActiveConfig();

    // นับจำนวนการตั้งค่าทั้งหมด
    long count();
}