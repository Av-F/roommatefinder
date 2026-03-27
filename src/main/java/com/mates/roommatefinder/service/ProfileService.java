package com.mates.roommatefinder.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.mates.roommatefinder.dto.ProfileDTO;
import com.mates.roommatefinder.dto.ProfileResponseDTO;
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

    public ProfileResponseDTO createProfile(ProfileDTO dto) {
    // fetch user by ID
    User user = userRepository.findById(dto.getId()) // make sure ProfileDTO has userId
            .orElseThrow(() -> new RuntimeException("User not found"));

    // build profile entity
    Profile profile = Profile.builder()
            .name(dto.getName())
            .lookingForOption(dto.getLookingForOption())
            .bio(dto.getBio())
            .age(dto.getAge())
            .city(dto.getCity())
            .user(user)
            .build();

    Profile savedProfile = profileRepository.save(profile);

    // convert to DTO before returning
    return toDTO(savedProfile);
}

    public ProfileResponseDTO toDTO(Profile profile) {
    return ProfileResponseDTO.builder()
            .id(profile.getId())
            .name(profile.getName())
            .lookingForOption(profile.getLookingForOption())
            .bio(profile.getBio())
            .age(profile.getAge())
            .city(profile.getCity())
            .userId(profile.getUser() != null ? profile.getUser().getId() : null)
            .build();
    }

    public List<ProfileResponseDTO> getAllProfilesDTO() {
        return profileRepository.findAll()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public ProfileResponseDTO getProfileDTO(Long id) {
    if (id == null) {
        throw new IllegalArgumentException("Profile ID cannot be null");
    }

    return profileRepository.findById(id)
            .map(this::toDTO) // convert entity to DTO
            .orElseThrow(() -> new RuntimeException("Profile not found with ID: " + id));
    }
}