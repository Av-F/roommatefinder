package com.mates.roommatefinder.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.mates.roommatefinder.model.Swipe;
import com.mates.roommatefinder.repository.SwipeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SwipeService {

    private final SwipeRepository swipeRepository;

    // Create a swipe
    public Swipe createSwipe(Swipe swipe) {
        return swipeRepository.save(swipe);
    }

    // Get all swipes
    public List<Swipe> getAllSwipes() {
        return swipeRepository.findAll();
    }

    // Get swipes by swiper user
    public List<Swipe> getSwipesByUser(Long swiperUserId) {
        return swipeRepository.findBySwiperUserId(swiperUserId);
    }

    // Delete a swipe
    public void deleteSwipe(Long id) {
        swipeRepository.deleteById(id);
    }

    // Optional: match detection later
}