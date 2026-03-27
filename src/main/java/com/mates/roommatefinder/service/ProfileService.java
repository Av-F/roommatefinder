package com.mates.roommatefinder.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.mates.roommatefinder.dto.ProfileDTO;
import com.mates.roommatefinder.dto.ProfileResponseDTO;
import com.mates.roommatefinder.dto.ProfileUpdateDTO;
import com.mates.roommatefinder.model.Profile;
import com.mates.roommatefinder.model.User;
import com.mates.roommatefinder.repository.ProfileRepository;
import com.mates.roommatefinder.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;

    public Profile createProfile(ProfileDTO dto) {
    User user = userRepository.findById(dto.getUserId())
            .orElseThrow(() -> new RuntimeException("User not found with ID: " + dto.getUserId()));

    // Check if profile already exists
    if (profileRepository.existsById(user.getId())) {
        throw new RuntimeException("Profile already exists for user ID: " + user.getId());
    }

    Profile profile = Profile.builder()
            .user(user)
            .name(dto.getName())
            .lookingForOption(dto.getLookingForOption())
            .bio(dto.getBio())
            .age(dto.getAge())
            .city(dto.getCity())
            .build();

    return profileRepository.save(profile);
}

    // Convert Profile to DTO
    public ProfileResponseDTO toDTO(Profile profile) {
        return ProfileResponseDTO.builder()
                .name(profile.getName())
                .lookingForOption(profile.getLookingForOption())
                .bio(profile.getBio())
                .age(profile.getAge())
                .city(profile.getCity())
                .id(profile.getUser() != null ? profile.getUser().getId() : null)
                .build();
    }

        // Retrieve all profiles as DTOs
    public List<ProfileResponseDTO> getAllProfilesDTO() {
        return profileRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // Retrieve a specific profile by ID (same as user ID)
    public ProfileResponseDTO getProfileDTO(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }

        return profileRepository.findById(userId)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Profile not found for user ID: " + userId));
    }

        public Profile updateProfile(Long userId, ProfileUpdateDTO dto) {
        Profile profile = profileRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        // Only update fields that are not null
        if (dto.getName() != null) profile.setName(dto.getName());
        if (dto.getBio() != null) profile.setBio(dto.getBio());
        if (dto.getAge() != null) profile.setAge(dto.getAge());
        if (dto.getCity() != null) profile.setCity(dto.getCity());
        if (dto.getLookingForOption() != null) profile.setLookingForOption(dto.getLookingForOption());

        return profileRepository.save(profile);
    }

}