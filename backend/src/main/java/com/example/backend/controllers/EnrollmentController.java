package com.example.backend.controllers;

import com.example.backend.dtos.enrollment.*;
import com.example.backend.services.EnrollmentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/enrollments")
@CrossOrigin(origins = "*")
public class EnrollmentController {

    @Autowired
    private EnrollmentService enrollmentService;

    /**
     * ดึงคลาสทั้งหมดที่นักเรียนลงทะเบียน
     */
    @GetMapping("/students/{studentId}")
    public ResponseEntity<StudentEnrolledClassesDto> getStudentEnrollments(@PathVariable Long studentId) {
        try {
            StudentEnrolledClassesDto enrollments = enrollmentService.getStudentEnrollments(studentId);
            return ResponseEntity.ok(enrollments);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    /**
     * ดึงนักเรียนทั้งหมดในคลาส Hourly Group
     */
    @GetMapping("/hourly-group/{classId}/students")
    public ResponseEntity<List<EnrollmentResponseDto>> getHourlyGroupStudents(@PathVariable Long classId) {
        try {
            List<EnrollmentResponseDto> students = enrollmentService.getHourlyGroupStudents(classId);
            return ResponseEntity.ok(students);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * ดึงนักเรียนทั้งหมดในคลาส Monthly
     */
    @GetMapping("/monthly/{classId}/students")
    public ResponseEntity<List<EnrollmentResponseDto>> getMonthlyStudents(@PathVariable Long classId) {
        try {
            List<EnrollmentResponseDto> students = enrollmentService.getMonthlyStudents(classId);
            return ResponseEntity.ok(students);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * เพิ่มนักเรียนเข้าคลาส Hourly Group
     */
    @PostMapping("/hourly-group")
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR')")
    public ResponseEntity<?> enrollHourlyGroup(@Valid @RequestBody EnrollmentRequestDto request) {
        try {
            EnrollmentResponseDto response = enrollmentService.enrollHourlyGroup(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * เพิ่มนักเรียนเข้าคลาส Monthly
     */
    @PostMapping("/monthly")
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR')")
    public ResponseEntity<?> enrollMonthly(@Valid @RequestBody EnrollmentRequestDto request) {
        try {
            EnrollmentResponseDto response = enrollmentService.enrollMonthly(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * ลบนักเรียนออกจากคลาส Hourly Group
     */
    @DeleteMapping("/hourly-group/{classId}/students/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR')")
    public ResponseEntity<?> unenrollHourlyGroup(
            @PathVariable Long classId,
            @PathVariable Long studentId) {
        try {
            enrollmentService.unenrollHourlyGroup(classId, studentId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Student unenrolled successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * ลบนักเรียนออกจากคลาส Monthly
     */
    @DeleteMapping("/monthly/{classId}/students/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR')")
    public ResponseEntity<?> unenrollMonthly(
            @PathVariable Long classId,
            @PathVariable Long studentId) {
        try {
            enrollmentService.unenrollMonthly(classId, studentId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Student unenrolled successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * เปิดการใช้งาน Monthly Enrollment
     */
    @PatchMapping("/monthly/{enrollmentId}/activate")
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR')")
    public ResponseEntity<?> activateMonthlyEnrollment(@PathVariable Long enrollmentId,
                                                       @RequestBody EnrollmentStatusChangeDto dto) {
        try {
            enrollmentService.activateMonthlyEnrollment(enrollmentId, dto.getReason());
            Map<String, String> response = new HashMap<>();
            response.put("message", "Enrollment activated successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * ปิดการใช้งาน Monthly Enrollment
     */
    @PatchMapping("/monthly/{enrollmentId}/deactivate")
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR')")
    public ResponseEntity<?> deactivateMonthlyEnrollment(@PathVariable Long enrollmentId,
                                                         @RequestBody EnrollmentStatusChangeDto dto) {
        try {
            enrollmentService.deactivateMonthlyEnrollment(enrollmentId, dto.getReason());
            Map<String, String> response = new HashMap<>();
            response.put("message", "Enrollment deactivated successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * เปิดการใช้งาน Hourly Group Enrollment
     */
    @PatchMapping("/hourly-group/{enrollmentId}/activate")
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR')")
    public ResponseEntity<?> activateHourlyGroupEnrollment(@PathVariable Long enrollmentId,
                                                           @RequestBody EnrollmentStatusChangeDto dto) {
        try {
            enrollmentService.activateHourlyGroupEnrollment(enrollmentId, dto.getReason());
            Map<String, String> response = new HashMap<>();
            response.put("message", "Enrollment activated successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * ปิดการใช้งาน Hourly Group Enrollment
     */
    @PatchMapping("/hourly-group/{enrollmentId}/deactivate")
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR')")
    public ResponseEntity<?> deactivateHourlyGroupEnrollment(@PathVariable Long enrollmentId,
                                                             @RequestBody EnrollmentStatusChangeDto dto) {
        try {
            enrollmentService.deactivateHourlyGroupEnrollment(enrollmentId, dto.getReason());
            Map<String, String> response = new HashMap<>();
            response.put("message", "Enrollment deactivated successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PatchMapping("/hourly-individual/{enrollmentId}/activate")
    public ResponseEntity<Void> activateHourlyIndividualEnrollment(@PathVariable Long enrollmentId,
                                                                   @RequestBody EnrollmentStatusChangeDto dto) {
        enrollmentService.activateHourlyIndividualEnrollment(enrollmentId, dto.getReason());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/hourly-individual/{enrollmentId}/deactivate")
    public ResponseEntity<Void> deactivateHourlyIndividualEnrollment(@PathVariable Long enrollmentId,
                                                                     @RequestBody EnrollmentStatusChangeDto dto) {
        enrollmentService.deactivateHourlyIndividualEnrollment(enrollmentId, dto.getReason());
        return ResponseEntity.noContent().build();
    }

    /**
     * ดึงนักเรียนทั้งหมดในคลาส Hourly Individual
     */
    @GetMapping("/hourly-individual/{classId}/students")
    public ResponseEntity<List<EnrollmentResponseDto>> getHourlyIndividualStudents(@PathVariable Long classId) {
        try {
            List<EnrollmentResponseDto> students = enrollmentService.getHourlyIndividualStudents(classId);
            return ResponseEntity.ok(students);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * เพิ่มนักเรียนเข้าคลาส Hourly Individual
     */
    @PostMapping("/hourly-individual")
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR')")
    public ResponseEntity<?> enrollHourlyIndividual(@Valid @RequestBody EnrollmentRequestDto request) {
        try {
            EnrollmentResponseDto response = enrollmentService.enrollHourlyIndividual(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * ลบนักเรียนออกจากคลาส Hourly Individual
     */
    @DeleteMapping("/hourly-individual/{classId}/students/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR')")
    public ResponseEntity<?> unenrollHourlyIndividual(
            @PathVariable Long classId,
            @PathVariable Long studentId) {
        try {
            enrollmentService.unenrollHourlyIndividual(classId, studentId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Student unenrolled successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * ดึงประวัติการเปลี่ยนสถานะของ enrollment
     */
    @GetMapping("/{enrollmentType}/{enrollmentId}/history")
    public ResponseEntity<?> getEnrollmentHistory(
            @PathVariable String enrollmentType,
            @PathVariable Long enrollmentId) {
        try {
            List<EnrollmentHistoryDto> history = enrollmentService.getEnrollmentHistory(enrollmentType, enrollmentId);
            return ResponseEntity.ok(history);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * ดึงนักเรียนทั้งหมด (รวม inactive) ในคลาส Monthly - สำหรับ ViewAllClasses
     */
    @GetMapping("/monthly/{classId}/all-students")
    public ResponseEntity<List<EnrollmentResponseDto>> getAllMonthlyStudents(@PathVariable Long classId) {
        try {
            List<EnrollmentResponseDto> students = enrollmentService.getAllMonthlyStudents(classId);
            return ResponseEntity.ok(students);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * ดึงนักเรียนทั้งหมด (รวม inactive) ในคลาส Hourly Group - สำหรับ ViewAllClasses
     */
    @GetMapping("/hourly-group/{classId}/all-students")
    public ResponseEntity<List<EnrollmentResponseDto>> getAllHourlyGroupStudents(@PathVariable Long classId) {
        try {
            List<EnrollmentResponseDto> students = enrollmentService.getAllHourlyGroupStudents(classId);
            return ResponseEntity.ok(students);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * ดึงนักเรียนทั้งหมด (รวม inactive) ในคลาส Hourly Individual - สำหรับ ViewAllClasses
     */
    @GetMapping("/hourly-individual/{classId}/all-students")
    public ResponseEntity<List<EnrollmentResponseDto>> getAllHourlyIndividualStudents(@PathVariable Long classId) {
        try {
            List<EnrollmentResponseDto> students = enrollmentService.getAllHourlyIndividualStudents(classId);
            return ResponseEntity.ok(students);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * เพิ่มนักเรียนเข้าคลาสพร้อมระบุสถานะ isActive
     */
    @PostMapping("/monthly/with-status")
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR')")
    public ResponseEntity<?> enrollMonthlyWithStatus(@Valid @RequestBody EnrollmentWithStatusRequestDto request) {
        try {
            EnrollmentResponseDto response = enrollmentService.enrollMonthlyWithStatus(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping("/hourly-group/with-status")
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR')")
    public ResponseEntity<?> enrollHourlyGroupWithStatus(@Valid @RequestBody EnrollmentWithStatusRequestDto request) {
        try {
            EnrollmentResponseDto response = enrollmentService.enrollHourlyGroupWithStatus(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}