package com.example.backend.controllers;

import com.example.backend.dtos.CreateTutorRequestDto;
import com.example.backend.dtos.tutor.TutorDto;
import com.example.backend.dtos.UpdateTutorRequestDto;
import com.example.backend.services.TutorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tutors")
@CrossOrigin(origins = "*")
public class TutorController {

    @Autowired
    private TutorService tutorService;

    // Get all tutors
    @GetMapping
    public ResponseEntity<List<TutorDto>> getAllTutors(
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) String search) {

        try {
            List<TutorDto> tutors;

            if (search != null && !search.trim().isEmpty()) {
                tutors = tutorService.searchTutors(search);
            } else if (active != null && active) {
                tutors = tutorService.getActiveTutors();
            } else {
                tutors = tutorService.getAllTutors();
            }

            return ResponseEntity.ok(tutors);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get tutor by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getTutorById(@PathVariable Long id) {
        try {
            TutorDto tutor = tutorService.getTutorById(id);
            return ResponseEntity.ok(tutor);
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

    // Create new tutor
    @PostMapping
    public ResponseEntity<?> createTutor(@Valid @RequestBody CreateTutorRequestDto request) {
        try {
            TutorDto tutor = tutorService.createTutor(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(tutor);
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

    // Update tutor
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTutor(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTutorRequestDto request) {
        try {
            TutorDto tutor = tutorService.updateTutor(id, request);
            return ResponseEntity.ok(tutor);
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

    // Toggle active status
    @PatchMapping("/{id}/toggle-active")
    public ResponseEntity<?> toggleActive(@PathVariable Long id) {
        try {
            TutorDto tutor = tutorService.toggleActive(id);
            return ResponseEntity.ok(tutor);
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

    // Delete tutor (soft delete)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTutor(@PathVariable Long id) {
        try {
            tutorService.deleteTutor(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Tutor deleted successfully");
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
}