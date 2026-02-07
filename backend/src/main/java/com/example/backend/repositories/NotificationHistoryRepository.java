package com.example.backend.repositories;

import com.example.backend.entities.NotificationHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationHistoryRepository extends JpaRepository<NotificationHistory, Long> {

    /**
     * ดึงประวัติของ notification นั้นๆ เรียงตามเวลาล่าสุด
     */
    List<NotificationHistory> findByNotificationIdOrderByUpdatedAtDesc(Long notificationId);

    /**
     * ดึงประวัติที่แก้ไขเฉพาะ receipt_code
     */
    List<NotificationHistory> findByNotificationIdAndFieldNameOrderByUpdatedAtDesc(
            Long notificationId, String fieldName);
}