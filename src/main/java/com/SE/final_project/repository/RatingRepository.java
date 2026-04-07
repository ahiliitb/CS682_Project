package com.SE.final_project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.SE.final_project.model.Rating;
import com.SE.final_project.model.User;

public interface RatingRepository extends JpaRepository<Rating, Long> {

    List<Rating> findByRateeOrderByCreatedAtDesc(User ratee);
}