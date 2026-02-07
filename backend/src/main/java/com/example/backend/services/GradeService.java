package com.example.backend.services;

import com.example.backend.dtos.GradeDto;
import com.example.backend.dtos.hourform.HourFormDto;
import com.example.backend.entities.Grade;
import com.example.backend.entities.student.Student;
import com.example.backend.repositories.GradeRepository;
import com.example.backend.repositories.student.StudentRepository;
import com.example.backend.repositories.hourform.HourFormRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GradeService {

    @Autowired
    private GradeRepository gradeRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private HourFormRepository hourFormRepository;

    /**
     * ดึงทุก Grade เรียงตาม gradeOrder
     */
    @Cacheable(value = "grades", key = "'all'")
    public List<GradeDto> getAllGrades() {
        return gradeRepository.findAllOrdered().stream()
                .map(GradeDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * ดึงเฉพาะ Grade ที่มีนักเรียน active อยู่
     */
    @Cacheable(value = "grades", key = "'with-students'")
    public List<GradeDto> getGradesWithActiveStudents() {
        List<Grade> allGrades = gradeRepository.findAllOrdered();

        return allGrades.stream()
                .filter(grade -> {
                    Long count = studentRepository.countByGradeIdAndActiveTrue(grade.getId());
                    return count != null && count > 0;
                })
                .map(GradeDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * ดึง Grade ตาม ID
     */
    public GradeDto getGradeById(Long id) {
        Grade grade = gradeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Grade not found"));
        return GradeDto.fromEntity(grade);
    }

    /**
     * ดึง Grade ตามชื่อ
     */
    public GradeDto getGradeByName(String gradeName) {
        Grade grade = gradeRepository.findByGradeName(gradeName)
                .orElseThrow(() -> new RuntimeException("Grade not found"));
        return GradeDto.fromEntity(grade);
    }

    /**
     * นับจำนวนนักเรียนในแต่ละ Grade
     */
    public Long countStudentsByGrade(Long gradeId) {
        return studentRepository.countByGradeIdAndActiveTrue(gradeId);
    }

    /**
     * ดึงประวัติการเรียนของนักเรียนตามชื่อและ grade
     */
    public List<HourFormDto> getStudentHistoryByName(String studentName, Long gradeId) {
        // แยกชื่อและนามสกุล
        String[] nameParts = parseStudentName(studentName);
        String firstName = nameParts[0];
        String lastName = nameParts[1];

        // หานักเรียนจากชื่อ
        Student student = studentRepository.findByFirstNameAndLastNameAndGradeId(firstName, lastName, gradeId)
                .orElseThrow(() -> new RuntimeException("Student not found: " + studentName + " in grade " + gradeId));

        // ดึงประวัติจาก hour_forms
        return hourFormRepository.findByStudentIdAndGradeId(student.getId(), gradeId).stream()
                .map(HourFormDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * แยกชื่อและนามสกุล
     */
    private String[] parseStudentName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new RuntimeException("Student name is required");
        }

        fullName = fullName.trim();
        int spaceIndex = fullName.indexOf(' ');

        if (spaceIndex > 0) {
            String firstName = fullName.substring(0, spaceIndex).trim();
            String lastName = fullName.substring(spaceIndex + 1).trim();
            return new String[]{firstName, lastName};
        } else {
            return new String[]{fullName, ""};
        }
    }
}