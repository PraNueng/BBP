package com.example.backend.controllers;

import com.example.backend.dtos.hourform.*;
import com.example.backend.entities.User;
import com.example.backend.repositories.UserRepository;
import com.example.backend.securities.JwtUtil;
import com.example.backend.services.HourFormHistoryService;
import com.example.backend.services.HourFormService;
import com.example.backend.services.StudentHourFormOverrideHistoryService;
import com.example.backend.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/hour-forms")
@CrossOrigin(origins = "*")
public class HourFormController {

    @Autowired
    private HourFormService hourFormService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HourFormHistoryService historyService;

    @Autowired
    private StudentHourFormOverrideHistoryService studentOverrideHistoryService;

    /**
     * ดึง HourForm ทั้งหมด (สำหรับ Admin)
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<HourFormDto>> getAllHourForms(
            @RequestParam(required = false) Long tutorId,
            @RequestParam(required = false) Long classId,
            @RequestParam(required = false) String classType) {

        List<HourFormDto> forms = hourFormService.getAllHourForms(tutorId, classId, classType);
        return ResponseEntity.ok(forms);
    }

    /**
     * ดึง HourForm ของครูที่ login อยู่
     */
    @GetMapping("/my-forms")
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR')")
    public ResponseEntity<List<HourFormDto>> getMyHourForms(
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.substring(7);
        Long tutorId = jwtUtil.getUserIdFromToken(token);

        List<HourFormDto> forms = hourFormService.getHourFormsByTutor(tutorId);
        return ResponseEntity.ok(forms);
    }

    /**
     * ดึง HourForm ตาม ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR')")
    public ResponseEntity<?> getHourFormById(@PathVariable Long id) {
        try {
            HourFormDto form = hourFormService.getHourFormById(id);
            return ResponseEntity.ok(form);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    /**
     * สร้าง HourForm ใหม่
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR')")
    public ResponseEntity<?> createHourForm(
            @Valid @RequestBody CreateHourFormRequestDto request,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            Long tutorId = jwtUtil.getUserIdFromToken(token);

            HourFormDto created = hourFormService.createHourForm(request, tutorId);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("message", "เกิดข้อผิดพลาดในการบันทึกข้อมูล");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * แก้ไข HourForm
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR')")
    public ResponseEntity<?> updateHourForm(
            @PathVariable Long id,
            @Valid @RequestBody UpdateHourFormRequestDto request,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            Long tutorId = jwtUtil.getUserIdFromToken(token);

            HourFormDto updated = hourFormService.updateHourForm(id, request, tutorId);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "เกิดข้อผิดพลาดในการแก้ไขข้อมูล");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * ลบ HourForm
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR')")
    public ResponseEntity<?> deleteHourForm(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            Long tutorId = jwtUtil.getUserIdFromToken(token);
            String role = jwtUtil.getRoleFromToken(token);

            hourFormService.deleteHourForm(id, tutorId, role);

            Map<String, String> response = new HashMap<>();
            response.put("message", "ลบข้อมูลสำเร็จ");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * ดึง HourForm ตามวันที่
     */
    @GetMapping("/by-date")
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR')")
    public ResponseEntity<List<HourFormDto>> getHourFormsByDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam(required = false) Long tutorId) {

        List<HourFormDto> forms = hourFormService.getHourFormsByDateRange(startDate, endDate, tutorId);
        return ResponseEntity.ok(forms);
    }

    /**
     * ดึงสถิติการสอนของครู
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR')")
    public ResponseEntity<?> getTeachingStatistics(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {

        String token = authHeader.substring(7);
        Long tutorId = jwtUtil.getUserIdFromToken(token);

        Map<String, Object> statistics = hourFormService.getTeachingStatistics(tutorId, startDate, endDate);
        return ResponseEntity.ok(statistics);
    }

    /**
     * ดึงประวัติการแก้ไข HourForm (Placeholder - ยังไม่ implement)
     */
    @GetMapping("/{id}/history")
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR')")
    public ResponseEntity<?> getHourFormHistory(@PathVariable Long id) {
        try {
            List<HourFormHistoryDto> history = historyService.getHourFormHistory(id);
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @PutMapping("/{hourFormId}/students/{studentId}/hours")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateStudentHours(
            @PathVariable Long hourFormId,
            @PathVariable Long studentId,
            @RequestBody Map<String, Object> request) {

        BigDecimal hours = new BigDecimal(request.get("hours").toString());
        String remark = (String) request.get("remark");
        hourFormService.updateStudentHoursOverride(hourFormId, studentId, hours, remark);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{hourFormId}/remark")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateFormRemark(
            @PathVariable Long hourFormId,
            @RequestBody Map<String, String> request) {

        String newRemark = request.get("remark");
        hourFormService.updateFormRemark(hourFormId, newRemark);
        return ResponseEntity.ok().build();
    }

    /**
     * Admin สร้าง HourForm ให้นักเรียนคนใดคนหนึ่ง (ไม่กระทบเพื่อนในคลาส)
     */
    @PostMapping("/admin/for-student")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createHourFormForStudent(
            @Valid @RequestBody CreateHourFormRequestDto request,
            @RequestParam Long studentId) {
        try {
            HourFormDto created = hourFormService.createHourFormForSpecificStudent(request, studentId);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("message", "เกิดข้อผิดพลาดในการบันทึกข้อมูล");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * ดึงรายชื่อคลาสที่นักเรียนเรียนอยู่ (สำหรับ dropdown)
     */
    @GetMapping("/student/{studentId}/classes")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getStudentActiveClasses(@PathVariable Long studentId) {
        try {
            List<Map<String, Object>> classes = hourFormService.getStudentActiveClasses(studentId);
            return ResponseEntity.ok(classes);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    /**
     * ดึงรายชื่อครูทั้งหมด (สำหรับ dropdown ใน Admin Form)
     */
    @GetMapping("/tutors")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllTutors() {
        try {
            List<User> tutors = userRepository.findByRoleAndIsActiveTrue("TUTOR");

            List<Map<String, Object>> tutorList = tutors.stream()
                    .map(tutor -> {
                        Map<String, Object> tutorData = new HashMap<>();
                        tutorData.put("id", tutor.getId());
                        tutorData.put("username", tutor.getUsername());
                        tutorData.put("nickname", tutor.getNickname());
                        return tutorData;
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(tutorList);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @GetMapping("/{hourFormId}/students/{studentId}/history")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getStudentOverrideHistory(
            @PathVariable Long hourFormId,
            @PathVariable Long studentId) {
        try {
            List<StudentHourFormOverrideHistoryDto> history =
                    studentOverrideHistoryService.getHistory(hourFormId, studentId);
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
}