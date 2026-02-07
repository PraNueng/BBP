package com.example.backend.repositories.monthly;

import com.example.backend.entities.monthly.MonthlySubtype;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MonthlySubtypeRepository extends JpaRepository<MonthlySubtype, Long> {

    /**
     * ดึงประเภทคลาสทั้งหมดที่ active เรียงตาม displayOrder
     */
    List<MonthlySubtype> findByIsActiveTrueOrderByDisplayOrderAsc();

    /**
     * ดึงประเภทคลาสทั้งหมด เรียงตาม displayOrder
     */
    List<MonthlySubtype> findAllByOrderByDisplayOrderAsc();

    // ค้นหาด้วยชื่อ subtype
    Optional<MonthlySubtype> findBySubtypeName(String subtypeName);

    // ค้นหา subtypes ที่ active เรียงตาม displayOrder
    @Query("SELECT m FROM MonthlySubtype m WHERE m.isActive = true ORDER BY m.displayOrder ASC")
    List<MonthlySubtype> findAllActive();

    // ค้นหาทั้งหมดเรียงตาม displayOrder
    @Query("SELECT m FROM MonthlySubtype m ORDER BY m.displayOrder ASC")
    List<MonthlySubtype> findAllOrdered();

    // นับจำนวน subtypes ที่ active
    long countByIsActiveTrue();

    // ตรวจสอบว่ามีชื่อนี้แล้วหรือไม่
    boolean existsBySubtypeName(String subtypeName);

    // ตรวจสอบว่ามีชื่อนี้แล้วหรือไม่ (ยกเว้น id ที่ระบุ)
    @Query("SELECT CASE WHEN COUNT(m) > 0 THEN true ELSE false END FROM MonthlySubtype m " +
            "WHERE m.subtypeName = :subtypeName AND m.id != :subtypeId")
    boolean existsBySubtypeNameAndIdNot(@Param("subtypeName") String subtypeName,
                                        @Param("subtypeId") Long subtypeId);

    // ค้นหา display order ที่ใหญ่ที่สุด
    @Query("SELECT COALESCE(MAX(m.displayOrder), 0) FROM MonthlySubtype m")
    Integer findMaxDisplayOrder();

    /**
     * ตรวจสอบว่าประเภทคลาสนี้ถูกใช้ในคลาสหรือไม่
     */
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END " +
            "FROM MonthlyClass c WHERE c.subtype.id = :subtypeId AND c.isActive = true")
    boolean isSubtypeInUse(Long subtypeId);
}