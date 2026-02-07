package com.example.backend.services;

import com.example.backend.dtos.hourform.HourFormHistoryDto;
import com.example.backend.entities.User;
import com.example.backend.entities.hourform.HourForm;
import com.example.backend.entities.hourform.HourFormHistory;
import com.example.backend.repositories.hourform.HourFormHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class HourFormHistoryService {

    @Autowired
    private HourFormHistoryRepository historyRepository;

    /**
     * บันทึกประวัติการแก้ไข
     */
    public void recordChange(HourForm hourForm, String fieldName, String oldValue, String newValue, User updatedBy) {
        // ถ้าค่าเท่ากัน ไม่ต้องบันทึก
        if ((oldValue == null && newValue == null) ||
                (oldValue != null && oldValue.equals(newValue))) {
            return;
        }

        HourFormHistory history = new HourFormHistory();
        history.setHourForm(hourForm);
        history.setFieldName(fieldName);
        history.setOldValue(oldValue);
        history.setNewValue(newValue);
        history.setUpdatedBy(updatedBy);
        history.setAction("UPDATE");

        historyRepository.save(history);
    }

    /**
     * ดึงประวัติการแก้ไขของ HourForm
     */
    @Transactional(readOnly = true)
    public List<HourFormHistoryDto> getHourFormHistory(Long hourFormId) {
        return historyRepository.findByHourFormIdOrderByUpdatedAtDesc(hourFormId)
                .stream()
                .map(HourFormHistoryDto::fromEntity)
                .collect(Collectors.toList());
    }
}