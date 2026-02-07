package com.example.backend.dtos.tutor;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class AssignTutorsRequestDto {

    @NotEmpty(message = "Tutor IDs list cannot be empty")
    private List<Long> tutorIds;

    public AssignTutorsRequestDto() {}

    public AssignTutorsRequestDto(List<Long> tutorIds) {
        this.tutorIds = tutorIds;
    }

    public List<Long> getTutorIds() {
        return tutorIds;
    }

    public void setTutorIds(List<Long> tutorIds) {
        this.tutorIds = tutorIds;
    }
}