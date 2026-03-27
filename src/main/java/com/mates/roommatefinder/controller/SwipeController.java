package com.mates.roommatefinder.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.mates.roommatefinder.dto.ProfileResponseDTO;
import com.mates.roommatefinder.service.SwipeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/swipes")
@RequiredArgsConstructor
public class SwipeController {

    private final SwipeService swipeService;

    // Get swipeable profiles
    @GetMapping("/{userId}")
    public List<ProfileResponseDTO> getSwipeableProfiles(@PathVariable Long userId) {
        return swipeService.getSwipeableProfiles(userId);
    }

    // Swipe on a user
    @PostMapping("/{swiperId}/{targetId}")
    public void swipe(@PathVariable Long swiperId,
                      @PathVariable Long targetId,
                      @RequestParam boolean liked) {
        swipeService.swipe(swiperId, targetId, liked);
    }

    // Check match
    @GetMapping("/match/{user1}/{user2}")
    public boolean checkMatch(@PathVariable Long user1, @PathVariable Long user2) {
        return swipeService.isMatch(user1, user2);
    }
}