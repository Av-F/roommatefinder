package com.mates.roommatefinder.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mates.roommatefinder.model.Swipe;
import com.mates.roommatefinder.repository.SwipeRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/swipes")
@RequiredArgsConstructor
public class SwipeController {

    private final SwipeRepository swipeRepository;

    @PostMapping("/create")
    public Swipe createSwipe(@RequestBody Swipe swipe) {
        return swipeRepository.save(swipe);
    }

    @GetMapping
    public List<Swipe> getAllSwipes() {
        return swipeRepository.findAll();
    }

    @GetMapping("/{id}")
    public Swipe getSwipe(@PathVariable Long id) {
        return swipeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Swipe not found"));
    }
}