package com.mates.roommatefinder.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mates.roommatefinder.dto.ProfileDTO;
import com.mates.roommatefinder.dto.ProfileResponseDTO;
import com.mates.roommatefinder.service.ProfileService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/profiles")
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;

     @PostMapping("/create")
    public ProfileResponseDTO createProfile(@RequestBody ProfileDTO profileDTO) {
        return profileService.createProfile(profileDTO);
    }

    @GetMapping("/retrieve")
    public List<ProfileResponseDTO> getAllProfiles() {
        return profileService.getAllProfilesDTO();
    }

    @GetMapping("/{id}")
    public ProfileResponseDTO getProfile(@PathVariable Long id) {
        return profileService.getProfileDTO(id);
    }
}