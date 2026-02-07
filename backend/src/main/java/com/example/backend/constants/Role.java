package com.example.backend.constants;

/**
 * User role constants
 */
public final class Role {
    public static final String ADMIN = "admin";
    public static final String TUTOR = "tutor";

    // Prevent instantiation
    private Role() {
        throw new AssertionError("Cannot instantiate constants class");
    }

    /**
     * Validate if role is valid
     */
    public static boolean isValid(String role) {
        if (role == null) return false;
        return ADMIN.equalsIgnoreCase(role) || TUTOR.equalsIgnoreCase(role);
    }

    /**
     * Check if role is admin
     */
    public static boolean isAdmin(String role) {
        return ADMIN.equalsIgnoreCase(role);
    }

    /**
     * Check if role is tutor
     */
    public static boolean isTutor(String role) {
        return TUTOR.equalsIgnoreCase(role);
    }

    /**
     * Normalize role to lowercase
     */
    public static String normalize(String role) {
        if (role == null) return null;
        return role.toLowerCase();
    }
}