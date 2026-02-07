package com.example.backend.controllers;

import com.example.backend.dtos.hourly.CreateHourlyGroupClassRequestDto;
import com.example.backend.dtos.hourly.HourlyGroupClassDto;
import com.example.backend.dtos.hourly.UpdateHourlyGroupClassRequestDto;
import com.example.backend.dtos.student.StudentDto;
import com.example.backend.dtos.tutor.AssignTutorsRequestDto;
import com.example.backend.dtos.tutor.RemoveTutorsRequestDto;
import com.example.backend.dtos.tutor.TutorDto;
import com.example.backend.entities.User;
import com.example.backend.entities.student.Student;
import com.example.backend.repositories.hourly.HourlyGroupEnrollmentRepository;
import com.example.backend.services.HourlyGroupClassService;
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
@RequestMapping("/api/hourly-group-classes")
@CrossOrigin(origins = "*")
public class HourlyGroupClassController {

    @Autowired
    private HourlyGroupClassService hourlyGroupClassService;

    @Autowired
    private HourlyGroupEnrollmentRepository hourlyGroupEnrollmentRepository;

    /**
     * ดึงรายการคลาสทั้งหมด
     */
    @GetMapping
    public ResponseEntity<List<HourlyGroupClassDto>> getAllClasses(
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) Long tutorId,
            @RequestParam(required = false) Long subjectId) {
        try {
            List<HourlyGroupClassDto> classes;

            if (tutorId != null) {
                classes = hourlyGroupClassService.getClassesByTutor(tutorId, active);
            } else if (subjectId != null) {
                classes = hourlyGroupClassService.getClassesBySubject(subjectId, active);
            } else if (active != null && active) {
                classes = hourlyGroupClassService.getActiveClasses();
            } else {
                classes = hourlyGroupClassService.getAllClasses();
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
            HourlyGroupClassDto classDto = hourlyGroupClassService.getClassById(id);
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
     * ดึงคลาสทั้งหมดของ tutor คนหนึ่ง
     */
    @GetMapping("/tutor/{tutorId}")
    public ResponseEntity<?> getClassesByTutor(
            @PathVariable Long tutorId,
            @RequestParam(required = false) Boolean active) {
        try {
            List<HourlyGroupClassDto> classes = hourlyGroupClassService.getClassesByTutor(tutorId, active);
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
    public ResponseEntity<?> createClass(@Valid @RequestBody CreateHourlyGroupClassRequestDto request) {
        try {
            HourlyGroupClassDto createdClass = hourlyGroupClassService.createClass(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdClass);
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
     * แก้ไขข้อมูลคลาส
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR')")
    public ResponseEntity<?> updateClass(
            @PathVariable Long id,
            @Valid @RequestBody UpdateHourlyGroupClassRequestDto request) {
        try {
            HourlyGroupClassDto updatedClass = hourlyGroupClassService.updateClass(id, request);
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
            HourlyGroupClassDto updatedClass = hourlyGroupClassService.toggleActive(id);
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
            hourlyGroupClassService.deleteClass(id);
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
     * เพิ่มครูหลายคนให้กับคลาส
     * POST /api/hourly-group-classes/{classId}/tutors
     * Body: { "tutorIds": [1, 2, 3] }
     */
    @PostMapping("/{classId}/tutors")
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR')")
    public ResponseEntity<?> assignTutors(
            @PathVariable Long classId,
            @Valid @RequestBody AssignTutorsRequestDto request) {
        try {
            HourlyGroupClassDto updatedClass = hourlyGroupClassService.assignTutors(classId, request);
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
     * DELETE /api/hourly-group-classes/{classId}/tutors
     * Body: { "tutorIds": [1, 2] }
     */
    @DeleteMapping("/{classId}/tutors")
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR')")
    public ResponseEntity<?> removeTutors(
            @PathVariable Long classId,
            @RequestBody RemoveTutorsRequestDto request) {
        try {
            HourlyGroupClassDto updatedClass = hourlyGroupClassService.removeTutors(classId, request);
            return ResponseEntity.ok(Map.of(
                    "message", "Tutors removed successfully",
                    "data", updatedClass
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * แทนที่ครูทั้งหมดด้วยรายการใหม่
     * PUT /api/hourly-group-classes/{classId}/tutors
     * Body: { "tutorIds": [3, 4, 5] }
     */
    @PutMapping("/{classId}/tutors")
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR')")
    public ResponseEntity<?> replaceTutors(
            @PathVariable Long classId,
            @Valid @RequestBody AssignTutorsRequestDto request) {
        try {
            HourlyGroupClassDto updatedClass = hourlyGroupClassService.replaceTutors(classId, request);
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
     * GET /api/hourly-group-classes/{classId}/tutors
     */
    @GetMapping("/{classId}/tutors")
    public ResponseEntity<?> getClassTutors(@PathVariable Long classId) {
        try {
            List<User> tutors = hourlyGroupClassService.getClassTutors(classId);
            List<TutorDto> tutorDtos = tutors.stream()
                    .map(TutorDto::fromUser)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(tutorDtos);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * เพิ่มครู 1 คน (backward compatibility)
     * PATCH /api/hourly-group-classes/{classId}/assign-tutor/{tutorId}
     */
    @PatchMapping("/{classId}/assign-tutor/{tutorId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR')")
    public ResponseEntity<?> assignTutor(
            @PathVariable Long classId,
            @PathVariable Long tutorId) {
        try {
            HourlyGroupClassDto updatedClass = hourlyGroupClassService.assignTutor(classId, tutorId);
            return ResponseEntity.ok(updatedClass);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * ลบครู 1 คน (backward compatibility)
     * PATCH /api/hourly-group-classes/{classId}/remove-tutor/{tutorId}
     */
    @PatchMapping("/{classId}/remove-tutor/{tutorId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TUTOR')")
    public ResponseEntity<?> removeTutor(
            @PathVariable Long classId,
            @PathVariable Long tutorId) {
        try {
            HourlyGroupClassDto updatedClass = hourlyGroupClassService.removeTutor(classId, tutorId);
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
     * GET /api/hourly-group-classes/{classId}/students
     */
    @GetMapping("/{classId}/students")
    public ResponseEntity<?> getClassStudents(@PathVariable Long classId) {
        try {
            List<Student> students = hourlyGroupClassService.getClassStudents(classId);

            // แปลง Student เป็น Map พร้อม enrollmentId
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

                        // หา enrollmentId
                        hourlyGroupEnrollmentRepository
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