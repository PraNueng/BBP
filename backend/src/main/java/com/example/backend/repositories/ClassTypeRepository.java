package com.example.backend.repositories;

import com.example.backend.entities.ClassType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassTypeRepository extends JpaRepository<ClassType, Long> {

    // ค้นหาตามชื่อประเภทคอร์ส
    Optional<ClassType> findByTypeName(String typeName);

    // ค้นหาประเภทคอร์สที่ active
    List<ClassType> findByIsActiveTrueOrderByTypeNameAsc();

    // ค้นหาประเภทคอร์สทั้งหมด
    List<ClassType> findAllByOrderByTypeNameAsc();

    // ค้นหาประเภทคอร์สตามชื่อแบบ partial match (active เท่านั้น)
    @Query("SELECT ct FROM ClassType ct WHERE " +
            "LOWER(ct.typeName) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "AND ct.isActive = true " +
            "ORDER BY ct.typeName ASC")
    List<ClassType> searchActiveClassTypes(@Param("search") String search);

    // นับจำนวนประเภทคอร์สที่ active
    long countByIsActiveTrue();

    // ตรวจสอบว่ามีประเภทคอร์สนี้แล้วหรือไม่
    boolean existsByTypeName(String typeName);
}