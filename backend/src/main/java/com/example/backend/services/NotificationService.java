package com.example.backend.services;

import com.example.backend.dtos.NotificationDto;
import com.example.backend.dtos.NotificationHistoryDto;
import com.example.backend.entities.Notification;
import com.example.backend.entities.NotificationHistory;
import com.example.backend.entities.Subject;
import com.example.backend.entities.User;
import com.example.backend.entities.monthly.MonthlyClass;
import com.example.backend.entities.monthly.MonthlyEnrollment;
import com.example.backend.entities.student.Student;
import com.example.backend.entities.student.StudentCoursePurchase;
import com.example.backend.repositories.NotificationHistoryRepository;
import com.example.backend.repositories.NotificationRepository;
import com.example.backend.repositories.UserRepository;
import com.example.backend.repositories.hourly.HourlyIndividualClassStudentRepository;
import com.example.backend.repositories.monthly.MonthlyEnrollmentRepository;
import com.example.backend.repositories.student.StudentCoursePurchaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationHistoryRepository notificationHistoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentCoursePurchaseRepository coursePurchaseRepository;

    @Autowired
    private HourlyIndividualClassStudentRepository hourlyIndividualClassStudentRepository;

    @Autowired
    private MonthlyEnrollmentRepository monthlyEnrollmentRepository;

    /**
     * ดึงการแจ้งเตือนทั้งหมด เรียงตาม unread ก่อน แล้วตามวันที่
     */
    @Transactional(readOnly = true)
    public List<NotificationDto> getAllNotifications() {
        List<Notification> notifications = notificationRepository
                .findAllByOrderByIsReadAscCreatedAtDesc();

        return notifications.stream()
                .map(notification -> {
                    NotificationDto dto = NotificationDto.fromEntity(notification);

                    // ดึง isIndividual สำหรับ hourly_individual
                    if ("hourly_individual".equals(notification.getClassType())) {
                        try {
                            // นับจำนวนนักเรียนทั้งหมด (รวม inactive) ที่เคยอยู่ในคลาส
                            long totalStudents = hourlyIndividualClassStudentRepository
                                    .countAllByClassId(notification.getClassId());

                            // true = PV-เดี่ยว (1 คน), false = PV-กลุ่ม (>1 คน)
                            dto.setIsIndividual(totalStudents == 1);
                        } catch (Exception e) {
                            // ถ้าดึงไม่ได้ ให้เป็น null
                            dto.setIsIndividual(null);
                        }
                    }

                    return dto;
                })
                .collect(Collectors.toList());
    }

    /**
     * นับจำนวนการแจ้งเตือนที่ยังไม่ได้อ่าน
     */
    @Transactional(readOnly = true)
    public long getUnreadCount() {
        return notificationRepository.countByIsReadFalse();
    }

    /**
     * ดึงการแจ้งเตือนที่ยังไม่ได้อ่าน
     */
    @Transactional(readOnly = true)
    public List<NotificationDto> getUnreadNotifications() {
        List<Notification> notifications = notificationRepository
                .findByIsReadFalseOrderByCreatedAtDesc();

        return notifications.stream()
                .map(notification -> {
                    NotificationDto dto = NotificationDto.fromEntity(notification);

                    if ("hourly_individual".equals(notification.getClassType())) {
                        try {
                            long totalStudents = hourlyIndividualClassStudentRepository
                                    .countAllByClassId(notification.getClassId());
                            dto.setIsIndividual(totalStudents == 1);
                        } catch (Exception e) {
                            dto.setIsIndividual(null);
                        }
                    }

                    return dto;
                })
                .collect(Collectors.toList());
    }

    /**
     * ทำเครื่องหมายว่าอ่านแล้ว
     */
    @Transactional
    public void markAsRead(Long notificationId, String username) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found: " + notificationId));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

        notification.setIsRead(true);
        notification.setReadBy(user);
        notification.setReadAt(LocalDateTime.now());

        notificationRepository.save(notification);
    }

    /**
     * ทำเครื่องหมายทั้งหมดว่าอ่านแล้ว
     */
    @Transactional
    public void markAllAsRead(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

        List<Notification> unreadNotifications = notificationRepository
                .findByIsReadFalse();

        LocalDateTime now = LocalDateTime.now();

        for (Notification notification : unreadNotifications) {
            notification.setIsRead(true);
            notification.setReadBy(user);
            notification.setReadAt(now);
        }

        notificationRepository.saveAll(unreadNotifications);
    }

    /**
     * ลบการแจ้งเตือน
     */
    @Transactional
    public void deleteNotification(Long notificationId) {
        notificationRepository.deleteById(notificationId);
    }

    /**
     * ลบการแจ้งเตือนที่อ่านแล้วทั้งหมด
     */
    @Transactional
    public int deleteAllReadNotifications() {
        List<Notification> readNotifications = notificationRepository.findByIsReadTrue();
        int count = readNotifications.size();
        notificationRepository.deleteAll(readNotifications);
        return count;
    }

    /**
     * ดึงการแจ้งเตือนของนักเรียนคนนั้น
     */
    @Transactional(readOnly = true)
    public List<NotificationDto> getNotificationsByStudent(Long studentId) {
        List<Notification> notifications = notificationRepository
                .findByStudentIdOrderByCreatedAtDesc(studentId);

        return notifications.stream()
                .map(notification -> {
                    NotificationDto dto = NotificationDto.fromEntity(notification);

                    if ("hourly_individual".equals(notification.getClassType())) {
                        try {
                            long totalStudents = hourlyIndividualClassStudentRepository
                                    .countAllByClassId(notification.getClassId());
                            dto.setIsIndividual(totalStudents == 1);
                        } catch (Exception e) {
                            dto.setIsIndividual(null);
                        }
                    }

                    return dto;
                })
                .collect(Collectors.toList());
    }

    // ===== NOTIFICATION CREATION =====

    /**
     * ตรวจสอบและสร้าง notification เมื่อนักเรียนเรียนครบชั่วโมง
     * เรียกใช้หลังจากบันทึก HourForm
     */
    @Transactional
    public void checkAndCreateHoursCompletedNotification(
            Student student,
            Long subjectId,
            String classType,
            Long classId,
            BigDecimal previousHoursCompleted) {

        // ดึงข้อมูลการซื้อคอร์สของนักเรียน + วิชานี้
        List<StudentCoursePurchase> purchases = coursePurchaseRepository
                .findByStudentIdAndSubjectIdAndClassTypeAndIsActiveTrue(student.getId(), subjectId, classType);

        if (purchases.isEmpty()) {
            return; // ไม่มีข้อมูลการซื้อคอร์ส
        }

        // ดึง Subject จาก purchase แรก (เพราะ query มาจาก subjectId เดียวกัน)
        Subject subject = purchases.get(0).getSubject();

        // รวมชั่วโมงทั้งหมด
        BigDecimal totalPurchased = purchases.stream()
                .map(StudentCoursePurchase::getHoursPurchased)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCompleted = purchases.stream()
                .map(StudentCoursePurchase::getHoursCompleted)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // คำนวณ milestone ปัจจุบันและก่อนหน้า
        int currentMilestone = totalCompleted.divide(totalPurchased, 0, BigDecimal.ROUND_DOWN).intValue();
        int previousMilestone = previousHoursCompleted.divide(totalPurchased, 0, BigDecimal.ROUND_DOWN).intValue();

        // ตรวจสอบว่าข้ามเส้น milestone หรือไม่ (เช่น จาก 0→1 หรือ 1→2)
        if (currentMilestone > previousMilestone) {
            createNotification(student, subject, classType, classId, totalCompleted, totalPurchased, currentMilestone);
        }
    }

    /**
     * สร้าง notification
     */
    private void createNotification(
            Student student,
            Subject subject,
            String classType,
            Long classId,
            BigDecimal hoursCompleted,
            BigDecimal hoursTarget,
            int milestone) {

        Notification notification = new Notification();
        notification.setStudent(student);
        notification.setSubject(subject);
        notification.setClassType(classType);
        notification.setClassId(classId);
        notification.setNotificationType("HOURS_MILESTONE");
        notification.setMilestoneReached(milestone);

        String studentName = student.getNickname() != null
                ? student.getNickname()
                : student.getFirstName();

        // แปลง classType เป็นภาษาไทย
        String classTypeText;
        if ("hourly_group".equals(classType)) {
            classTypeText = "กลุ่มรวม";
        } else if ("hourly_individual".equals(classType) || "INDIVIDUAL_GROUP".equalsIgnoreCase(classType)) {
            // ดึง isIndividual จาก HourlyIndividualClassStudent
            boolean isIndividual = true; // default เป็น เดี่ยว
            try {
                long totalStudents = hourlyIndividualClassStudentRepository.countAllByClassId(classId);
                isIndividual = (totalStudents == 1);
            } catch (Exception e) {
                // ignore, ใช้ default
            }
            classTypeText = isIndividual ? "PV-เดี่ยว" : "PV-กลุ่ม";
        } else if ("monthly".equals(classType)) {
            classTypeText = "รายเดือน";
        } else {
            classTypeText = classType;
        }

        // สร้าง title และ message
        if (milestone == 1) {
            notification.setTitle("🎉 " + studentName + " เรียนครบชั่วโมงแล้ว!");
            notification.setMessage(
                    String.format("%s เรียนวิชา %s %s ครบ %.1f ชั่วโมงแล้ว (เป้าหมาย %.1f ชม.)",
                            studentName,
                            subject.getSubjectName(),
                            classTypeText,
                            hoursCompleted.doubleValue(),
                            hoursTarget.doubleValue())
            );
        } else {
            notification.setTitle("🎉 " + studentName + " เรียนครบชั่วโมงครั้งที่ " + milestone + "!");
            notification.setMessage(
                    String.format("%s เรียนวิชา %s %s ไปแล้ว %.1f ชั่วโมง (ครบชั่วโมงครั้งที่ %d)",
                            studentName,
                            subject.getSubjectName(),
                            classTypeText,
                            hoursCompleted.doubleValue(),
                            milestone)
            );
        }

        notification.setIsRead(false);

        notificationRepository.save(notification);
    }

    /**
     * ตรวจสอบและสร้าง notification สำหรับทุก purchase ที่ active
     * เรียกใช้เมื่อต้องการตรวจสอบทั้งระบบ (batch job)
     */
    @Transactional
    public void checkAllCoursePurchases() {
        List<StudentCoursePurchase> allPurchases = coursePurchaseRepository
                .findByIsActiveTrue();

        for (StudentCoursePurchase purchase : allPurchases) {
            if (purchase.getHoursCompleted().compareTo(purchase.getHoursPurchased()) >= 0) {
                // TODO: ดึงข้อมูล classType และ classId จาก purchase (ถ้ามี)
                // แล้วเรียก checkAndCreateHoursCompletedNotification
            }
        }
    }

    /**
     * อัปเดต receipt_code พร้อมบันทึกประวัติ
     */
    @Transactional
    public NotificationDto updateReceiptCode(Long notificationId, String newReceiptCode, String username) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found: " + notificationId));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

        String oldReceiptCode = notification.getReceiptCode();

        // ตรวจสอบว่ามีการเปลี่ยนแปลงจริงหรือไม่
        if ((oldReceiptCode == null && newReceiptCode != null) ||
                (oldReceiptCode != null && !oldReceiptCode.equals(newReceiptCode))) {

            // บันทึกประวัติก่อนแก้ไข
            NotificationHistory history = new NotificationHistory();
            history.setNotificationId(notificationId);
            history.setAction("UPDATE");
            history.setFieldName("receipt_code");
            history.setOldValue(oldReceiptCode);
            history.setNewValue(newReceiptCode);
            history.setUpdatedBy(user);

            notificationHistoryRepository.save(history);

            // อัปเดตค่าใหม่
            notification.setReceiptCode(newReceiptCode);
            notificationRepository.save(notification);
        }

        return NotificationDto.fromEntity(notification);
    }

    /**
     * ดึงประวัติการแก้ไข notification
     */
    @Transactional(readOnly = true)
    public List<NotificationHistoryDto> getNotificationHistory(Long notificationId) {
        List<NotificationHistory> histories = notificationHistoryRepository
                .findByNotificationIdOrderByUpdatedAtDesc(notificationId);

        return histories.stream()
                .map(NotificationHistoryDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * ดึงประวัติการแก้ไข receipt_code เท่านั้น
     */
    @Transactional(readOnly = true)
    public List<NotificationHistoryDto> getReceiptCodeHistory(Long notificationId) {
        List<NotificationHistory> histories = notificationHistoryRepository
                .findByNotificationIdAndFieldNameOrderByUpdatedAtDesc(notificationId, "receipt_code");

        return histories.stream()
                .map(NotificationHistoryDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * ตรวจสอบและสร้าง notification สำหรับนักเรียนที่หมดคอร์สรายเดือน
     * เรียกใช้ทุกวันที่ 1 ของเดือน
     */
    @Transactional
    public void checkAndCreateMonthlyExpirationNotifications() {
        LocalDateTime now = LocalDateTime.now();
        int currentYear = now.getYear();
        int currentMonth = now.getMonthValue();

        // ดึงนักเรียนทั้งหมดที่มี monthly enrollment ที่ active
        List<MonthlyEnrollment> activeEnrollments =
                monthlyEnrollmentRepository.findAllActiveEnrollmentsWithDetails();

        for (MonthlyEnrollment enrollment : activeEnrollments) {
            Student student = enrollment.getStudent();
            MonthlyClass monthlyClass = enrollment.getMonthlyClass();
            Subject subject = monthlyClass.getSubject();

            // เช็คว่านักเรียนถูกสร้างมาก่อนเดือนนี้หรือไม่
            LocalDateTime studentCreatedAt = student.getCreatedAt();
            if (studentCreatedAt.getYear() == currentYear &&
                    studentCreatedAt.getMonthValue() == currentMonth) {
                continue; // ข้ามนักเรียนที่ถูกสร้างในเดือนนี้
            }

            // เช็คว่ามี notification แล้วหรือยัง (ป้องกันการสร้างซ้ำ)
            boolean alreadyNotified = notificationRepository.existsMonthlyExpirationForMonth(
                    student.getId(),
                    subject.getId(),
                    monthlyClass.getId(),
                    currentYear,
                    currentMonth
            );

            if (!alreadyNotified) {
                createMonthlyExpirationNotification(student, subject, monthlyClass);
            }
        }
    }

    /**
     * สร้าง notification สำหรับการหมดคอร์สรายเดือน
     */
    private void createMonthlyExpirationNotification(
            Student student,
            Subject subject,
            MonthlyClass monthlyClass) {

        Notification notification = new Notification();
        notification.setStudent(student);
        notification.setSubject(subject);
        notification.setClassType("monthly");
        notification.setClassId(monthlyClass.getId());
        notification.setNotificationType("MONTHLY_EXPIRATION");
        notification.setMilestoneReached(null); // ไม่มี milestone สำหรับ monthly

        String studentName = student.getNickname() != null
                ? student.getNickname()
                : student.getFirstName();

        notification.setTitle("🔔 " + studentName + " หมดคอร์สรายเดือนแล้ว");
        notification.setMessage(
                String.format("%s หมดคอร์สรายเดือน%sแล้ว",
                        studentName,
                        subject.getSubjectName())
        );

        notification.setIsRead(false);

        notificationRepository.save(notification);
    }
}