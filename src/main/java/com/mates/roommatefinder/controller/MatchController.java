package com.mates.roommatefinder.controller;

import java.util.List;

import com.mates.roommatefinder.dto.ProfileResponseDTO;
import com.mates.roommatefinder.service.MatchService;

import lombok.RequiredArgsConstructor;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/matches")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;

    // List matches for a user
    @GetMapping("/{userId}")
    public List<ProfileResponseDTO> getMatches(@PathVariable Long userId) {
        return matchService.getMatches(userId);
    }
}