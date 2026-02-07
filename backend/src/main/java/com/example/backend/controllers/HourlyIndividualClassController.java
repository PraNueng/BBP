package com.example.backend.controllers;

import com.example.backend.dtos.hourly.CreateHourlyIndividualClassRequestDto;
import com.example.backend.dtos.hourly.HourlyIndividualClassDto;
import com.example.backend.dtos.hourly.UpdateHourlyIndividualClassRequestDto;
import com.example.backend.dtos.student.StudentDto;
import com.example.backend.dtos.tutor.TutorDto;
import com.example.backend.entities.hourly.HourlyIndividualClassStudent;
import com.example.backend.repositories.hourly.HourlyIndividualClassStudentRepository;
import com.example.backend.services.HourlyIndividualClassService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/hourly-individual-classes")
@CrossOrigin(origins = "*")
public class HourlyIndividualClassController {

    @Autowired
    private HourlyIndividualClassService hourlyIndividualClassService;

    @Autowired
    private HourlyIndividualClassStudentRepository hourlyIndividualClassStudentRepository;

    /**
     * ดึงรายการคลาสทั้งหมด - รองรับ filter tutorId
     */
    @GetMapping
    public ResponseEntity<List<HourlyIndividualClassDto>> getAllClasses(
            @RequestParam(required = false) Long tutorId,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) Long subjectId,
            @RequestParam(required = false) Long studentId) {
        try {
            List<HourlyIndividualClassDto> classes;

            // ลำดับความสำคัญ: studentId > tutorId > subjectId > active
            if (studentId != null) {
                classes = hourlyIndividualClassService.getClassesByStudent(studentId, active);
            } else if (tutorId != null) {
                // Filter ตาม tutorId
                classes = hourlyIndividualClassService.getAllClasses(tutorId, active);
            } else if (subjectId != null) {
                classes = hourlyIndividualClassService.getClassesBySubject(subjectId, active);
            } else if (active != null && active) {
                classes = hourlyIndividualClassService.getActiveClasses();
            } else {
                classes = hourlyIndividualClassService.getAllClasses(null, null);
            }

            return ResponseEntity.ok(classes);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * ดึงข้อมูลคลาสตาม ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getClassById(@PathVariable Long id) {
        try {
            HourlyIndividualClassDto classDto = hourlyIndividualClassService.getClassById(id);
            return ResponseEntity.ok(classDto);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * ดึงคลาสทั้งหมดของ student คนหนึ่ง
     */
    @GetMapping("/student/{studentId}")
    public ResponseEntity<?> getClassesByStudent(
            @PathVariable Long studentId,
            @RequestParam(required = false) Boolean active) {
        try {
            List<HourlyIndividualClassDto> classes = hourlyIndividualClassService.getClassesByStudent(studentId, active);
            return ResponseEntity.ok(classes);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Error fetching classes");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * สร้างคลาสใหม่
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR')")
    public ResponseEntity<?> createClass(@Valid @RequestBody CreateHourlyIndividualClassRequestDto request) {
        try {
            HourlyIndividualClassDto createdClass = hourlyIndividualClassService.createClass(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdClass);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("message", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * แก้ไขข้อมูลคลาส
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR')")
    public ResponseEntity<?> updateClass(
            @PathVariable Long id,
            @Valid @RequestBody UpdateHourlyIndividualClassRequestDto request) {
        try {
            HourlyIndividualClassDto updatedClass = hourlyIndividualClassService.updateClass(id, request);
            return ResponseEntity.ok(updatedClass);
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
     * เปิด/ปิดการใช้งานคลาส
     */
    @PatchMapping("/{id}/toggle-active")
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR')")
    public ResponseEntity<?> toggleActive(@PathVariable Long id) {
        try {
            HourlyIndividualClassDto updatedClass = hourlyIndividualClassService.toggleActive(id);
            return ResponseEntity.ok(updatedClass);
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
     * ลบคลาส
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR')")
    public ResponseEntity<?> deleteClass(@PathVariable Long id) {
        try {
            hourlyIndividualClassService.deleteClass(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Class deleted successfully");
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
     * เปิดการใช้งานคลาส
     */
    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR')")
    public ResponseEntity<?> activateClass(@PathVariable Long id) {
        try {
            hourlyIndividualClassService.activateClass(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Class activated successfully");
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
     * ปิดการใช้งานคลาส
     */
    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR')")
    public ResponseEntity<?> deactivateClass(@PathVariable Long id) {
        try {
            hourlyIndividualClassService.deactivateClass(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Class deactivated successfully");
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
     * เพิ่มครูให้กับคลาส (single tutor)
     */
    @PatchMapping("/{classId}/assign-tutor/{tutorId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR')")
    public ResponseEntity<?> assignTutor(
            @PathVariable Long classId,
            @PathVariable Long tutorId) {
        try {
            HourlyIndividualClassDto updatedClass = hourlyIndividualClassService.assignTutor(classId, tutorId);
            return ResponseEntity.ok(Map.of(
                    "message", "Tutor assigned successfully",
                    "data", updatedClass
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * ดึงรายการครูที่สอนคลาสนี้
     */
    @GetMapping("/{id}/tutors")
    public ResponseEntity<List<TutorDto>> getClassTutors(@PathVariable Long id) {
        try {
            List<TutorDto> tutors = hourlyIndividualClassService.getClassTutors(id);
            return ResponseEntity.ok(tutors);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
        }
    }

    /**
     * เพิ่มครูเข้าคลาส
     */
    @PostMapping("/{id}/tutors")
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR')")
    public ResponseEntity<?> addTutors(
            @PathVariable Long id,
            @RequestBody Map<String, List<Long>> request) {
        try {
            List<Long> tutorIds = request.get("tutorIds");
            if (tutorIds == null || tutorIds.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "tutorIds is required"));
            }

            hourlyIndividualClassService.addTutorsToClass(id, tutorIds);
            return ResponseEntity.ok(Map.of("message", "Tutors added successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * ลบครูออกจากคลาส
     */
    @DeleteMapping("/{id}/tutors")
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR')")
    public ResponseEntity<?> removeTutors(
            @PathVariable Long id,
            @RequestBody Map<String, List<Long>> request) {
        try {
            List<Long> tutorIds = request.get("tutorIds");
            if (tutorIds == null || tutorIds.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "tutorIds is required"));
            }

            hourlyIndividualClassService.removeTutorsFromClass(id, tutorIds);
            return ResponseEntity.ok(Map.of("message", "Tutors removed successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * ดึงรายชื่อนักเรียนทั้งหมดในคลาส
     * GET /api/hourly-individual-classes/{classId}/students
     */
    @GetMapping("/{classId}/students")
    public ResponseEntity<?> getClassStudents(@PathVariable Long classId) {
        try {
            List<StudentDto> students = hourlyIndividualClassService.getClassStudents(classId);

            // เพิ่ม enrollmentId เข้าไปใน response
            List<Map<String, Object>> studentsWithEnrollmentId = students.stream()
                    .map(dto -> {
                        Map<String, Object> result = new HashMap<>();

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

                        // หา enrollmentId
                        List<HourlyIndividualClassStudent> enrollments =
                                hourlyIndividualClassStudentRepository.findByClassIdAndActive(classId);

                        enrollments.stream()
                                .filter(e -> e.getStudent().getId().equals(dto.getId()))
                                .findFirst()
                                .ifPresent(enrollment -> result.put("enrollmentId", enrollment.getId()));

                        return result;
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(studentsWithEnrollmentId);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        }
    }
}