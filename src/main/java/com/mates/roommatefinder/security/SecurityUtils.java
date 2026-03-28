package com.mates.roommatefinder.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.mates.roommatefinder.model.User;

/**
 * Utility class for accessing security context information
 * such as the currently authenticated user.
 */
@Component
public class SecurityUtils {

    /**
     * Get the currently authenticated user
     * @return the authenticated User object, or null if not authenticated
     */
    public static User getCurrentUser() {
        Object authentication = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (authentication instanceof User) {
            return (User) authentication;
        }
        return null;
    }

    /**
     * Get the ID of the currently authenticated user
     * @return the user ID, or null if not authenticated
     */
    public static Long getCurrentUserId() {
        User user = getCurrentUser();
        return user != null ? user.getId() : null;
    }

    /**
     * Check if the given user ID matches the currently authenticated user
     * @param userId the user ID to check
     * @return true if the user ID matches the current user, false otherwise
     */
    public static boolean isCurrentUser(Long userId) {
        Long currentUserId = getCurrentUserId();
        return currentUserId != null && currentUserId.equals(userId);
    }

    /**
     * Check if a user is authenticated
     * @return true if a user is authenticated, false otherwise
     */
    public static boolean isAuthenticated() {
        return getCurrentUser() != null;
    }
}
