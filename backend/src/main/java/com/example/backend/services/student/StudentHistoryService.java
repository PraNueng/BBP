package com.example.backend.services.student;

import com.example.backend.dtos.student.StudentHistoryDto;
import com.example.backend.entities.student.Student;
import com.example.backend.entities.student.StudentHistory;
import com.example.backend.entities.User;
import com.example.backend.repositories.student.StudentHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class StudentHistoryService {

    @Autowired
    private StudentHistoryRepository studentHistoryRepository;

    /**
     * บันทึกการเปลี่ยนแปลงของ field
     */
    @Transactional
    public void trackFieldChange(Student student, String fieldName, String oldValue, String newValue, User updatedBy) {
        // Skip if values are the same
        if (Objects.equals(oldValue, newValue)) {
            return;
        }

        // Skip if both are null or empty
        if (isNullOrEmpty(oldValue) && isNullOrEmpty(newValue)) {
            return;
        }

        StudentHistory history = new StudentHistory();
        history.setStudent(student);
        history.setAction("UPDATE");
        history.setFieldName(fieldName);
        history.setOldValue(oldValue != null ? oldValue : "");
        history.setNewValue(newValue != null ? newValue : "");
        history.setUpdatedBy(updatedBy);
        history.setUpdatedAt(LocalDateTime.now());

        studentHistoryRepository.save(history);
    }

    /**
     * ดึงประวัติทั้งหมดของนักเรียน เรียงตามเวลาล่าสุด
     */
    public List<StudentHistoryDto> getStudentHistory(Long studentId) {
        return studentHistoryRepository.findByStudentId(studentId).stream()
                .map(StudentHistoryDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * ดึงประวัติของ field เฉพาะ
     */
    public List<StudentHistoryDto> getHistoryByField(Long studentId, String fieldName) {
        return studentHistoryRepository.findByStudentIdAndFieldName(studentId, fieldName)
                .stream()
                .map(StudentHistoryDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * ดึงประวัติที่แก้ไขโดยผู้ใช้คนใดคนหนึ่ง
     */
    public List<StudentHistoryDto> getHistoryByUser(Long studentId, Long userId) {
        return studentHistoryRepository.findByStudentId(studentId).stream()
                .filter(h -> h.getUpdatedBy().getId().equals(userId))
                .map(StudentHistoryDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * ดึงประวัติในช่วงเวลาที่กำหนด
     */
    public List<StudentHistoryDto> getHistoryByDateRange(Long studentId, LocalDateTime startDate, LocalDateTime endDate) {
        return studentHistoryRepository.findByDateRange(startDate, endDate)
                .stream()
                .filter(h -> h.getStudent().getId().equals(studentId))
                .map(StudentHistoryDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * นับจำนวนการแก้ไขทั้งหมด
     */
    public Long countHistoryByStudent(Long studentId) {
        return studentHistoryRepository.countByStudentId(studentId);
    }

    /**
     * ลบประวัติทั้งหมดของนักเรียน (ใช้เมื่อลบนักเรียน)
     */
    @Transactional
    public void deleteHistoryByStudent(Long studentId) {
        List<StudentHistory> histories = studentHistoryRepository.findByStudentId(studentId);
        studentHistoryRepository.deleteAll(histories);
    }

    /**
     * บันทึกการเปลี่ยนแปลงหลายฟิลด์พร้อมกัน
     */
    @Transactional
    public void trackMultipleChanges(Student student, List<FieldChange> changes, User updatedBy) {
        for (FieldChange change : changes) {
            trackFieldChange(student, change.getFieldName(), change.getOldValue(), change.getNewValue(), updatedBy);
        }
    }

    /**
     * สร้าง summary ของการแก้ไข
     */
    public HistorySummary getHistorySummary(Long studentId) {
        List<StudentHistory> histories = studentHistoryRepository.findByStudentId(studentId);

        HistorySummary summary = new HistorySummary();
        summary.setTotalChanges(histories.size());

        if (!histories.isEmpty()) {
            summary.setLastUpdated(histories.get(0).getUpdatedAt());
            summary.setLastUpdatedBy(histories.get(0).getUpdatedBy().getUsername());

            // นับจำนวนการแก้ไขแต่ละ field
            summary.setFieldChangeCounts(
                    histories.stream()
                            .collect(Collectors.groupingBy(
                                    StudentHistory::getFieldName,
                                    Collectors.counting()
                            ))
            );
        }

        return summary;
    }

    /**
     * ตรวจสอบว่าค่าเป็น null หรือ empty
     */
    private boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    // ===== Inner Classes =====

    /**
     * Class สำหรับเก็บข้อมูลการเปลี่ยนแปลง
     */
    public static class FieldChange {
        private String fieldName;
        private String oldValue;
        private String newValue;

        public FieldChange(String fieldName, String oldValue, String newValue) {
            this.fieldName = fieldName;
            this.oldValue = oldValue;
            this.newValue = newValue;
        }

        public String getFieldName() { return fieldName; }
        public String getOldValue() { return oldValue; }
        public String getNewValue() { return newValue; }
    }

    /**
     * Class สำหรับสรุปประวัติ
     */
    public static class HistorySummary {
        private int totalChanges;
        private LocalDateTime lastUpdated;
        private String lastUpdatedBy;
        private java.util.Map<String, Long> fieldChangeCounts;

        public int getTotalChanges() { return totalChanges; }
        public void setTotalChanges(int totalChanges) { this.totalChanges = totalChanges; }

        public LocalDateTime getLastUpdated() { return lastUpdated; }
        public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }

        public String getLastUpdatedBy() { return lastUpdatedBy; }
        public void setLastUpdatedBy(String lastUpdatedBy) { this.lastUpdatedBy = lastUpdatedBy; }

        public java.util.Map<String, Long> getFieldChangeCounts() { return fieldChangeCounts; }
        public void setFieldChangeCounts(java.util.Map<String, Long> fieldChangeCounts) {
            this.fieldChangeCounts = fieldChangeCounts;
        }
    }

    /**
     * บันทึกการเพิ่มคลาส
     */
    @Transactional
    public void trackClassAdded(Student student, String classType, String className,
                                String subjectName, User updatedBy) {
        StudentHistory history = new StudentHistory();
        history.setStudent(student);
        history.setAction("INSERT");
        history.setFieldName("classAdded");
        history.setOldValue("");
        history.setNewValue(String.format("%s - %s (%s)", classType, className, subjectName));
        history.setUpdatedBy(updatedBy);
        history.setUpdatedAt(LocalDateTime.now());

        studentHistoryRepository.save(history);
    }

    /**
     * บันทึกการลบคลาส (deactivate)
     */
    @Transactional
    public void trackClassRemoved(Student student, String classType, String className,
                                  String subjectName, User updatedBy) {
        StudentHistory history = new StudentHistory();
        history.setStudent(student);
        history.setAction("DELETE");
        history.setFieldName("classRemoved");
        history.setOldValue(String.format("%s - %s (%s)", classType, className, subjectName));
        history.setNewValue("");
        history.setUpdatedBy(updatedBy);
        history.setUpdatedAt(LocalDateTime.now());

        studentHistoryRepository.save(history);
    }

    /**
     * บันทึกการเปิดการใช้งานคลาสอีกครั้ง
     */
    @Transactional
    public void trackClassReactivated(Student student, String classType, String className,
                                      String subjectName, User updatedBy) {
        StudentHistory history = new StudentHistory();
        history.setStudent(student);
        history.setAction("UPDATE");
        history.setFieldName("classReactivated");
        history.setOldValue("");
        history.setNewValue(String.format("%s - %s (%s)", classType, className, subjectName));
        history.setUpdatedBy(updatedBy);
        history.setUpdatedAt(LocalDateTime.now());

        studentHistoryRepository.save(history);
    }

    /**
     * บันทึกประวัติการซื้อคอร์ส
     */
    @Transactional
    public void trackCoursePurchaseAdded(
            Student student,
            String subjectName,
            Double hoursPurchased,
            String classType,
            String monthlySubtypeName,
            User updatedBy
    ) {
        StudentHistory history = new StudentHistory();
        history.setStudent(student);
        history.setAction("INSERT");
        history.setFieldName("coursePurchaseAdded");
        history.setOldValue("");
        String newValue;
        if (hoursPurchased == null) {
            // Monthly
            newValue = String.format("%s รายเดือน %s",
                    subjectName,
                    monthlySubtypeName != null ? monthlySubtypeName : "");
        } else {
            // Hourly
            String typeLabel = formatClassType(classType);
            newValue = String.format("%s %s %.1f ชั่วโมง",
                    subjectName, typeLabel, hoursPurchased);
        }

        history.setNewValue(newValue);
        history.setUpdatedBy(updatedBy);
        history.setUpdatedAt(LocalDateTime.now());

        studentHistoryRepository.save(history);
    }

    public void trackCoursePurchaseUpdated(Student student,
                                           String oldSubject, Double oldHours, String oldClassType,
                                           String newSubject, Double newHours, String newClassType,
                                           String oldEditReason,
                                           String editReason,
                                           User updatedBy) {
        // Format old value
        String oldValue;
        if (oldHours != null) {
            oldValue = String.format("%s %s %.1f ชั่วโมง", oldSubject, formatClassType(oldClassType), oldHours);
        } else {
            oldValue = String.format("%s รายเดือน %s", oldSubject, formatClassType(oldClassType));
        }

        // Add old edit reason if exists
        if (oldEditReason != null && !oldEditReason.trim().isEmpty()) {
            oldValue += " | เหตุผล: " + oldEditReason;
        }

        // Format new value
        String newValue;
        if (newHours != null) {
            newValue = String.format("%s %s %.1f ชั่วโมง", newSubject, formatClassType(newClassType), newHours);
        } else {
            newValue = String.format("%s รายเดือน %s", newSubject, formatClassType(newClassType));
        }

        // Add new edit reason if exists
        if (editReason != null && !editReason.trim().isEmpty()) {
            newValue += " | เหตุผล: " + editReason;
        }
        trackFieldChange(student, "แก้ไขคอร์ส", oldValue, newValue, updatedBy);
    }

    public void trackCoursePurchaseDeleted(Student student,
                                           String subjectName,
                                           Double hours,
                                           String classType,
                                           User updatedBy) {
        String value;
        if (hours != null) {
            value = String.format("%s %s %.1f ชั่วโมง",
                    subjectName, formatClassType(classType), hours);
        } else {
            value = String.format("%s รายเดือน %s",
                    subjectName, formatClassType(classType));
        }

        trackFieldChange(student, "ลบคอร์ส", value, null, updatedBy);
    }

    private String formatClassType(String classType) {
        if (classType == null) return "";

        switch (classType.toLowerCase()) {
            case "monthly":
            case "รายเดือน":
                return "รายเดือน";
            case "hourly_group":
            case "group":
                return "กลุ่มรวม";
            case "hourly_individual":
            case "individual":
                return "PV-เดี่ยว";
            case "hourly_individual_group":
            case "individual_group":
                return "PV-กลุ่ม";
            default:
                return classType;
        }
    }
}