package com.mates.roommatefinder.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.mates.roommatefinder.dto.ProfileRequestDTO;
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

    /*createProfile:
    Parameters @ProfileRequestDTO
    creates the user profile by first linking user to profile and calls builder to profile
    maps profile to profile DTO*/
    public ProfileResponseDTO createProfile(ProfileRequestDTO dto) {

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Profile profile = Profile.builder()
                .name(dto.getName())
                .lookingForOption(dto.getLookingForOption())
                .bio(dto.getBio())
                .age(dto.getAge())
                .city(dto.getCity())
                .user(user)
                .build();

        return mapToDTO(profileRepository.save(profile));
    }

    /*searchProfiles:
    @param city
    @param minAge
    @param maxAge
    @param lookingForOption
    curates a list of profiles based on given parameters*/
    public List<ProfileResponseDTO> searchProfiles(
            String city,
            Integer minAge,
            Integer maxAge,
            String lookingForOption
    ) {

        List<Profile> profiles;

        if (city != null) {
            profiles = profileRepository.findByCity(city);
        } else if (minAge != null && maxAge != null) {
            profiles = profileRepository.findByAgeBetween(minAge, maxAge);
        } else if (lookingForOption != null) {
            profiles = profileRepository.findByLookingForOption(lookingForOption);
        } else {
            profiles = profileRepository.findAll();
        }

        return profiles.stream().map(this::mapToDTO).toList();
    }
    /*mapToDTO
    @param p (type profile)
    maps the given profile to a DTO */
    private ProfileResponseDTO mapToDTO(Profile p) {
        return ProfileResponseDTO.builder()
                .id(p.getId())
                .name(p.getName())
                .lookingForOption(p.getLookingForOption())
                .bio(p.getBio())
                .age(p.getAge())
                .city(p.getCity())
                .userId(p.getUser().getId())
                .build();
    }
}