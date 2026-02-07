package com.example.backend.controllers;

import com.example.backend.dtos.NotificationDto;
import com.example.backend.dtos.NotificationHistoryDto;
import com.example.backend.dtos.UpdateReceiptCodeRequest;
import com.example.backend.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    /**
     * ดึงการแจ้งเตือนทั้งหมด (เฉพาะ Admin)
     * เรียงลำดับจาก unread ก่อน แล้วเรียงตามวันที่ล่าสุด
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<NotificationDto>> getAllNotifications() {
        List<NotificationDto> notifications = notificationService.getAllNotifications();
        return ResponseEntity.ok(notifications);
    }

    /**
     * ดึงจำนวนการแจ้งเตือนที่ยังไม่ได้อ่าน (เฉพาะ Admin)
     */
    @GetMapping("/unread-count")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Long>> getUnreadCount() {
        long count = notificationService.getUnreadCount();
        Map<String, Long> response = new HashMap<>();
        response.put("count", count);
        return ResponseEntity.ok(response);
    }

    /**
     * ดึงการแจ้งเตือนที่ยังไม่ได้อ่าน (เฉพาะ Admin)
     */
    @GetMapping("/unread")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<NotificationDto>> getUnreadNotifications() {
        List<NotificationDto> notifications = notificationService.getUnreadNotifications();
        return ResponseEntity.ok(notifications);
    }

    /**
     * ทำเครื่องหมายว่าอ่านแล้ว (เฉพาะ Admin)
     */
    @PatchMapping("/{id}/mark-read")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> markAsRead(
            @PathVariable Long id,
            Authentication authentication) {
        String username = authentication.getName();
        notificationService.markAsRead(id, username);
        return ResponseEntity.noContent().build();
    }

    /**
     * ทำเครื่องหมายทั้งหมดว่าอ่านแล้ว (เฉพาะ Admin)
     */
    @PatchMapping("/mark-all-read")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> markAllAsRead(Authentication authentication) {
        String username = authentication.getName();
        notificationService.markAllAsRead(username);
        return ResponseEntity.noContent().build();
    }

    /**
     * ลบการแจ้งเตือน (เฉพาะ Admin)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * ลบการแจ้งเตือนที่อ่านแล้วทั้งหมด (เฉพาะ Admin)
     */
    @DeleteMapping("/read")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Integer>> deleteAllRead() {
        int deletedCount = notificationService.deleteAllReadNotifications();
        Map<String, Integer> response = new HashMap<>();
        response.put("deletedCount", deletedCount);
        return ResponseEntity.ok(response);
    }

    /**
     * ดึงการแจ้งเตือนของนักเรียนคนนั้น (เฉพาะ Admin)
     */
    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<NotificationDto>> getNotificationsByStudent(
            @PathVariable Long studentId) {
        List<NotificationDto> notifications = notificationService.getNotificationsByStudent(studentId);
        return ResponseEntity.ok(notifications);
    }

    /**
     * อัปเดต receipt_code (เฉพาะ Admin)
     */
    @PatchMapping("/{id}/receipt-code")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<NotificationDto> updateReceiptCode(
            @PathVariable Long id,
            @RequestBody UpdateReceiptCodeRequest request,
            Authentication authentication) {
        String username = authentication.getName();
        NotificationDto updated = notificationService.updateReceiptCode(
                id, request.getReceiptCode(), username);
        return ResponseEntity.ok(updated);
    }

    /**
     * ดึงประวัติการแก้ไข notification (เฉพาะ Admin)
     */
    @GetMapping("/{id}/history")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<NotificationHistoryDto>> getNotificationHistory(
            @PathVariable Long id) {
        List<NotificationHistoryDto> history = notificationService.getNotificationHistory(id);
        return ResponseEntity.ok(history);
    }

    /**
     * ดึงประวัติการแก้ไข receipt_code เท่านั้น (เฉพาะ Admin)
     */
    @GetMapping("/{id}/receipt-code-history")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<NotificationHistoryDto>> getReceiptCodeHistory(
            @PathVariable Long id) {
        List<NotificationHistoryDto> history = notificationService.getReceiptCodeHistory(id);
        return ResponseEntity.ok(history);
    }

    /**
     * Endpoint สำหรับทดสอบ notification รายเดือน (DEV/TEST ONLY)
     * เรียกใช้: GET /api/notifications/test/monthly-expiration
     */
    @GetMapping("/test/monthly-expiration")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> testMonthlyExpirationNotifications() {
        try {
            // เรียก method เดียวกับที่ Scheduler เรียก
            notificationService.checkAndCreateMonthlyExpirationNotifications();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Monthly expiration notifications checked and created successfully");
            response.put("timestamp", LocalDateTime.now());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error: " + e.getMessage());

            return ResponseEntity.status(500).body(response);
        }
    }
}