package com.example.backend.services;

import com.example.backend.constants.Role;
import com.example.backend.dtos.LoginRequestDto;
import com.example.backend.dtos.LoginResponseDto;
import com.example.backend.dtos.UserDto;
import com.example.backend.entities.User;
import com.example.backend.repositories.UserRepository;
import com.example.backend.securities.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * Login user with username and password
     */
    @Transactional(readOnly = true)
    public LoginResponseDto login(LoginRequestDto request) {
        // Validate input
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw new RuntimeException("Username is required");
        }
        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            throw new RuntimeException("Password is required");
        }

        // Find active user
        User user = userRepository.findByUsernameAndIsActiveTrue(request.getUsername().trim())
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        // Generate JWT token with userId
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole(), user.getId());

        // Create userDTO
        UserDto userDto = UserDto.fromUser(user);

        return new LoginResponseDto(token, userDto);
    }

    /**
     * Validate if user exists and is active
     */
    @Transactional(readOnly = true)
    public boolean validateUser(String username) {
        return userRepository.findByUsernameAndIsActiveTrue(username).isPresent();
    }

    /**
     * Get user info by username
     */
    @Transactional(readOnly = true)
    public UserDto getUserInfo(String username) {
        User user = userRepository.findByUsernameAndIsActiveTrue(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return UserDto.fromUser(user);
    }

    /**
     * Change password (for current user)
     */
    @Transactional
    public void changePassword(String username, String oldPassword, String newPassword) {
        User user = userRepository.findByUsernameAndIsActiveTrue(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Verify old password
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("Old password is incorrect");
        }

        // Validate new password
        if (newPassword == null || newPassword.length() < 6) {
            throw new RuntimeException("New password must be at least 6 characters");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    /**
     * Reset password (for admin only)
     */
    @Transactional
    public void resetPassword(Long userId, String newPassword, String adminUsername) {
        // Verify admin
        User admin = userRepository.findByUsernameAndIsActiveTrue(adminUsername)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        // Fixed: ใช้ Role.isAdmin() แทน Role.ADMIN
        if (!Role.isAdmin(admin.getRole())) {
            throw new RuntimeException("Only admin can reset passwords");
        }

        // Find target user
        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Validate new password
        if (newPassword == null || newPassword.length() < 6) {
            throw new RuntimeException("New password must be at least 6 characters");
        }

        // Update password
        targetUser.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(targetUser);
    }
}