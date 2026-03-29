package com.mates.roommatefinder.dto;

import com.mates.roommatefinder.model.Profile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ProfileResponseDTO {
    private Long id;
    private Long userId;
    private String name;
    private String lookingForOption;
    private String bio;
    private Integer age;
    private String city;

    // Mapper method from Profile -> ProfileResponseDTO
    public static ProfileResponseDTO fromProfile(Profile profile) {
        if (profile == null) return null;

        return ProfileResponseDTO.builder()
                .id(profile.getId())
                .userId(profile.getUser() != null ? profile.getUser().getId() : null)
                .name(profile.getName())
                .bio(profile.getBio())
                .age(profile.getAge())
                .city(profile.getCity())
                .lookingForOption(profile.getLookingForOption())
                .build();
    }

}