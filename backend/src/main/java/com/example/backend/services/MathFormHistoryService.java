package com.example.backend.services;

import com.example.backend.dtos.MathFormHistoryDto;
import com.example.backend.entities.User;
import com.example.backend.entities.MathForm;
import com.example.backend.entities.MathFormHistory;
import com.example.backend.repositories.MathFormHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MathFormHistoryService {

    @Autowired
    private MathFormHistoryRepository historyRepository;

    /**
     * บันทึกประวัติการแก้ไข
     */
    public void recordChange(MathForm mathForm, String fieldName, String oldValue, String newValue, User updatedBy) {
        // ถ้าค่าเท่ากัน ไม่ต้องบันทึก
        if ((oldValue == null && newValue == null) ||
                (oldValue != null && oldValue.equals(newValue))) {
            return;
        }

        MathFormHistory history = new MathFormHistory();
        history.setMathForm(mathForm);
        history.setFieldName(fieldName);
        history.setOldValue(oldValue);
        history.setNewValue(newValue);
        history.setUpdatedBy(updatedBy);
        history.setAction("UPDATE");

        historyRepository.save(history);
    }

    /**
     * ดึงประวัติการแก้ไขของ MathForm
     */
    @Transactional(readOnly = true)
    public List<MathFormHistoryDto> getMathFormHistory(Long mathFormId) {
        return historyRepository.findByMathFormIdOrderByUpdatedAtDesc(mathFormId)
                .stream()
                .map(MathFormHistoryDto::fromEntity)
                .collect(Collectors.toList());
    }
}