package com.SE.final_project.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.springframework.stereotype.Service;

import com.SE.final_project.model.Item;
import com.SE.final_project.model.Rating;
import com.SE.final_project.model.RelationType;
import com.SE.final_project.model.User;
import com.SE.final_project.model.UserItemRelation;
import com.SE.final_project.repository.RatingRepository;
import com.SE.final_project.repository.UserItemRelationRepository;
import com.SE.final_project.repository.UserRepository;

@Service
public class RatingService {

    private final RatingRepository ratingRepository;
    private final UserRepository userRepository;
    private final UserItemRelationRepository userItemRelationRepository;

    public RatingService(RatingRepository ratingRepository,
                         UserRepository userRepository,
                         UserItemRelationRepository userItemRelationRepository) {
        this.ratingRepository = ratingRepository;
        this.userRepository = userRepository;
        this.userItemRelationRepository = userItemRelationRepository;
    }

    /**
     * Sellers the current user may rate: distinct users who sold them an item on Buy &amp; Sell (completed purchase).
     */
    public List<User> getSellersBuyerPurchasedFrom(String buyerUsername) {
        User buyer = requireUser(buyerUsername);
        LinkedHashMap<Long, User> byId = new LinkedHashMap<>();
        for (UserItemRelation bought : userItemRelationRepository.findByUserAndRelationType(buyer, RelationType.BOUGHT)) {
            Item item = bought.getItem();
            if (item == null) {
                continue;
            }
            for (UserItemRelation sold : userItemRelationRepository.findByItemAndRelationType(item, RelationType.SOLD)) {
                User seller = sold.getUser();
                if (seller != null && !seller.getId().equals(buyer.getId())) {
                    byId.putIfAbsent(seller.getId(), seller);
                }
            }
        }
        return new ArrayList<>(byId.values());
    }

    public boolean hasBuyerPurchasedFromSeller(User buyer, User seller) {
        for (UserItemRelation bought : userItemRelationRepository.findByUserAndRelationType(buyer, RelationType.BOUGHT)) {
            Item item = bought.getItem();
            if (item == null) {
                continue;
            }
            for (UserItemRelation sold : userItemRelationRepository.findByItemAndRelationType(item, RelationType.SOLD)) {
                if (sold.getUser() != null && sold.getUser().getId().equals(seller.getId())) {
                    return true;
                }
            }
        }
        return false;
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

        if (!hasBuyerPurchasedFromSeller(rater, ratee)) {
            throw new IllegalArgumentException("You can only rate sellers you have bought from on Buy & Sell.");
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