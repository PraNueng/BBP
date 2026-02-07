package com.example.backend.services.student;

import com.example.backend.dtos.student.StudentDto;
import com.example.backend.repositories.student.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class StudentValidationService {

    @Autowired
    private StudentRepository studentRepository;

    // Regex patterns
    private static final Pattern PHONE_PATTERN = Pattern.compile("^0\\d{8,9}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern NAME_PATTERN = Pattern.compile("^[ก-๙A-Za-z ]+$");

    /**
     * ตรวจสอบข้อมูลนักเรียนทั้งหมด
     */
    public ValidationResult validateStudent(StudentDto dto, boolean isUpdate) {
        ValidationResult result = new ValidationResult();

        // ตรวจสอบชื่อ
        if (dto.getFirstName() == null || dto.getFirstName().trim().isEmpty()) {
            result.addError("firstName", "กรุณากรอกชื่อ");
        } else if (dto.getFirstName().length() > 100) {
            result.addError("firstName", "ชื่อยาวเกินไป (สูงสุด 100 ตัวอักษร)");
        } else if (!NAME_PATTERN.matcher(dto.getFirstName()).matches()) {
            result.addError("firstName", "ชื่อประกอบด้วยตัวอักษรไทยหรืออังกฤษเท่านั้น");
        }

        // ตรวจสอบนามสกุล
        if (dto.getLastName() != null && !dto.getLastName().trim().isEmpty()) {
            if (dto.getLastName().length() > 100) {
                result.addError("lastName", "นามสกุลยาวเกินไป (สูงสุด 100 ตัวอักษร)");
            } else if (!NAME_PATTERN.matcher(dto.getLastName()).matches()) {
                result.addError("lastName", "นามสกุลประกอบด้วยตัวอักษรไทยหรืออังกฤษเท่านั้น");
            }
        }

        // ตรวจสอบชื่อเล่น
        if (dto.getNickname() != null && dto.getNickname().length() > 50) {
            result.addError("nickname", "ชื่อเล่นยาวเกินไป (สูงสุด 50 ตัวอักษร)");
        }

        // ตรวจสอบชั้นเรียน
        if (dto.getGradeId() == null) {
            result.addError("gradeId", "กรุณาเลือกชั้นเรียน");
        }

        // ตรวจสอบเบอร์โทรนักเรียน
        if (dto.getPhoneStudent() != null && !dto.getPhoneStudent().trim().isEmpty()) {
            if (!PHONE_PATTERN.matcher(dto.getPhoneStudent()).matches()) {
                result.addError("phoneStudent", "เบอร์โทรไม่ถูกต้อง (ต้องขึ้นต้นด้วย 0 และมี 9-10 หลัก)");
            }
        }

        // ตรวจสอบเบอร์โทรผู้ปกครอง
        if (dto.getPhoneParent() != null && !dto.getPhoneParent().trim().isEmpty()) {
            if (!PHONE_PATTERN.matcher(dto.getPhoneParent()).matches()) {
                result.addError("phoneParent", "เบอร์โทรผู้ปกครองไม่ถูกต้อง (ต้องขึ้นต้นด้วย 0 และมี 9-10 หลัก)");
            }
        }

        // ตรวจสอบรหัสนักเรียนซ้ำ (เฉพาะตอนสร้างใหม่)
        if (!isUpdate && dto.getStudentCode() != null) {
            if (studentRepository.existsByStudentCode(dto.getStudentCode())) {
                result.addError("studentCode", "รหัสนักเรียนนี้มีอยู่ในระบบแล้ว");
            }
        }

        // ✅ ลบการ validate คลาสออก เพราะไม่มี StudentClass แล้ว
        // คลาสจะถูกจัดการแยกต่างหากใน HourlyGroupClass, HourlyIndividualClass, MonthlyClass

        return result;
    }

    /**
     * ตรวจสอบว่าชื่อนักเรียนซ้ำหรือไม่ (ชื่อ + นามสกุล + ชั้นเรียน)
     */
    public boolean isDuplicateStudent(String firstName, String lastName, Long gradeId, Long excludeId) {
        if (excludeId != null) {
            return studentRepository.existsByFirstNameAndLastNameAndGradeIdAndIdNot(
                    firstName, lastName, gradeId, excludeId);
        }
        return studentRepository.existsByFirstNameAndLastNameAndGradeId(firstName, lastName, gradeId);
    }

    /**
     * ตรวจสอบเบอร์โทรศัพท์
     */
    public boolean isValidPhoneNumber(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return true; // optional field
        }
        return PHONE_PATTERN.matcher(phone).matches();
    }

    /**
     * ตรวจสอบอีเมล
     */
    public boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return true; // optional field
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * ตรวจสอบความยาวข้อความ
     */
    public boolean isValidLength(String text, int maxLength) {
        if (text == null) {
            return true;
        }
        return text.length() <= maxLength;
    }

    /**
     * ทำความสะอาดหมายเลขโทรศัพท์ (ลบเครื่องหมาย - และช่องว่าง)
     */
    public String sanitizePhoneNumber(String phone) {
        if (phone == null) {
            return null;
        }
        return phone.replaceAll("[^0-9]", "");
    }

    /**
     * ทำความสะอาดข้อความ (trim และลบ whitespace ที่ไม่จำเป็น)
     */
    public String sanitizeText(String text) {
        if (text == null) {
            return null;
        }
        return text.trim().replaceAll("\\s+", " ");
    }

    // ===== Inner Class =====

    /**
     * Class สำหรับเก็บผลการ validate
     */
    public static class ValidationResult {
        private List<ValidationError> errors = new ArrayList<>();

        public void addError(String field, String message) {
            errors.add(new ValidationError(field, message));
        }

        public boolean isValid() {
            return errors.isEmpty();
        }

        public List<ValidationError> getErrors() {
            return errors;
        }

        public String getErrorMessage() {
            if (errors.isEmpty()) {
                return "";
            }
            StringBuilder sb = new StringBuilder();
            for (ValidationError error : errors) {
                sb.append(error.getMessage()).append("\n");
            }
            return sb.toString().trim();
        }
    }

    /**
     * Class สำหรับเก็บข้อมูล error แต่ละรายการ
     */
    public static class ValidationError {
        private String field;
        private String message;

        public ValidationError(String field, String message) {
            this.field = field;
            this.message = message;
        }

        public String getField() { return field; }
        public String getMessage() { return message; }
    }
}