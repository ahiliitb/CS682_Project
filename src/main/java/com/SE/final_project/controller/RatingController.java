package com.SE.final_project.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.SE.final_project.model.User;
import com.SE.final_project.model.UserRole;
import com.SE.final_project.repository.UserRepository;
import com.SE.final_project.service.NotificationService;
import com.SE.final_project.service.RatingService;

@Controller
public class RatingController {

    private final RatingService ratingService;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public RatingController(RatingService ratingService, UserRepository userRepository, NotificationService notificationService) {
        this.ratingService = ratingService;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    @GetMapping("/dashboard/ratings")
    public String ratingsPage(Principal principal, Model model) {
        populateRatingsModel(principal, model);
        return "dashboard";
    }

    @PostMapping("/dashboard/ratings")
    public String submitRating(Principal principal,
                               @RequestParam String rateeUsername,
                               @RequestParam int score,
                               @RequestParam(required = false) String comment,
                               RedirectAttributes redirectAttributes) {
        try {
            ratingService.createRating(principal.getName(), rateeUsername, score, comment);
            redirectAttributes.addFlashAttribute("successMessage", "Rating saved successfully.");
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }

        return "redirect:/dashboard/ratings";
    }

    private void populateRatingsModel(Principal principal, Model model) {
        String username = principal.getName();
        User currentUser = userRepository.findByUsername(username);
        List<User> users = userRepository.findAll().stream()
                .filter(user -> !user.getUsername().equalsIgnoreCase(username))
                .toList();

        model.addAttribute("username", username);
        model.addAttribute("activeTab", "ratings");
        model.addAttribute("notifications", notificationService.getRecentForUser(username));
        model.addAttribute("unreadNotifications", notificationService.getUnreadCount(username));
        model.addAttribute("isAdmin", currentUser != null && currentUser.getRole() == UserRole.ADMIN);
        model.addAttribute("users", users);
        model.addAttribute("ratingsReceived", ratingService.getRatingsReceivedBy(username));
        model.addAttribute("averageRating", ratingService.getAverageRatingReceivedBy(username));
        model.addAttribute("ratingsCount", ratingService.getRatingsReceivedBy(username).size());
        model.addAttribute("ratedUser", currentUser);
    }
}