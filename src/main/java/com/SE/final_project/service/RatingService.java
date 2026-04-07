package com.SE.final_project.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.SE.final_project.model.Rating;
import com.SE.final_project.model.User;
import com.SE.final_project.repository.RatingRepository;
import com.SE.final_project.repository.UserRepository;

@Service
public class RatingService {

    private final RatingRepository ratingRepository;
    private final UserRepository userRepository;

    public RatingService(RatingRepository ratingRepository, UserRepository userRepository) {
        this.ratingRepository = ratingRepository;
        this.userRepository = userRepository;
    }

    public Rating createRating(String raterUsername, String rateeUsername, int score, String comment) {
        if (score < 1 || score > 5) {
            throw new IllegalArgumentException("Score must be between 1 and 5.");
        }

        User rater = requireUser(raterUsername);
        User ratee = requireUser(rateeUsername);

        if (rater.getUsername().equalsIgnoreCase(ratee.getUsername())) {
            throw new IllegalArgumentException("You cannot rate yourself.");
        }

        String safeComment = comment == null ? "" : comment.trim();
        Rating rating = new Rating(rater, ratee, score, safeComment);
        return ratingRepository.save(rating);
    }

    public List<Rating> getRatingsReceivedBy(String username) {
        User user = requireUser(username);
        return ratingRepository.findByRateeOrderByCreatedAtDesc(user);
    }

    public double getAverageRatingReceivedBy(String username) {
        List<Rating> ratings = getRatingsReceivedBy(username);
        if (ratings.isEmpty()) {
            return 0.0;
        }

        int total = 0;
        for (Rating rating : ratings) {
            total += rating.getScore();
        }
        return (double) total / ratings.size();
    }

    private User requireUser(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("User not found: " + username);
        }
        return user;
    }
}