package com.example.backend.dtos;

import com.example.backend.entities.User;

public class UserDto {
    private Long id;
    private String username;
    private String role;
    private String nickname;
    private Boolean isActive;

    public UserDto() {}

    public UserDto(Long id, String username, String role, String nickname, Boolean isActive) {
        this.id = id;
        this.username = username;
        this.role = role;
        this.nickname = nickname;
        this.isActive = isActive;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    // Static factory method
    public static UserDto fromUser(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setRole(user.getRole());
        dto.setNickname(user.getNickname());
        dto.setIsActive(user.getIsActive());
        return dto;
    }
}