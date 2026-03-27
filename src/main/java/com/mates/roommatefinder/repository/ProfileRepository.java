package com.mates.roommatefinder.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mates.roommatefinder.model.Profile;
import com.mates.roommatefinder.model.User;

public interface ProfileRepository extends JpaRepository<Profile, Long> {

    List<Profile> findByCity(String city);

    List<Profile> findByAgeBetween(Integer minAge, Integer maxAge);

    List<Profile> findByLookingForOption(String lookingForOption);

    boolean existsByUser(User user);

}