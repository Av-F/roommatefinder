package com.mates.roommatefinder.controller;

import java.util.List;

import com.mates.roommatefinder.dto.ProfileResponseDTO;
import com.mates.roommatefinder.service.MatchService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;

    /**
     * GET /api/matches?userId=1
     * Returns all matched profiles for a user
     */
    @GetMapping("/api/matches")
    public ResponseEntity<List<ProfileResponseDTO>> getMatches(@RequestParam Long userId) {
        List<ProfileResponseDTO> matches = matchService.getMatches(userId);
        return ResponseEntity.ok(matches);
    }
}