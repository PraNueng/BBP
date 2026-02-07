package com.example.backend.repositories.hourly;

import com.example.backend.entities.hourly.HourlyGroupSubtype;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HourlyGroupSubtypeRepository extends JpaRepository<HourlyGroupSubtype, Long> {

    /**
     * ดึงประเภทกลุ่มทั้งหมดที่ active เรียงตาม displayOrder
     */
    List<HourlyGroupSubtype> findByIsActiveTrueOrderByDisplayOrderAsc();

    /**
     * ดึงประเภทกลุ่มทั้งหมด เรียงตาม displayOrder
     */
    List<HourlyGroupSubtype> findAllByOrderByDisplayOrderAsc();

    // ค้นหาด้วยชื่อ subtype
    Optional<HourlyGroupSubtype> findBySubtypeName(String subtypeName);

    // ค้นหา subtypes ที่ active เรียงตาม displayOrder
    @Query("SELECT h FROM HourlyGroupSubtype h WHERE h.isActive = true ORDER BY h.displayOrder ASC")
    List<HourlyGroupSubtype> findAllActive();

    // ค้นหาทั้งหมดเรียงตาม displayOrder
    @Query("SELECT h FROM HourlyGroupSubtype h ORDER BY h.displayOrder ASC")
    List<HourlyGroupSubtype> findAllOrdered();

    // นับจำนวน subtypes ที่ active
    long countByIsActiveTrue();

    // ตรวจสอบว่ามีชื่อนี้แล้วหรือไม่
    boolean existsBySubtypeName(String subtypeName);

    // ตรวจสอบว่ามีชื่อนี้แล้วหรือไม่ (ยกเว้น id ที่ระบุ)
    @Query("SELECT CASE WHEN COUNT(h) > 0 THEN true ELSE false END FROM HourlyGroupSubtype h " +
            "WHERE h.subtypeName = :subtypeName AND h.id != :subtypeId")
    boolean existsBySubtypeNameAndIdNot(@Param("subtypeName") String subtypeName,
                                        @Param("subtypeId") Long subtypeId);

    // ค้นหา display order ที่ใหญ่ที่สุด
    @Query("SELECT COALESCE(MAX(h.displayOrder), 0) FROM HourlyGroupSubtype h")
    Integer findMaxDisplayOrder();

    /**
     * ตรวจสอบว่าประเภทคลาสนี้ถูกใช้ในคลาสหรือไม่
     */
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END " +
            "FROM HourlyGroupClass c WHERE c.subtype.id = :subtypeId AND c.isActive = true")
    boolean isSubtypeInUse(Long subtypeId);
}