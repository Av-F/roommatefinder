package com.mates.roommatefinder.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.mates.roommatefinder.service.SwipeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/swipes")
@RequiredArgsConstructor
public class SwipeController {

    private final SwipeService swipeService;

    @PostMapping
    public ResponseEntity<String> swipe(
            @RequestParam Long swiperId,
            @RequestParam Long targetId,
            @RequestParam boolean liked) {

        swipeService.swipe(swiperId, targetId, liked);
        return ResponseEntity.ok("Swipe recorded");
    }
}