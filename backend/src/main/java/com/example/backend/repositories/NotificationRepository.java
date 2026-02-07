package com.example.backend.repositories;

import com.example.backend.entities.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * ดึงการแจ้งเตือนทั้งหมด เรียงตาม unread ก่อน (false มาก่อน true)
     * แล้วตามวันที่สร้างล่าสุด
     */
    List<Notification> findAllByOrderByIsReadAscCreatedAtDesc();

    /**
     * ดึงการแจ้งเตือนที่ยังไม่ได้อ่าน เรียงตามวันที่ล่าสุด
     */
    List<Notification> findByIsReadFalseOrderByCreatedAtDesc();

    /**
     * ดึงการแจ้งเตือนที่อ่านแล้ว
     */
    List<Notification> findByIsReadTrue();

    /**
     * ดึงการแจ้งเตือนที่ยังไม่ได้อ่าน (ไม่เรียงลำดับ)
     */
    List<Notification> findByIsReadFalse();

    /**
     * นับจำนวนการแจ้งเตือนที่ยังไม่ได้อ่าน
     */
    long countByIsReadFalse();

    /**
     * ดึงการแจ้งเตือนของนักเรียนคนนั้น
     */
    List<Notification> findByStudentIdOrderByCreatedAtDesc(Long studentId);

    /**
     * ดึงการแจ้งเตือนตาม class type และ class id
     */
    List<Notification> findByClassTypeAndClassIdOrderByCreatedAtDesc(String classType, Long classId);

    /**
     * ดึงการแจ้งเตือนของนักเรียนและคลาส (ใช้ตรวจสอบว่ามี notification แล้วหรือยัง)
     */
    List<Notification> findByStudentIdAndClassTypeAndClassIdAndMilestoneReached(
            Long studentId, String classType, Long classId, Integer milestoneReached);

    /**
     * ดึงการแจ้งเตือนที่ยังไม่ได้อ่านของนักเรียนและคลาส
     * (สำหรับเช็คว่ามี notification ที่ milestone สูงกว่ายังไม่ได้อ่านหรือไม่)
     */
    List<Notification> findByStudentIdAndClassTypeAndClassIdAndIsReadFalse(
            Long studentId, String classType, Long classId);

    /**
     * เช็คว่ามี notification แล้วหรือยัง สำหรับเดือนนั้น ๆ
     * ดูจาก notification_type, student_id, subject_id, และวันที่สร้าง (ในเดือนเดียวกัน)
     */
    @Query("SELECT CASE WHEN COUNT(n) > 0 THEN true ELSE false END FROM Notification n " +
            "WHERE n.notificationType = 'MONTHLY_EXPIRATION' " +
            "AND n.student.id = :studentId " +
            "AND n.subject.id = :subjectId " +
            "AND n.classType = 'monthly' " +
            "AND n.classId = :classId " +
            "AND YEAR(n.createdAt) = :year " +
            "AND MONTH(n.createdAt) = :month")
    boolean existsMonthlyExpirationForMonth(
            @Param("studentId") Long studentId,
            @Param("subjectId") Long subjectId,
            @Param("classId") Long classId,
            @Param("year") int year,
            @Param("month") int month
    );
}