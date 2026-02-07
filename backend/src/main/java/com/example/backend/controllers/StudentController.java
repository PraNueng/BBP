package com.example.backend.controllers;

import com.example.backend.dtos.hourform.HourFormDto;
import com.example.backend.dtos.student.StudentDetailDto;
import com.example.backend.dtos.student.StudentDto;
import com.example.backend.dtos.student.StudentRequestDto;
import com.example.backend.dtos.student.StudentHistoryDto;
import com.example.backend.services.student.StudentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/students")
@CrossOrigin(origins = "*")
public class StudentController {

    @Autowired
    private StudentService studentService;

    /**
     * ดึงนักเรียนทั้งหมดที่ active
     */
    @GetMapping
    public ResponseEntity<List<StudentDto>> getAllActiveStudents() {
        List<StudentDto> students = studentService.getAllActiveStudents();
        return ResponseEntity.ok(students);
    }

    /**
     * ค้นหานักเรียนด้วยชื่อหรือ filter ตามชั้น
     */
    @GetMapping("/search")
    public ResponseEntity<List<StudentDto>> searchStudents(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String grade) {

        if (grade != null && !grade.isEmpty()) {
            Long gradeId = Long.parseLong(grade);
            List<StudentDto> students = studentService.searchStudentsByGrade(gradeId, q);
            return ResponseEntity.ok(students);
        }

        List<StudentDto> students = studentService.searchStudents(q);
        return ResponseEntity.ok(students);
    }

    /**
     * ดึงนักเรียนตามชั้นเรียน
     */
    @GetMapping("/grade/{grade}")
    public ResponseEntity<List<StudentDto>> getStudentsByGrade(@PathVariable String grade) {
        Long gradeId = Long.parseLong(grade);
        List<StudentDto> students = studentService.getStudentsByGrade(gradeId);
        return ResponseEntity.ok(students);
    }

    /**
     * ดึงข้อมูลนักเรียนตาม ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<StudentDto> getStudentById(@PathVariable Long id) {
        StudentDto student = studentService.getStudentById(id);
        return ResponseEntity.ok(student);
    }

    /**
     * ดึงประวัติการแก้ไขของนักเรียน
     */
    @GetMapping("/{id}/history")
    public ResponseEntity<List<StudentHistoryDto>> getStudentHistory(@PathVariable Long id) {
        List<StudentHistoryDto> history = studentService.getStudentHistory(id);
        return ResponseEntity.ok(history);
    }

    /**
     * ปิดการใช้งานนักเรียน (soft delete)
     */
    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deactivateStudent(@PathVariable Long id) {
        studentService.deactivateStudent(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * เปิดการใช้งานนักเรียนอีกครั้ง
     */
    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> activateStudent(@PathVariable Long id) {
        studentService.activateStudent(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * ลบนักเรียนถาวร (hard delete)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }

    // ===== HELPER METHOD =====

    /**
     * แปลง StudentRequestDto เป็น StudentDto (สำหรับกรณีไม่มีคลาส)
     */
    private StudentDto convertToBasicDto(StudentRequestDto request) {
        StudentDto dto = new StudentDto();
        dto.setFirstName(request.getFirstName());
        dto.setLastName(request.getLastName());
        dto.setNickname(request.getNickname());
        dto.setSchoolName(request.getSchoolName());
        dto.setGradeId(request.getGradeId());
        dto.setPhoneStudent(request.getPhoneStudent());
        dto.setPhoneParent(request.getPhoneParent());
        dto.setStudyPlan(request.getStudyPlan());
        return dto;
    }

    /**
     * ดึงข้อมูลนักเรียนพร้อมคลาสทั้งหมด
     */
    @GetMapping("/{id}/with-classes")
    public ResponseEntity<StudentDetailDto> getStudentWithClasses(
            @PathVariable Long id,
            @RequestParam(required = false) Long gradeId) {
        StudentDetailDto student = studentService.getStudentWithClasses(id, gradeId);
        return ResponseEntity.ok(student);
    }

    /**
     * เพิ่มนักเรียนใหม่ (รองรับทั้ง coursePurchases และ classes แบบเก่า)
     *
     * รองรับ 3 กรณี:
     * 1. เพิ่มนักเรียนอย่างเดียว (ไม่มีทั้ง coursePurchases และ classes)
     * 2. เพิ่มนักเรียน + คอร์สที่ซื้อ (มี coursePurchases)
     * 3. เพิ่มนักเรียน + คลาสแบบเก่า (มี classes) - backward compatibility
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR')")
    public ResponseEntity<?> createStudent(
            @Valid @RequestBody StudentRequestDto request,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            StudentDto created;

            // กรณีที่ 1: มี coursePurchases (วิธีใหม่)
            if (request.getCoursePurchases() != null && !request.getCoursePurchases().isEmpty()) {
                created = studentService.createStudentWithCoursePurchases(request, username);
            }
            // กรณีที่ 2: มี classes (วิธีเก่า - backward compatibility)
            else if (request.getClasses() != null && !request.getClasses().isEmpty()) {
                created = studentService.createStudentWithClasses(request, username);
            }
            // กรณีที่ 3: ไม่มีทั้งสองอย่าง (สร้างแค่นักเรียน)
            else {
                StudentDto basicDto = convertToBasicDto(request);
                created = studentService.createStudent(basicDto, username);
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "เกิดข้อผิดพลาดในการสร้างนักเรียน: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * แก้ไขข้อมูลนักเรียน (รองรับทั้ง coursePurchases และ classes)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR')")
    public ResponseEntity<?> updateStudent(
            @PathVariable Long id,
            @Valid @RequestBody StudentRequestDto request,
            Authentication authentication) {
        try {
            String username = authentication.getName();
            StudentDto updated;

            // ถ้ามี coursePurchases
            if ((request.getCoursePurchases() != null && !request.getCoursePurchases().isEmpty()) ||
                    (request.getUpdatedCoursePurchases() != null && !request.getUpdatedCoursePurchases().isEmpty()) ||
                    (request.getDeletedCoursePurchaseIds() != null && !request.getDeletedCoursePurchaseIds().isEmpty())) {
                updated = studentService.updateStudentWithCoursePurchases(id, request, username);
            }
            // ถ้ามี classes
            else if (request.getClasses() != null && !request.getClasses().isEmpty()) {
                updated = studentService.updateStudentWithClasses(id, request, username);
            }
            // ไม่มีทั้งสองอย่าง (อัพเดทแค่ข้อมูลพื้นฐาน)
            else {
                StudentDto basicDto = convertToBasicDto(request);
                basicDto.setId(id);
                updated = studentService.updateStudent(id, basicDto);
            }

            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "เกิดข้อผิดพลาดในการอัพเดทนักเรียน: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * ดึงประวัติการเรียนของนักเรียน (HourForms ที่เกี่ยวข้อง)
     */
    @GetMapping("/{studentId}/learning-history")
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR')")
    public ResponseEntity<List<HourFormDto>> getStudentLearningHistory(
            @PathVariable Long studentId) {

        List<HourFormDto> history = studentService.getStudentLearningHistory(studentId);
        return ResponseEntity.ok(history);
    }
}