package com.mates.roommatefinder.controller;

import java.util.List;

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
import com.mates.roommatefinder.service.ProfileService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @PostMapping("/create")
    public ResponseEntity<ProfileResponseDTO> createProfile(@Valid @RequestBody ProfileDTO profileDTO) {
        Profile profile = profileService.createProfile(profileDTO);
        return ResponseEntity.ok(profileService.toDTO(profile));
    }

    // Retrieve all profiles
    @GetMapping("/retrieve")
    public ResponseEntity<List<ProfileResponseDTO>> getAllProfiles() {
        List<ProfileResponseDTO> profiles = profileService.getAllProfilesDTO();
        return ResponseEntity.ok(profiles);
    }

    // Retrieve a specific profile by user ID
    @GetMapping("/{userId}")
    public ResponseEntity<ProfileResponseDTO> getProfile(@PathVariable Long userId) {
        ProfileResponseDTO profile = profileService.getProfileDTO(userId);
        return ResponseEntity.ok(profile);
    }

    @PatchMapping("/update")
    public ResponseEntity<ProfileResponseDTO> updateProfile(
            @RequestParam Long userId, // later you can get from auth
            @RequestBody ProfileUpdateDTO dto) {
        Profile updatedProfile = profileService.updateProfile(userId, dto);
        return ResponseEntity.ok(ProfileResponseDTO.fromProfile(updatedProfile));
    }

}