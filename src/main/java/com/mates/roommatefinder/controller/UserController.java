package com.mates.roommatefinder.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mates.roommatefinder.model.User;
import com.mates.roommatefinder.repository.UserRepository;
import com.mates.roommatefinder.security.SecurityUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserRepository userRepository;

    @GetMapping("/test")
    public String test() {
        return "Controller reachable!";
    }

    /**
     * Get authenticated user's own profile
     */
    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser() {
        User currentUser = SecurityUtils.getCurrentUser();
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(currentUser);
    }

    /**
     * Get a specific user by ID (only owner or admin can access)
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        User currentUser = SecurityUtils.getCurrentUser();
        
        // Authorization check: user can only view their own profile
        if (currentUser == null || !currentUser.getId().equals(id)) {
            log.warn("Unauthorized access attempt: User {} tried to access User {}", 
                     SecurityUtils.getCurrentUserId(), id);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return userRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Update user profile (only owner can update)
     */
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        User currentUser = SecurityUtils.getCurrentUser();
        
        // Authorization check: user can only update their own profile
        if (currentUser == null || !currentUser.getId().equals(id)) {
            log.warn("Unauthorized update attempt: User {} tried to update User {}", 
                     SecurityUtils.getCurrentUserId(), id);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return userRepository.findById(id)
                .map(user -> {
                    if (userDetails.getUsername() != null) {
                        user.setUsername(userDetails.getUsername());
                    }
                    if (userDetails.getEmail() != null) {
                        user.setEmail(userDetails.getEmail());
                    }
                    // Don't update password through this endpoint for security
                    User updatedUser = userRepository.save(user);
                    return ResponseEntity.ok(updatedUser);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Delete user account (only owner can delete)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        User currentUser = SecurityUtils.getCurrentUser();
        
        // Authorization check: user can only delete their own account
        if (currentUser == null || !currentUser.getId().equals(id)) {
            log.warn("Unauthorized delete attempt: User {} tried to delete User {}", 
                     SecurityUtils.getCurrentUserId(), id);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Get all users for browsing/discovery
     * ADMIN ONLY - Requires ROLE_ADMIN authority
     * 
     * PRODUCTION NOTE: In a production environment, you should consider:
     * - Implementing pagination: @RequestParam(defaultValue="0") int page
     * - Filtering by preferences/location
     * - Rate limiting to prevent abuse
     * - Caching expensive queries
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/retrieve")
    public ResponseEntity<List<User>> getAllUsers() {
        try {
            List<User> users = userRepository.findAll();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            log.error("Failed to retrieve users: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}