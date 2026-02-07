package com.example.backend.controllers;

import com.example.backend.dtos.*;
import com.example.backend.entities.User;
import com.example.backend.repositories.UserRepository;
import com.example.backend.securities.JwtUtil;
import com.example.backend.services.ScienceFormHistoryService;
import com.example.backend.services.ScienceFormService;
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
@RequestMapping("/api/science-forms")
@CrossOrigin(origins = "*")
public class ScienceFormController {

    @Autowired
    private ScienceFormService scienceFormService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ScienceFormHistoryService historyService;

    /**
     * ดึง ScienceForm ทั้งหมด (สำหรับ Admin)
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ScienceFormDto>> getAllScienceForms(
            @RequestParam(required = false) Long tutorId,
            @RequestParam(required = false) Long classId) {

        List<ScienceFormDto> forms = scienceFormService.getAllScienceForms(tutorId, classId);
        return ResponseEntity.ok(forms);
    }

    /**
     * ดึง ScienceForm ของครูที่ login อยู่
     */
    @GetMapping("/my-forms")
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR')")
    public ResponseEntity<List<ScienceFormDto>> getMyScienceForms(
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.substring(7);
        Long tutorId = jwtUtil.getUserIdFromToken(token);

        List<ScienceFormDto> forms = scienceFormService.getScienceFormsByTutor(tutorId);
        return ResponseEntity.ok(forms);
    }

    /**
     * ดึง ScienceForm ตาม ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR')")
    public ResponseEntity<?> getScienceFormById(@PathVariable Long id) {
        try {
            ScienceFormDto form = scienceFormService.getScienceFormById(id);
            return ResponseEntity.ok(form);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    /**
     * สร้าง ScienceForm ใหม่
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR')")
    public ResponseEntity<?> createScienceForm(
            @Valid @RequestBody CreateScienceFormRequestDto request,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            Long tutorId = jwtUtil.getUserIdFromToken(token);

            ScienceFormDto created = scienceFormService.createScienceForm(request, tutorId);
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
     * แก้ไข ScienceForm
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR')")
    public ResponseEntity<?> updateScienceForm(
            @PathVariable Long id,
            @Valid @RequestBody UpdateScienceFormRequestDto request,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            Long tutorId = jwtUtil.getUserIdFromToken(token);

            ScienceFormDto updated = scienceFormService.updateScienceForm(id, request, tutorId);
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
     * ลบ ScienceForm
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR')")
    public ResponseEntity<?> deleteScienceForm(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            Long tutorId = jwtUtil.getUserIdFromToken(token);
            String role = jwtUtil.getRoleFromToken(token);

            scienceFormService.deleteScienceForm(id, tutorId, role);

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
     * ดึง ScienceForm ตามวันที่
     */
    @GetMapping("/by-date")
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR')")
    public ResponseEntity<List<ScienceFormDto>> getScienceFormsByDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam(required = false) Long tutorId) {

        List<ScienceFormDto> forms = scienceFormService.getScienceFormsByDateRange(startDate, endDate, tutorId);
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

        Map<String, Object> statistics = scienceFormService.getTeachingStatistics(tutorId, startDate, endDate);
        return ResponseEntity.ok(statistics);
    }

    /**
     * ดึงประวัติการแก้ไข ScienceForm
     */
    @GetMapping("/{id}/history")
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR')")
    public ResponseEntity<?> getScienceFormHistory(@PathVariable Long id) {
        try {
            List<ScienceFormHistoryDto> history = historyService.getScienceFormHistory(id);
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