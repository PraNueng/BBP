package com.example.backend.controllers;

import com.example.backend.dtos.*;
import com.example.backend.entities.User;
import com.example.backend.repositories.UserRepository;
import com.example.backend.securities.JwtUtil;
import com.example.backend.services.MathFormHistoryService;
import com.example.backend.services.MathFormService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/math-forms")
@CrossOrigin(origins = "*")
public class MathFormController {

    @Autowired
    private MathFormService mathFormService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MathFormHistoryService historyService;

    /**
     * ดึง MathForm ทั้งหมด (สำหรับ Admin)
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<MathFormDto>> getAllMathForms(
            @RequestParam(required = false) Long tutorId,
            @RequestParam(required = false) Long classId) {

        List<MathFormDto> forms = mathFormService.getAllMathForms(tutorId, classId);
        return ResponseEntity.ok(forms);
    }

    /**
     * ดึง MathForm ของครูที่ login อยู่
     */
    @GetMapping("/my-forms")
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR')")
    public ResponseEntity<List<MathFormDto>> getMyMathForms(
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.substring(7);
        Long tutorId = jwtUtil.getUserIdFromToken(token);

        List<MathFormDto> forms = mathFormService.getMathFormsByTutor(tutorId);
        return ResponseEntity.ok(forms);
    }

    /**
     * ดึง MathForm ตาม ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR')")
    public ResponseEntity<?> getMathFormById(@PathVariable Long id) {
        try {
            MathFormDto form = mathFormService.getMathFormById(id);
            return ResponseEntity.ok(form);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    /**
     * สร้าง MathForm ใหม่
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR')")
    public ResponseEntity<?> createMathForm(
            @Valid @RequestBody CreateMathFormRequestDto request,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            Long tutorId = jwtUtil.getUserIdFromToken(token);

            MathFormDto created = mathFormService.createMathForm(request, tutorId);
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
     * แก้ไข MathForm
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR')")
    public ResponseEntity<?> updateMathForm(
            @PathVariable Long id,
            @Valid @RequestBody UpdateMathFormRequestDto request,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            Long tutorId = jwtUtil.getUserIdFromToken(token);

            MathFormDto updated = mathFormService.updateMathForm(id, request, tutorId);
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
     * ลบ MathForm
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR')")
    public ResponseEntity<?> deleteMathForm(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            Long tutorId = jwtUtil.getUserIdFromToken(token);
            String role = jwtUtil.getRoleFromToken(token);

            mathFormService.deleteMathForm(id, tutorId, role);

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
     * ดึง MathForm ตามวันที่
     */
    @GetMapping("/by-date")
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR')")
    public ResponseEntity<List<MathFormDto>> getMathFormsByDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam(required = false) Long tutorId) {

        List<MathFormDto> forms = mathFormService.getMathFormsByDateRange(startDate, endDate, tutorId);
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

        Map<String, Object> statistics = mathFormService.getTeachingStatistics(tutorId, startDate, endDate);
        return ResponseEntity.ok(statistics);
    }

    /**
     * ดึงประวัติการแก้ไข MathForm
     */
    @GetMapping("/{id}/history")
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR')")
    public ResponseEntity<?> getMathFormHistory(@PathVariable Long id) {
        try {
            List<MathFormHistoryDto> history = historyService.getMathFormHistory(id);
            return ResponseEntity.ok(history);
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
}