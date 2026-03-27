package com.mates.roommatefinder.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.mates.roommatefinder.model.Profile;
import com.mates.roommatefinder.repository.ProfileRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;

    // Create a profile
    public Profile createProfile(Profile profile) {
        return profileRepository.save(profile);
    }

    // Get all profiles
    public List<Profile> getAllProfiles() {
        return profileRepository.findAll();
    }

    // Get profile by ID
    public Profile getProfileById(Long id) {
        return profileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Profile not found"));
    }

    // Get profiles by city
    public List<Profile> getProfilesByCity(String city) {
        return profileRepository.findByCity(city);
    }

    // Update a profile
    public Profile updateProfile(Long id, Profile updatedProfile) {
        Profile profile = getProfileById(id);
        profile.setName(updatedProfile.getName());
        profile.setBio(updatedProfile.getBio());
        profile.setAge(updatedProfile.getAge());
        profile.setCity(updatedProfile.getCity());
        profile.setLookingForOption(updatedProfile.getLookingForOption());
        profile.setUser(updatedProfile.getUser()); // optional for now
        return profileRepository.save(profile);
    }

    // Delete a profile
    public void deleteProfile(Long id) {
        profileRepository.deleteById(id);
    }
}