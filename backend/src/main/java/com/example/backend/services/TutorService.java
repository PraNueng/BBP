package com.example.backend.services;

import com.example.backend.dtos.CreateTutorRequestDto;
import com.example.backend.dtos.tutor.TutorDto;
import com.example.backend.dtos.UpdateTutorRequestDto;
import com.example.backend.entities.Subject;
import com.example.backend.entities.User;
import com.example.backend.entities.monthly.MonthlyClassTutor;
import com.example.backend.entities.hourly.HourlyGroupClassTutor;
import com.example.backend.entities.hourly.HourlyIndividualClassTutor;
import com.example.backend.repositories.TutorRepository;
import com.example.backend.repositories.SubjectRepository;
import com.example.backend.repositories.monthly.MonthlyClassTutorRepository;
import com.example.backend.repositories.hourly.HourlyGroupClassTutorRepository;
import com.example.backend.repositories.hourly.HourlyIndividualClassTutorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TutorService {

    @Autowired
    private TutorRepository tutorRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ✅ เพิ่ม Repositories สำหรับ junction tables
    @Autowired
    private MonthlyClassTutorRepository monthlyClassTutorRepository;

    @Autowired
    private HourlyGroupClassTutorRepository hourlyGroupClassTutorRepository;

    @Autowired
    private HourlyIndividualClassTutorRepository hourlyIndividualClassTutorRepository;

    // Get all tutors with statistics
    @Transactional(readOnly = true)
    public List<TutorDto> getAllTutors() {
        List<User> tutors = tutorRepository.findByRoleOrderByCreatedAtDesc("TUTOR");
        return tutors.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Get active tutors only
    @Transactional(readOnly = true)
    public List<TutorDto> getActiveTutors() {
        List<User> tutors = tutorRepository.findActiveTeachers(true);
        return tutors.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Get tutor by ID
    @Transactional(readOnly = true)
    public TutorDto getTutorById(Long id) {
        User tutor = tutorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tutor not found with id: " + id));

        if (!"TUTOR".equalsIgnoreCase(tutor.getRole())) {
            throw new RuntimeException("User is not a tutor");
        }

        return convertToDto(tutor);
    }

    // Search tutors
    @Transactional(readOnly = true)
    public List<TutorDto> searchTutors(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllTutors();
        }

        List<User> tutors = tutorRepository.searchTutors(searchTerm.trim());
        return tutors.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Create new tutor
    @Transactional
    public TutorDto createTutor(CreateTutorRequestDto request) {
        // Check if username already exists
        if (tutorRepository.existsByUsernameAndRole(request.getUsername(), "TUTOR")) {
            throw new RuntimeException("Username already exists for a tutor");
        }

        User tutor = new User();
        tutor.setUsername(request.getUsername());
        tutor.setPassword(passwordEncoder.encode(request.getPassword()));
        tutor.setNickname(request.getNickname());
        tutor.setRole("tutor");
        tutor.setIsActive(true);

        User savedTutor = tutorRepository.save(tutor);
        return convertToDto(savedTutor);
    }

    // Update tutor
    @Transactional
    public TutorDto updateTutor(Long id, UpdateTutorRequestDto request) {
        User tutor = tutorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tutor not found with id: " + id));

        if (!"TUTOR".equalsIgnoreCase(tutor.getRole())) {
            throw new RuntimeException("User is not a tutor");
        }

        if (request.getNickname() != null) {
            tutor.setNickname(request.getNickname());
        }

        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            tutor.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        if (request.getIsActive() != null) {
            tutor.setIsActive(request.getIsActive());
        }

        User updatedTutor = tutorRepository.save(tutor);
        return convertToDto(updatedTutor);
    }

    // Toggle active status
    @Transactional
    public TutorDto toggleActive(Long id) {
        User tutor = tutorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tutor not found with id: " + id));

        if (!"TUTOR".equalsIgnoreCase(tutor.getRole())) {
            throw new RuntimeException("User is not a tutor");
        }

        tutor.setIsActive(!tutor.getIsActive());
        User updatedTutor = tutorRepository.save(tutor);
        return convertToDto(updatedTutor);
    }

    // Delete tutor (soft delete by setting inactive)
    @Transactional
    public void deleteTutor(Long id) {
        User tutor = tutorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tutor not found with id: " + id));

        if (!"TUTOR".equalsIgnoreCase(tutor.getRole())) {
            throw new RuntimeException("User is not a tutor");
        }

        // ✅ นับจำนวนคลาสที่ active จาก junction tables
        long monthlyCount = monthlyClassTutorRepository.findByTutorId(id).stream()
                .filter(ct -> Boolean.TRUE.equals(ct.getMonthlyClass().getIsActive()))
                .count();

        long hourlyGroupCount = hourlyGroupClassTutorRepository.findByTutorId(id).stream()
                .filter(ct -> Boolean.TRUE.equals(ct.getHourlyGroupClass().getIsActive()))
                .count();

        long hourlyIndividualCount = hourlyIndividualClassTutorRepository.findByTutorId(id).stream()
                .filter(ct -> Boolean.TRUE.equals(ct.getHourlyIndividualClass().getIsActive()))
                .count();

        long activeClassCount = monthlyCount + hourlyGroupCount + hourlyIndividualCount;

        if (activeClassCount > 0) {
            throw new RuntimeException("Cannot delete tutor with active classes. Please deactivate all classes first.");
        }

        // Soft delete
        tutor.setIsActive(false);
        tutorRepository.save(tutor);
    }

    // Convert User entity to TutorDto
    private TutorDto convertToDto(User tutor) {
        TutorDto dto = new TutorDto();
        dto.setId(tutor.getId());
        dto.setUsername(tutor.getUsername());
        dto.setNickname(tutor.getNickname());
        dto.setIsActive(tutor.getIsActive());
        dto.setCreatedAt(tutor.getCreatedAt());
        dto.setUpdatedAt(tutor.getUpdatedAt());

        // ดึงข้อมูลคลาสจาก junction tables
        List<MonthlyClassTutor> monthlyClasses = monthlyClassTutorRepository.findByTutorId(tutor.getId());
        List<HourlyGroupClassTutor> hourlyGroupClasses = hourlyGroupClassTutorRepository.findByTutorId(tutor.getId());
        List<HourlyIndividualClassTutor> hourlyIndividualClasses = hourlyIndividualClassTutorRepository.findByTutorId(tutor.getId());

        int monthlyCount = monthlyClasses.size();
        int hourlyGroupCount = hourlyGroupClasses.size();
        int hourlyIndividualCount = hourlyIndividualClasses.size();

        // นับคลาสที่ active
        int activeCount = (int) (
                monthlyClasses.stream()
                        .filter(ct -> Boolean.TRUE.equals(ct.getMonthlyClass().getIsActive()))
                        .count()
                        + hourlyGroupClasses.stream()
                        .filter(ct -> Boolean.TRUE.equals(ct.getHourlyGroupClass().getIsActive()))
                        .count()
                        + hourlyIndividualClasses.stream()
                        .filter(ct -> Boolean.TRUE.equals(ct.getHourlyIndividualClass().getIsActive()))
                        .count()
        );

        dto.setTotalMonthlyClasses(monthlyCount);
        dto.setTotalHourlyGroupClasses(hourlyGroupCount);
        dto.setTotalHourlyIndividualClasses(hourlyIndividualCount);
        dto.setTotalActiveClasses(activeCount);

        // Get unique subjects จาก junction tables
        Map<Long, TutorDto.SubjectDTO> subjectMap = new HashMap<>();

        monthlyClasses.forEach(ct -> {
            Subject subject = ct.getMonthlyClass().getSubject();
            subjectMap.computeIfAbsent(subject.getId(),
                            id -> new TutorDto.SubjectDTO(id, subject.getSubjectName(), 0))
                    .setClassCount(subjectMap.get(subject.getId()).getClassCount() + 1);
        });

        hourlyGroupClasses.forEach(ct -> {
            Subject subject = ct.getHourlyGroupClass().getSubject();
            subjectMap.computeIfAbsent(subject.getId(),
                            id -> new TutorDto.SubjectDTO(id, subject.getSubjectName(), 0))
                    .setClassCount(subjectMap.get(subject.getId()).getClassCount() + 1);
        });

        hourlyIndividualClasses.forEach(ct -> {
            Subject subject = ct.getHourlyIndividualClass().getSubject();
            subjectMap.computeIfAbsent(subject.getId(),
                            id -> new TutorDto.SubjectDTO(id, subject.getSubjectName(), 0))
                    .setClassCount(subjectMap.get(subject.getId()).getClassCount() + 1);
        });

        dto.setSubjects(new ArrayList<>(subjectMap.values()));

        return dto;
    }
}