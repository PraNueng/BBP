package com.example.backend.services.student;

import com.example.backend.entities.Grade;
import com.example.backend.repositories.student.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * Service สำหรับสร้างรหัสนักเรียน
 * รับผิดชอบการ generate และตรวจสอบความซ้ำซ้อน
 */
@Service
public class StudentCodeGeneratorService {

    @Autowired
    private StudentRepository studentRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final int MAX_RETRY = 10;

    /**
     * สร้างรหัสนักเรียนแบบ Sequential
     * Format: GRADE-YYYYMMDD-XXXX
     * Example: M1-20241107-0001
     */
    public String generateSequentialCode(Grade grade) {
        String gradePrefix = normalizeGradePrefix(grade.getGradeName());
        String dateStr = LocalDate.now().format(DATE_FORMATTER);

        // หาลำดับถัดไป
        long count = studentRepository.count();
        String sequence = String.format("%04d", count + 1);

        String code = gradePrefix + "-" + dateStr + "-" + sequence;

        // ถ้ารหัสซ้ำ ให้เพิ่มลำดับไปเรื่อย ๆ
        int retry = 0;
        while (studentRepository.existsByStudentCode(code) && retry < MAX_RETRY) {
            sequence = String.format("%04d", count + retry + 2);
            code = gradePrefix + "-" + dateStr + "-" + sequence;
            retry++;
        }

        return code;
    }

    /**
     * สร้างรหัสนักเรียนแบบ Random
     * Format: GRADE-YYYYMMDD-RRRR (R = Random number)
     * Example: M1-20241107-7A3B
     */
    public String generateRandomCode(Grade grade) {
        String gradePrefix = normalizeGradePrefix(grade.getGradeName());
        String dateStr = LocalDate.now().format(DATE_FORMATTER);

        String code;
        int retry = 0;

        do {
            String randomPart = generateRandomString(4);
            code = gradePrefix + "-" + dateStr + "-" + randomPart;
            retry++;
        } while (studentRepository.existsByStudentCode(code) && retry < MAX_RETRY);

        if (retry >= MAX_RETRY) {
            throw new RuntimeException("Cannot generate unique student code after " + MAX_RETRY + " attempts");
        }

        return code;
    }

    /**
     * สร้างรหัสนักเรียนแบบกำหนดเอง
     * Format: GRADE-CUSTOM
     * Example: M1-JOHN2024
     */
    public String generateCustomCode(Grade grade, String customPart) {
        String gradePrefix = normalizeGradePrefix(grade.getGradeName());
        String code = gradePrefix + "-" + customPart.toUpperCase();

        if (studentRepository.existsByStudentCode(code)) {
            throw new RuntimeException("Student code '" + code + "' already exists");
        }

        return code;
    }

    /**
     * สร้างรหัสนักเรียนแบบใช้เฉพาะปี
     * Format: GRADE-YY-XXXX
     * Example: M1-24-0001
     */
    public String generateYearBasedCode(Grade grade) {
        String gradePrefix = normalizeGradePrefix(grade.getGradeName());
        String year = String.valueOf(LocalDate.now().getYear()).substring(2); // เอา 2 ตัวหลัง

        // นับจำนวนนักเรียนในปีนี้
        String yearPrefix = gradePrefix + "-" + year;
        long countThisYear = studentRepository.countByStudentCodeStartingWith(yearPrefix);
        String sequence = String.format("%04d", countThisYear + 1);

        String code = yearPrefix + "-" + sequence;

        // ถ้ารหัสซ้ำ ให้เพิ่มลำดับ
        int retry = 0;
        while (studentRepository.existsByStudentCode(code) && retry < MAX_RETRY) {
            sequence = String.format("%04d", countThisYear + retry + 2);
            code = yearPrefix + "-" + sequence;
            retry++;
        }

        return code;
    }

    /**
     * ตรวจสอบว่ารหัสนักเรียนซ้ำหรือไม่
     */
    public boolean isCodeExists(String code) {
        return studentRepository.existsByStudentCode(code);
    }

    /**
     * แปลงชื่อชั้นเรียนเป็น prefix
     * ม.1 -> M1
     * ม.6 -> M6
     * อื่น ๆ -> OTHER
     */
    private String normalizeGradePrefix(String gradeName) {
        if (gradeName == null || gradeName.trim().isEmpty()) {
            return "STD";
        }

        // แปลง "ม.1" เป็น "M1"
        if (gradeName.startsWith("ม.")) {
            return "M" + gradeName.substring(2);
        }

        // แปลง "Grade 1" เป็น "G1"
        if (gradeName.toLowerCase().startsWith("grade")) {
            return "G" + gradeName.replaceAll("[^0-9]", "");
        }

        // กรณีอื่น ๆ ให้ใช้ 3 ตัวอักษรแรก
        String cleaned = gradeName.replaceAll("[^A-Za-z0-9]", "").toUpperCase();

        // ถ้าหลังจาก clean แล้วเป็น string ว่าง (เช่น "อื่น ๆ") ให้ return "OTHER"
        if (cleaned.isEmpty()) {
            return "OTHER";
        }

        return cleaned.substring(0, Math.min(3, cleaned.length()));
    }

    /**
     * สร้าง random string (ตัวเลขและตัวอักษร)
     */
    private String generateRandomString(int length) {
        String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }

        return sb.toString();
    }

    /**
     * สร้างรหัสนักเรียนแบบ Batch (สำหรับเพิ่มนักเรียนหลายคนพร้อมกัน)
     */
    public java.util.List<String> generateBatchCodes(Grade grade, int count) {
        java.util.List<String> codes = new java.util.ArrayList<>();

        for (int i = 0; i < count; i++) {
            String code = generateSequentialCode(grade);
            codes.add(code);
        }

        return codes;
    }

    /**
     * Parse รหัสนักเรียนเพื่อดึงข้อมูล
     */
    public StudentCodeInfo parseCode(String code) {
        if (code == null || !code.contains("-")) {
            return null;
        }

        String[] parts = code.split("-");

        StudentCodeInfo info = new StudentCodeInfo();
        info.setGradePrefix(parts[0]);

        if (parts.length >= 2) {
            info.setDateOrYear(parts[1]);
        }

        if (parts.length >= 3) {
            info.setSequence(parts[2]);
        }

        return info;
    }

    /**
     * Class สำหรับเก็บข้อมูลที่ parse จากรหัสนักเรียน
     */
    public static class StudentCodeInfo {
        private String gradePrefix;
        private String dateOrYear;
        private String sequence;

        public String getGradePrefix() { return gradePrefix; }
        public void setGradePrefix(String gradePrefix) { this.gradePrefix = gradePrefix; }

        public String getDateOrYear() { return dateOrYear; }
        public void setDateOrYear(String dateOrYear) { this.dateOrYear = dateOrYear; }

        public String getSequence() { return sequence; }
        public void setSequence(String sequence) { this.sequence = sequence; }
    }
}