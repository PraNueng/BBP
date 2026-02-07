package com.example.backend.dtos.tutor;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public class RemoveTutorsRequestDto {

    @NotEmpty(message = "Tutor IDs list cannot be empty")
    private List<Long> tutorIds;

    public RemoveTutorsRequestDto() {}

    public RemoveTutorsRequestDto(List<Long> tutorIds) {
        this.tutorIds = tutorIds;
    }

    public List<Long> getTutorIds() {
        return tutorIds;
    }

    public void setTutorIds(List<Long> tutorIds) {
        this.tutorIds = tutorIds;
    }
}