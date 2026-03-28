package com.mates.roommatefinder.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mates.roommatefinder.dto.ProfileDTO;
import com.mates.roommatefinder.dto.ProfileResponseDTO;
import com.mates.roommatefinder.dto.ProfileUpdateDTO;
import com.mates.roommatefinder.model.Profile;
import com.mates.roommatefinder.security.SecurityUtils;
import com.mates.roommatefinder.service.ProfileService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
@Slf4j
public class ProfileController {

    private final ProfileService profileService;

    /**
     * Create a new profile (authenticated users only)
     * The profile is linked to the currently authenticated user
     */
    @PostMapping("/create")
    public ResponseEntity<ProfileResponseDTO> createProfile(@Valid @RequestBody ProfileDTO profileDTO) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Set the user ID from authenticated user, ignore client-provided value
        profileDTO.setUserId(currentUserId);

        try {
            Profile profile = profileService.createProfile(profileDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(profileService.toDTO(profile));
        } catch (Exception e) {
            log.error("Failed to create profile for user {}: {}", currentUserId, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Get authenticated user's own profile
     */
    @GetMapping("/me")
    public ResponseEntity<ProfileResponseDTO> getMyProfile() {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            ProfileResponseDTO profile = profileService.getProfileDTO(currentUserId);
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Retrieve all profiles (for browsing/matching - can be public)
     * Returns limited info for privacy
     */
    @GetMapping("/retrieve")
    public ResponseEntity<List<ProfileResponseDTO>> getAllProfiles() {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            List<ProfileResponseDTO> profiles = profileService.getAllProfilesDTO();
            return ResponseEntity.ok(profiles);
        } catch (Exception e) {
            log.error("Failed to retrieve profiles: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get a specific profile by user ID
     * (Can be restricted or open depending on requirements)
     */
    @GetMapping("/{userId}")
    public ResponseEntity<ProfileResponseDTO> getProfile(@PathVariable Long userId) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            ProfileResponseDTO profile = profileService.getProfileDTO(userId);
            
            // Optional: Restrict to user's own profile or allow all authenticated users
            // Uncomment the check below to restrict to own profile only
            // if (!currentUserId.equals(userId)) {
            //     log.warn("User {} attempted to access profile of user {}", currentUserId, userId);
            //     return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            // }

            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Update profile (only owner can update their own profile)
     */
    @PatchMapping("/update")
    public ResponseEntity<ProfileResponseDTO> updateProfile(
            @RequestParam(required = false) Long userId,
            @RequestBody ProfileUpdateDTO dto) {
        
        Long currentUserId = SecurityUtils.getCurrentUserId();
        if (currentUserId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Use authenticated user ID, ignore client-provided userId
        userId = currentUserId;

        try {
            Profile updatedProfile = profileService.updateProfile(userId, dto);
            return ResponseEntity.ok(ProfileResponseDTO.fromProfile(updatedProfile));
        } catch (IllegalAccessError e) {
            log.warn("Unauthorized profile update attempt: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            log.error("Failed to update profile for user {}: {}", userId, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}