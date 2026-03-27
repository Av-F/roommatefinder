package com.mates.roommatefinder.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mates.roommatefinder.model.Swipe;
import com.mates.roommatefinder.model.User;

@Repository
public interface SwipeRepository extends JpaRepository<Swipe, Long> {

    List<Swipe> findBySwiper(User swiper);

    List<Swipe> findByTarget(User target);

    List<Swipe> findBySwiperAndTarget(User swiper, User target);
}