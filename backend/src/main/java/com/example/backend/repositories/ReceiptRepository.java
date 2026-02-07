package com.example.backend.repositories;

import com.example.backend.entities.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReceiptRepository extends JpaRepository<Receipt, Long> {

    // ค้นหาใบเสร็จของคลาส
    @Query("SELECT r FROM Receipt r WHERE r.classType = :classType AND r.classId = :classId " +
            "ORDER BY r.paymentDate DESC")
    List<Receipt> findByClassTypeAndClassId(
            @Param("classType") String classType,
            @Param("classId") Long classId
    );

    // ค้นหาใบเสร็จตามสถานะ
    @Query("SELECT r FROM Receipt r WHERE r.status = :status " +
            "ORDER BY r.paymentDate DESC")
    List<Receipt> findByStatus(@Param("status") String status);

    // ค้นหาใบเสร็จที่รอดำเนินการ
    @Query("SELECT r FROM Receipt r WHERE r.status = 'pending' " +
            "ORDER BY r.paymentDate ASC")
    List<Receipt> findPendingReceipts();

    // ค้นหาใบเสร็จในช่วงวันที่
    @Query("SELECT r FROM Receipt r WHERE r.paymentDate BETWEEN :startDate AND :endDate " +
            "ORDER BY r.paymentDate DESC")
    List<Receipt> findByPaymentDateRange(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    // ค้นหาใบเสร็จตามหลายเงื่อนไข
    @Query("SELECT r FROM Receipt r WHERE 1=1 " +
            "AND (:classType IS NULL OR r.classType = :classType) " +
            "AND (:status IS NULL OR r.status = :status) " +
            "AND (:startDate IS NULL OR r.paymentDate >= :startDate) " +
            "AND (:endDate IS NULL OR r.paymentDate <= :endDate) " +
            "ORDER BY r.paymentDate DESC")
    List<Receipt> findByMultipleCriteria(
            @Param("classType") String classType,
            @Param("status") String status,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    // คำนวณยอดรวมของคลาส
    @Query("SELECT COALESCE(SUM(r.amount), 0) FROM Receipt r " +
            "WHERE r.classType = :classType AND r.classId = :classId AND r.status = 'paid'")
    BigDecimal sumAmountByClassTypeAndClassId(
            @Param("classType") String classType,
            @Param("classId") Long classId
    );

    // คำนวณยอดรวมตามช่วงวันที่
    @Query("SELECT COALESCE(SUM(r.amount), 0) FROM Receipt r " +
            "WHERE r.paymentDate BETWEEN :startDate AND :endDate AND r.status = 'paid'")
    BigDecimal sumAmountByDateRange(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    // คำนวณยอดรวมตามประเภทคลาส
    @Query("SELECT COALESCE(SUM(r.amount), 0) FROM Receipt r " +
            "WHERE r.classType = :classType AND r.status = 'paid'")
    BigDecimal sumAmountByClassType(@Param("classType") String classType);

    // สถิติรายรับรายเดือน
    @Query("SELECT YEAR(r.paymentDate) as year, MONTH(r.paymentDate) as month, " +
            "r.classType, COUNT(r.id) as totalReceipts, SUM(r.amount) as totalAmount " +
            "FROM Receipt r " +
            "WHERE r.status = 'paid' " +
            "GROUP BY YEAR(r.paymentDate), MONTH(r.paymentDate), r.classType " +
            "ORDER BY year DESC, month DESC")
    List<Object[]> getMonthlyRevenue();

    // สถิติรายรับตามประเภทคลาส
    @Query("SELECT r.classType, r.status, COUNT(r.id) as totalReceipts, " +
            "SUM(r.amount) as totalAmount, AVG(r.amount) as avgAmount " +
            "FROM Receipt r " +
            "GROUP BY r.classType, r.status " +
            "ORDER BY r.classType, r.status")
    List<Object[]> getRevenueByClassType();

    // นับจำนวนใบเสร็จตามสถานะ
    @Query("SELECT COUNT(r) FROM Receipt r WHERE r.status = :status")
    long countByStatus(@Param("status") String status);

    // นับจำนวนใบเสร็จของคลาส
    @Query("SELECT COUNT(r) FROM Receipt r WHERE r.classType = :classType AND r.classId = :classId")
    long countByClassTypeAndClassId(
            @Param("classType") String classType,
            @Param("classId") Long classId
    );

    // ค้นหาใบเสร็จที่สร้างโดย user
    @Query("SELECT r FROM Receipt r WHERE r.createdBy.id = :userId " +
            "ORDER BY r.createdAt DESC")
    List<Receipt> findByCreatedBy(@Param("userId") Long userId);
}