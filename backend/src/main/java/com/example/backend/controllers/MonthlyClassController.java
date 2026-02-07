package com.example.backend.controllers;

import com.example.backend.dtos.student.StudentDto;
import com.example.backend.dtos.tutor.AssignTutorsRequestDto;
import com.example.backend.dtos.tutor.RemoveTutorsRequestDto;
import com.example.backend.dtos.monthly.CreateMonthlyClassRequestDto;
import com.example.backend.dtos.monthly.MonthlyClassDto;
import com.example.backend.dtos.monthly.UpdateMonthlyClassRequestDto;
import com.example.backend.dtos.tutor.TutorDto;
import com.example.backend.entities.User;
import com.example.backend.entities.student.Student;
import com.example.backend.repositories.monthly.MonthlyClassRepository;
import com.example.backend.repositories.monthly.MonthlyEnrollmentRepository;
import com.example.backend.services.MonthlyClassService;
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
@RequestMapping("/api/monthly-classes")
@CrossOrigin(origins = "*")
public class MonthlyClassController {

    @Autowired
    private MonthlyClassService monthlyClassService;

    @Autowired
    private MonthlyEnrollmentRepository monthlyEnrollmentRepository;

    /**
     * ดึงรายการคลาสทั้งหมด
     */
    @GetMapping
    public ResponseEntity<List<MonthlyClassDto>> getAllClasses(
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) Long tutorId,
            @RequestParam(required = false) Long subjectId) {
        try {
            List<MonthlyClassDto> classes;

            if (tutorId != null) {
                classes = monthlyClassService.getClassesByTutor(tutorId, active);
            } else if (subjectId != null) {
                classes = monthlyClassService.getClassesBySubject(subjectId, active);
            } else if (active != null && active) {
                classes = monthlyClassService.getActiveClasses();
            } else {
                classes = monthlyClassService.getAllClasses();
            }

            return ResponseEntity.ok(classes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * ดึงข้อมูลคลาสตาม ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getClassById(@PathVariable Long id) {
        try {
            MonthlyClassDto classDto = monthlyClassService.getClassById(id);
            return ResponseEntity.ok(classDto);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * สร้างคลาสใหม่
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR')")
    public ResponseEntity<?> createClass(@Valid @RequestBody CreateMonthlyClassRequestDto request) {
        try {
            MonthlyClassDto createdClass = monthlyClassService.createClass(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdClass);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * แก้ไขข้อมูลคลาส
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR')")
    public ResponseEntity<?> updateClass(
            @PathVariable Long id,
            @Valid @RequestBody UpdateMonthlyClassRequestDto request) {
        try {
            MonthlyClassDto updatedClass = monthlyClassService.updateClass(id, request);
            return ResponseEntity.ok(updatedClass);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * เปิด/ปิดการใช้งานคลาส
     */
    @PatchMapping("/{id}/toggle-active")
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR')")
    public ResponseEntity<?> toggleActive(@PathVariable Long id) {
        try {
            MonthlyClassDto updatedClass = monthlyClassService.toggleActive(id);
            return ResponseEntity.ok(updatedClass);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * ลบคลาส
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR')")
    public ResponseEntity<?> deleteClass(@PathVariable Long id) {
        try {
            monthlyClassService.deleteClass(id);
            return ResponseEntity.ok(Map.of("message", "Class deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    // ============================================
    // TUTOR MANAGEMENT ENDPOINTS (NEW)
    // ============================================

    /**
     * เพิ่มครูหลายคนให้กับคลาส
     * POST /api/monthly-classes/{classId}/tutors
     * Body: { "tutorIds": [1, 2, 3] }
     */
    @PostMapping("/{classId}/tutors")
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR')")
    public ResponseEntity<?> assignTutors(
            @PathVariable Long classId,
            @Valid @RequestBody AssignTutorsRequestDto request) {
        try {
            MonthlyClassDto updatedClass = monthlyClassService.assignTutors(classId, request);
            return ResponseEntity.ok(Map.of(
                    "message", "Tutors assigned successfully",
                    "data", updatedClass
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * ลบครูหลายคนออกจากคลาส
     * DELETE /api/monthly-classes/{classId}/tutors
     * Body: { "tutorIds": [1, 2] }
     */
    @DeleteMapping("/{classId}/tutors")
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR')")
    public ResponseEntity<?> removeTutors(
            @PathVariable Long classId,
            @RequestBody RemoveTutorsRequestDto request) {
        try {
            System.out.println("DEBUG: Removing tutors from class " + classId);
            System.out.println("DEBUG: Tutor IDs: " + request.getTutorIds());

            MonthlyClassDto updatedClass = monthlyClassService.removeTutors(classId, request);
            return ResponseEntity.ok(Map.of(
                    "message", "Tutors removed successfully",
                    "data", updatedClass
            ));
        } catch (RuntimeException e) {
            System.err.println("DEBUG: Error removing tutors: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * แทนที่ครูทั้งหมดด้วยรายการใหม่
     * PUT /api/monthly-classes/{classId}/tutors
     * Body: { "tutorIds": [3, 4, 5] }
     */
    @PutMapping("/{classId}/tutors")
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR')")
    public ResponseEntity<?> replaceTutors(
            @PathVariable Long classId,
            @Valid @RequestBody AssignTutorsRequestDto request) {
        try {
            MonthlyClassDto updatedClass = monthlyClassService.replaceTutors(classId, request);
            return ResponseEntity.ok(Map.of(
                    "message", "Tutors replaced successfully",
                    "data", updatedClass
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * ดึงรายชื่อครูทั้งหมดในคลาส
     * GET /api/monthly-classes/{classId}/tutors
     */
    @GetMapping("/{classId}/tutors")
    public ResponseEntity<?> getClassTutors(@PathVariable Long classId) {
        try {
            List<User> tutors = monthlyClassService.getClassTutors(classId);
            List<TutorDto> tutorDtos = tutors.stream()
                    .map(TutorDto::fromUser)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(tutorDtos);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    // ============================================
    // BACKWARD COMPATIBILITY ENDPOINTS
    // ============================================

    /**
     * เพิ่มครู 1 คน (เก่า - ยังใช้ได้)
     * PATCH /api/monthly-classes/{classId}/assign-tutor/{tutorId}
     */
    @PatchMapping("/{classId}/assign-tutor/{tutorId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR')")
    public ResponseEntity<?> assignTutor(
            @PathVariable Long classId,
            @PathVariable Long tutorId) {
        try {
            MonthlyClassDto updatedClass = monthlyClassService.assignTutor(classId, tutorId);
            return ResponseEntity.ok(updatedClass);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * ลบครู 1 คน (เก่า - ยังใช้ได้)
     * PATCH /api/monthly-classes/{classId}/remove-tutor/{tutorId}
     */
    @PatchMapping("/{classId}/remove-tutor/{tutorId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR')")
    public ResponseEntity<?> removeTutor(
            @PathVariable Long classId,
            @PathVariable Long tutorId) {
        try {
            MonthlyClassDto updatedClass = monthlyClassService.removeTutor(classId, tutorId);
            return ResponseEntity.ok(Map.of(
                    "message", "Tutor removed successfully",
                    "data", updatedClass
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * ดึงรายชื่อนักเรียนทั้งหมดในคลาส
     * GET /api/monthly-classes/{classId}/students
     */
    @GetMapping("/{classId}/students")
    public ResponseEntity<?> getClassStudents(@PathVariable Long classId) {
        try {
            List<Student> students = monthlyClassService.getClassStudents(classId);

            // แปลง Student เป็น Map พร้อม enrollmentId (เหมือน HourlyGroupClassController)
            List<Map<String, Object>> studentDtos = students.stream()
                    .map(student -> {
                        StudentDto dto = StudentDto.fromEntity(student);
                        Map<String, Object> result = new HashMap<>();

                        // Copy ข้อมูลจาก StudentDto
                        result.put("id", dto.getId());
                        result.put("firstName", dto.getFirstName());
                        result.put("lastName", dto.getLastName());
                        result.put("nickname", dto.getNickname());
                        result.put("studentCode", dto.getStudentCode());
                        result.put("phoneStudent", dto.getPhoneStudent());
                        result.put("phoneParent", dto.getPhoneParent());
                        result.put("schoolName", dto.getSchoolName());
                        result.put("studyPlan", dto.getStudyPlan());
                        result.put("gradeId", dto.getGradeId());
                        result.put("gradeName", dto.getGradeName());
                        result.put("isActive", dto.getIsActive());

                        // หา enrollmentId จาก MonthlyEnrollment
                        monthlyEnrollmentRepository
                                .findByClassIdAndStudentId(classId, student.getId())
                                .ifPresent(enrollment -> result.put("enrollmentId", enrollment.getId()));

                        return result;
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(studentDtos);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        }
    }
}