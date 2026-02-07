package com.example.backend.services;

import com.example.backend.dtos.hourform.StudentHourFormOverrideHistoryDto;
import com.example.backend.entities.User;
import com.example.backend.entities.hourform.HourForm;
import com.example.backend.entities.hourform.StudentHourFormOverride;
import com.example.backend.entities.hourform.StudentHourFormOverrideHistory;
import com.example.backend.entities.student.Student;
import com.example.backend.repositories.hourform.StudentHourFormOverrideHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class StudentHourFormOverrideHistoryService {

    @Autowired
    private StudentHourFormOverrideHistoryRepository repository;

    public void recordChange(StudentHourFormOverride override, HourForm hourForm, Student student,
                             String fieldName, String oldValue, String newValue, User updatedBy) {
        if ((oldValue == null && newValue == null) ||
                (oldValue != null && oldValue.equals(newValue))) {
            return;
        }

        StudentHourFormOverrideHistory history = new StudentHourFormOverrideHistory();
        history.setOverride(override);
        history.setHourForm(hourForm);
        history.setStudent(student);
        history.setFieldName(fieldName);
        history.setOldValue(oldValue);
        history.setNewValue(newValue);
        history.setUpdatedBy(updatedBy);
        history.setAction("UPDATE");

        repository.save(history);
    }

    @Transactional(readOnly = true)
    public List<StudentHourFormOverrideHistoryDto> getHistory(Long hourFormId, Long studentId) {
        return repository.findByHourFormIdAndStudentIdOrderByUpdatedAtDesc(hourFormId, studentId)
                .stream()
                .map(StudentHourFormOverrideHistoryDto::fromEntity)
                .collect(Collectors.toList());
    }
}