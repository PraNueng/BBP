package com.example.backend.services;

import com.example.backend.constants.Role;
import com.example.backend.dtos.CreateUserRequestDto;
import com.example.backend.dtos.UpdateUserRequestDto;
import com.example.backend.dtos.UserDto;
import com.example.backend.entities.User;
import com.example.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@CacheConfig(cacheNames = "users")
public class UserService {

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // ===== CREATE =====
    @Transactional
    @CacheEvict(allEntries = true)
    public UserDto createUser(CreateUserRequestDto dto, String creatorUsername) {
        // Validate creator is admin
        User creator = userRepository.findByUsernameAndIsActiveTrue(creatorUsername)
                .orElseThrow(() -> new RuntimeException("Creator not found"));

        if (!Role.isAdmin(creator.getRole())) {
            throw new RuntimeException("Only admin can create users");
        }

        // Validate input
        if (dto.getUsername() == null || dto.getUsername().trim().isEmpty()) {
            throw new RuntimeException("Username is required");
        }
        if (dto.getPassword() == null || dto.getPassword().length() < 6) {
            throw new RuntimeException("Password must be at least 6 characters");
        }
        if (dto.getRole() == null || !Role.isValid(dto.getRole())) {
            throw new RuntimeException("Invalid role. Must be 'admin' or 'tutor'");
        }

        // Check if username exists
        if (userRepository.existsByUsername(dto.getUsername().trim())) {
            throw new RuntimeException("Username '" + dto.getUsername() + "' already exists");
        }

        User user = new User();
        user.setUsername(dto.getUsername().trim());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(Role.normalize(dto.getRole()));
        user.setNickname(dto.getNickname());
        user.setIsActive(true);

        User saved = userRepository.save(user);
        return UserDto.fromUser(saved);
    }

    // ===== READ =====
    @Cacheable(key = "'all-active'")
    public List<UserDto> getAllActiveUsers() {
        return userRepository.findByIsActiveTrueOrderByNicknameAsc().stream()
                .map(UserDto::fromUser)
                .collect(Collectors.toList());
    }

    @Cacheable(key = "'role-' + #role")
    public List<UserDto> getUsersByRole(String role) {
        return userRepository.findByRoleAndIsActiveTrue(Role.normalize(role)).stream()
                .map(UserDto::fromUser)
                .collect(Collectors.toList());
    }

    @Cacheable(key = "'tutors'")
    public List<UserDto> getAllTutors() {
        return userRepository.findByRoleAndIsActiveTrue(Role.TUTOR).stream()
                .map(UserDto::fromUser)
                .collect(Collectors.toList());
    }

    @Cacheable(key = "'admins'")
    public List<UserDto> getAllAdmins() {
        return userRepository.findByRoleAndIsActiveTrue(Role.ADMIN).stream()
                .map(UserDto::fromUser)
                .collect(Collectors.toList());
    }

    @Cacheable(key = "'search-' + #search")
    public List<UserDto> searchUsers(String search) {
        if (search == null || search.trim().isEmpty()) {
            return getAllActiveUsers();
        }

        String searchTerm = "%" + search.trim().toLowerCase() + "%";
        return userRepository.findByIsActiveTrueAndUsernameContainingIgnoreCaseOrNicknameContainingIgnoreCase(
                        searchTerm, searchTerm
                ).stream()
                .map(UserDto::fromUser)
                .collect(Collectors.toList());
    }

    public List<UserDto> getUsersByRoleAndSearch(String role, String search) {
        String normalizedRole = Role.normalize(role);
        String searchTerm = search != null ? "%" + search.trim().toLowerCase() + "%" : "%%";

        return userRepository.findByRoleAndIsActiveTrueAndUsernameOrNicknameContaining(
                        normalizedRole, searchTerm, searchTerm
                ).stream()
                .map(UserDto::fromUser)
                .collect(Collectors.toList());
    }

    @Cacheable(key = "#id")
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return UserDto.fromUser(user);
    }

    @Cacheable(key = "'username-' + #username")
    public UserDto getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return UserDto.fromUser(user);
    }

    // ===== UPDATE =====
    @Transactional
    @CacheEvict(allEntries = true)
    public UserDto updateUser(Long id, UpdateUserRequestDto dto, String updaterUsername) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        User updater = userRepository.findByUsernameAndIsActiveTrue(updaterUsername)
                .orElseThrow(() -> new RuntimeException("Updater not found"));

        // Only admin can update other users, users can update themselves
        if (!Role.isAdmin(updater.getRole()) && !user.getId().equals(updater.getId())) {
            throw new RuntimeException("You are not authorized to update this user");
        }

        // Update username if changed
        if (dto.getUsername() != null && !dto.getUsername().equals(user.getUsername())) {
            if (userRepository.existsByUsernameAndIdNot(dto.getUsername(), id)) {
                throw new RuntimeException("Username '" + dto.getUsername() + "' already exists");
            }
            user.setUsername(dto.getUsername().trim());
        }

        // Update password if provided
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            if (dto.getPassword().length() < 6) {
                throw new RuntimeException("Password must be at least 6 characters");
            }
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        // Only admin can change role
        if (dto.getRole() != null && Role.isAdmin(updater.getRole())) {
            if (!Role.isValid(dto.getRole())) {
                throw new RuntimeException("Invalid role. Must be 'admin' or 'tutor'");
            }
            user.setRole(Role.normalize(dto.getRole()));
        }

        // Update nickname
        if (dto.getNickname() != null) {
            user.setNickname(dto.getNickname());
        }

        // Only admin can change active status
        if (dto.getIsActive() != null && Role.isAdmin(updater.getRole())) {
            user.setIsActive(dto.getIsActive());
        }

        User updated = userRepository.save(user);
        return UserDto.fromUser(updated);
    }

    // ===== DELETE (Soft Delete) =====
    @Transactional
    @CacheEvict(allEntries = true)
    public void deactivateUser(Long id, String adminUsername) {
        User admin = userRepository.findByUsernameAndIsActiveTrue(adminUsername)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        if (!Role.isAdmin(admin.getRole())) {
            throw new RuntimeException("Only admin can deactivate users");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Cannot deactivate yourself
        if (user.getId().equals(admin.getId())) {
            throw new RuntimeException("Cannot deactivate yourself");
        }

        user.setIsActive(false);
        userRepository.save(user);
    }

    @Transactional
    @CacheEvict(allEntries = true)
    public void activateUser(Long id, String adminUsername) {
        User admin = userRepository.findByUsernameAndIsActiveTrue(adminUsername)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        if (!Role.isAdmin(admin.getRole())) {
            throw new RuntimeException("Only admin can activate users");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setIsActive(true);
        userRepository.save(user);
    }

    // Hard Delete (for admin only)
    @Transactional
    @CacheEvict(allEntries = true)
    public void deleteUser(Long id, String adminUsername) {
        User admin = userRepository.findByUsernameAndIsActiveTrue(adminUsername)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        if (!Role.isAdmin(admin.getRole())) {
            throw new RuntimeException("Only admin can delete users");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Cannot delete yourself
        if (user.getId().equals(admin.getId())) {
            throw new RuntimeException("Cannot delete yourself");
        }

        userRepository.deleteById(id);
    }

    // ===== STATISTICS =====
    public Long countActiveUsers() {
        return userRepository.countByIsActiveTrue();
    }

    public Long countUsersByRole(String role) {
        return userRepository.countByRoleAndIsActiveTrue(Role.normalize(role));
    }

    // Note: ต้องสร้าง custom queries เหล่านี้ใน UserRepository
    public List<Object[]> getUsersWithStudentCount() {
        // ต้องสร้าง query ใน repository
        // @Query("SELECT u.id, u.nickname, COUNT(DISTINCT s.id) FROM User u ...")
        return userRepository.findUsersWithStudentCount();
    }

    public List<Object[]> getTutorsWithTotalHours() {
        // ต้องสร้าง query ใน repository
        // @Query("SELECT u.id, u.nickname, SUM(hf.hoursTaught) FROM User u ...")
        return userRepository.findTutorsWithTotalHours();
    }
}