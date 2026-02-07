package com.example.backend.dtos.enrollment;

public class EnrollmentStatusChangeDto {
    private String reason;

    public EnrollmentStatusChangeDto() {}

    public EnrollmentStatusChangeDto(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}