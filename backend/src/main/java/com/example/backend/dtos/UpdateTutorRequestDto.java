package com.example.backend.dtos;

import jakarta.validation.constraints.Size;

public class UpdateTutorRequestDto {
    @Size(max = 100, message = "Nickname must not exceed 100 characters")
    private String nickname;

    @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
    private String password;

    private Boolean isActive;

    // Constructors
    public UpdateTutorRequestDto() {}

    // Getters and Setters
    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}