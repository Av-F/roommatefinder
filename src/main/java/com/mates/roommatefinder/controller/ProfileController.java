package com.mates.roommatefinder.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mates.roommatefinder.dto.ProfileRequestDTO;
import com.mates.roommatefinder.dto.ProfileResponseDTO;
import com.mates.roommatefinder.service.ProfileService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/profiles")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;
    /*create
    @param profileRequestDTO
    creates the profile with given DTO */
    @PostMapping
    public ProfileResponseDTO create(@RequestBody ProfileRequestDTO dto) {
        return profileService.createProfile(dto);
    }
    /*search
    gives list of profiles based on given parameters */
    @GetMapping("/search")
    public List<ProfileResponseDTO> search(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) Integer minAge,
            @RequestParam(required = false) Integer maxAge,
            @RequestParam(required = false) String lookingForOption
    ) {
        return profileService.searchProfiles(city, minAge, maxAge, lookingForOption);
    }
}