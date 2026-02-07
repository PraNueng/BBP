package com.example.backend.services;

import com.example.backend.dtos.*;
import com.example.backend.entities.Subject;
import com.example.backend.entities.User;
import com.example.backend.entities.monthly.MonthlyClass;
import com.example.backend.entities.MathForm;
import com.example.backend.repositories.SubjectRepository;
import com.example.backend.repositories.UserRepository;
import com.example.backend.repositories.monthly.MonthlyClassRepository;
import com.example.backend.repositories.MathFormRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class MathFormService {

    @Autowired
    private MathFormRepository mathFormRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private MonthlyClassRepository monthlyClassRepository;

    @Autowired
    private MathFormHistoryService historyService;

    /**
     * ดึง MathForm ทั้งหมด (มี filter)
     */
    @Transactional(readOnly = true)
    public List<MathFormDto> getAllMathForms(Long tutorId, Long classId) {
        List<MathForm> forms;

        if (tutorId != null && classId != null) {
            forms = mathFormRepository.findByTutorIdAndMonthlyClassId(tutorId, classId);
        } else if (tutorId != null) {
            forms = mathFormRepository.findByTutorId(tutorId);
        } else if (classId != null) {
            forms = mathFormRepository.findByClassId(classId);
        } else {
            forms = mathFormRepository.findAll();
        }

        return forms.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * ดึง MathForm ของครูคนหนึ่ง
     */
    @Transactional(readOnly = true)
    public List<MathFormDto> getMathFormsByTutor(Long tutorId) {
        List<MathForm> forms = mathFormRepository.findByTutorId(tutorId);
        return forms.stream()
                .filter(f -> f.getIsAdminCreated() == null || !f.getIsAdminCreated())
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * ดึง MathForm ตาม ID
     */
    @Transactional(readOnly = true)
    public MathFormDto getMathFormById(Long id) {
        MathForm form = mathFormRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ไม่พบข้อมูลการสอน"));
        return convertToDto(form);
    }

    /**
     * สร้าง MathForm ใหม่
     */
    public MathFormDto createMathForm(CreateMathFormRequestDto request, Long tutorId) {
        // Validate tutor
        Long effectiveTutorId = request.getTutorId() != null ? request.getTutorId() : tutorId;
        User tutor = userRepository.findById(effectiveTutorId)
                .orElseThrow(() -> new RuntimeException("ไม่พบข้อมูลครู"));

        // Validate subject
        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new RuntimeException("ไม่พบข้อมูลวิชา"));

        // Validate class exists (ต้องเป็น MonthlyClass เท่านั้น)
        MonthlyClass monthlyClass = monthlyClassRepository.findById(request.getClassId())
                .orElseThrow(() -> new RuntimeException("ไม่พบคลาสรายเดือน"));

        // Create new MathForm
        MathForm form = new MathForm();
        form.setTutor(tutor);
        form.setMonthlyClass(monthlyClass);
        form.setSubject(subject);
        form.setContent(request.getContent());
        form.setTeachingDate(request.getTeachingDate());
        form.setHoursTaught(request.getHoursTaught());
        form.setStudentsPresent(request.getStudentsPresent());
        form.setStudentsAbsent(request.getStudentsAbsent());
        form.setRemark(request.getRemark());
        form.setCreatedBy(tutor);

        MathForm saved = mathFormRepository.save(form);

        // ไม่มีการ updateStudentHours() เพราะนี่คือคลาสรายเดือน (ไม่ตัดชั่วโมงคอร์ส)

        return convertToDto(saved);
    }

    /**
     * แก้ไข MathForm
     */
    /**
     * แก้ไข MathForm
     */
    public MathFormDto updateMathForm(Long id, UpdateMathFormRequestDto request, Long tutorId) {
        MathForm form = mathFormRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ไม่พบข้อมูลการสอน"));

        // ตรวจสอบสิทธิ์ (เฉพาะครูที่สร้างเท่านั้น)
        if (!form.getTutor().getId().equals(tutorId)) {
            throw new RuntimeException("คุณไม่มีสิทธิ์แก้ไขข้อมูลนี้");
        }

        User updatedBy = userRepository.findById(tutorId)
                .orElseThrow(() -> new RuntimeException("ไม่พบข้อมูลผู้ใช้"));

        // บันทึกประวัติการเปลี่ยนแปลง classId
        if (request.getClassId() != null && !request.getClassId().equals(form.getMonthlyClass().getId())) {
            String oldClassName = form.getMonthlyClass().getClassName();

            MonthlyClass newClass = monthlyClassRepository.findById(request.getClassId())
                    .orElseThrow(() -> new RuntimeException("ไม่พบคลาสที่เลือก"));

            historyService.recordChange(
                    form,
                    "className",
                    oldClassName,
                    newClass.getClassName(),
                    updatedBy
            );

            form.setMonthlyClass(newClass);

            // อัพเดท subjectId ถ้ามี
            if (request.getSubjectId() != null) {
                Subject newSubject = subjectRepository.findById(request.getSubjectId())
                        .orElseThrow(() -> new RuntimeException("ไม่พบวิชาที่เลือก"));
                form.setSubject(newSubject);
            }
        }

        // บันทึกประวัติการเปลี่ยนแปลง content
        if (request.getContent() != null && !request.getContent().equals(form.getContent())) {
            historyService.recordChange(form, "content", form.getContent(), request.getContent(), updatedBy);
            form.setContent(request.getContent());
        }

        // บันทึกประวัติการเปลี่ยนแปลง teachingDate
        if (request.getTeachingDate() != null && !request.getTeachingDate().equals(form.getTeachingDate())) {
            historyService.recordChange(
                    form,
                    "teachingDate",
                    form.getTeachingDate().toString(),
                    request.getTeachingDate().toString(),
                    updatedBy
            );
            form.setTeachingDate(request.getTeachingDate());
        }

        // บันทึกประวัติการเปลี่ยนแปลง hoursTaught
        if (request.getHoursTaught() != null && request.getHoursTaught().compareTo(form.getHoursTaught()) != 0) {
            historyService.recordChange(
                    form,
                    "hoursTaught",
                    form.getHoursTaught().toString(),
                    request.getHoursTaught().toString(),
                    updatedBy
            );
            form.setHoursTaught(request.getHoursTaught());
        }

        // บันทึกประวัติการเปลี่ยนแปลง studentsPresent
        if (request.getStudentsPresent() != null && !request.getStudentsPresent().equals(form.getStudentsPresent())) {
            historyService.recordChange(
                    form,
                    "studentsPresent",
                    String.valueOf(form.getStudentsPresent()),
                    String.valueOf(request.getStudentsPresent()),
                    updatedBy
            );
            form.setStudentsPresent(request.getStudentsPresent());
        }

        // บันทึกประวัติการเปลี่ยนแปลง studentsAbsent
        if (request.getStudentsAbsent() != null && !request.getStudentsAbsent().equals(form.getStudentsAbsent())) {
            historyService.recordChange(
                    form,
                    "studentsAbsent",
                    String.valueOf(form.getStudentsAbsent()),
                    String.valueOf(request.getStudentsAbsent()),
                    updatedBy
            );
            form.setStudentsAbsent(request.getStudentsAbsent());
        }

        // บันทึกประวัติการเปลี่ยนแปลง remark
        String oldRemark = form.getRemark() != null ? form.getRemark() : "";
        String newRemark = request.getRemark() != null ? request.getRemark() : "";
        if (!oldRemark.equals(newRemark)) {
            historyService.recordChange(form, "remark", oldRemark, newRemark, updatedBy);
            form.setRemark(request.getRemark());
        }

        MathForm updated = mathFormRepository.save(form);
        return convertToDto(updated);
    }

    /**
     * ลบ MathForm
     */
    public void deleteMathForm(Long id, Long tutorId, String role) {
        MathForm form = mathFormRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ไม่พบข้อมูลการสอน"));

        // ตรวจสอบสิทธิ์ (Admin ลบได้ทั้งหมด, Tutor ลบได้เฉพาะของตัวเอง)
        if (!role.equalsIgnoreCase("ADMIN") && !form.getTutor().getId().equals(tutorId)) {
            throw new RuntimeException("คุณไม่มีสิทธิ์ลบข้อมูลนี้");
        }

        mathFormRepository.delete(form);
    }

    /**
     * ดึง MathForm ตามช่วงวันที่
     */
    @Transactional(readOnly = true)
    public List<MathFormDto> getMathFormsByDateRange(String startDate, String endDate, Long tutorId) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);

        List<MathForm> forms;
        if (tutorId != null) {
            forms = mathFormRepository.findByTutorIdAndDateRange(tutorId, start, end);
        } else {
            forms = mathFormRepository.findByDateRange(start, end);
        }

        return forms.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * ดึงสถิติการสอน
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getTeachingStatistics(Long tutorId, String startDate, String endDate) {
        LocalDate start = startDate != null ? LocalDate.parse(startDate) : LocalDate.now().minusMonths(1);
        LocalDate end = endDate != null ? LocalDate.parse(endDate) : LocalDate.now();

        List<MathForm> forms = mathFormRepository.findByTutorIdAndDateRange(tutorId, start, end);

        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalForms", forms.size());
        statistics.put("totalHours", forms.stream()
                .map(MathForm::getHoursTaught)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        statistics.put("totalStudentsPresent", forms.stream()
                .mapToInt(MathForm::getStudentsPresent)
                .sum());
        statistics.put("totalStudentsAbsent", forms.stream()
                .mapToInt(MathForm::getStudentsAbsent)
                .sum());

        return statistics;
    }

    // ===== HELPER METHODS =====

    /**
     * แปลง Entity เป็น DTO พร้อมข้อมูลคลาส
     */
    private MathFormDto convertToDto(MathForm form) {
        MathFormDto dto = MathFormDto.fromEntity(form);

        dto.setClassName(form.getMonthlyClass().getClassName());
        dto.setGrade(form.getMonthlyClass().getGrade() != null
                ? form.getMonthlyClass().getGrade().getGradeName()
                : "-");

        return dto;
    }
}