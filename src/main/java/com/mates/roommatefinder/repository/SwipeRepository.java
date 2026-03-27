package com.mates.roommatefinder.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mates.roommatefinder.model.Swipe;
import com.mates.roommatefinder.model.User;

public interface SwipeRepository extends JpaRepository<Swipe, Long> {

    Optional<Swipe> findBySwiperAndTarget(User swiper, User target);
}