package com.mates.roommatefinder.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mates.roommatefinder.model.Swipe;

public interface SwipeRepository extends JpaRepository<Swipe, Long> {

    List<Swipe> findBySwiperUserId(Long swiperUserId);
    
    List<Swipe> findBySwipedUserIdAndLikedTrue(Long swipedUserId);
}