package com.example.backend.services;

import com.example.backend.dtos.ScienceFormHistoryDto;
import com.example.backend.entities.User;
import com.example.backend.entities.ScienceForm;
import com.example.backend.entities.ScienceFormHistory;
import com.example.backend.repositories.ScienceFormHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ScienceFormHistoryService {

    @Autowired
    private ScienceFormHistoryRepository historyRepository;

    /**
     * บันทึกประวัติการแก้ไข
     */
    public void recordChange(ScienceForm scienceForm, String fieldName, String oldValue, String newValue, User updatedBy) {
        // ถ้าค่าเท่ากัน ไม่ต้องบันทึก
        if ((oldValue == null && newValue == null) ||
                (oldValue != null && oldValue.equals(newValue))) {
            return;
        }

        ScienceFormHistory history = new ScienceFormHistory();
        history.setScienceForm(scienceForm);
        history.setFieldName(fieldName);
        history.setOldValue(oldValue);
        history.setNewValue(newValue);
        history.setUpdatedBy(updatedBy);
        history.setAction("UPDATE");

        historyRepository.save(history);
    }

    /**
     * ดึงประวัติการแก้ไขของ ScienceForm
     */
    @Transactional(readOnly = true)
    public List<ScienceFormHistoryDto> getScienceFormHistory(Long scienceFormId) {
        return historyRepository.findByScienceFormIdOrderByUpdatedAtDesc(scienceFormId)
                .stream()
                .map(ScienceFormHistoryDto::fromEntity)
                .collect(Collectors.toList());
    }
}