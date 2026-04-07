package com.SE.final_project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.SE.final_project.model.User;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import com.SE.final_project.repository.UserRepository;
import com.SE.final_project.service.IitbHighlightsService;
import com.SE.final_project.service.TeamService;

import org.springframework.security.crypto.password.PasswordEncoder;

@Controller
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final IitbHighlightsService iitbHighlightsService;
    private final TeamService teamService;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder,
            IitbHighlightsService iitbHighlightsService, TeamService teamService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.iitbHighlightsService = iitbHighlightsService;
        this.teamService = teamService;
    }

    @GetMapping("/login")
    public String loginPage(@AuthenticationPrincipal UserDetails userDetails,
                            @RequestParam(value = "logout", required = false) String logout,
                            @RequestParam(value = "error", required = false) String error,
                            Model model) {
        if (userDetails != null) {
            return "redirect:/";
        }
        if (logout != null) {
            model.addAttribute("message", "You have been logged out successfully.");
        }
        if (error != null) {
            model.addAttribute("error", "Invalid username or password.");
        }
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails != null) {
            return "redirect:/";
        }
        return "Registration Page";
    }

    @GetMapping("/signup")
    public String signupRedirect(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null) {
            return "redirect:/";
        }
        return "redirect:/register";
    }

    @PostMapping("/signup")
    public String signup(@RequestParam String username,
                         @RequestParam String email,
                         @RequestParam String password,
                         @RequestParam String confirmPassword,
                         RedirectAttributes redirectAttributes) {
        if (username == null || username.isBlank()) {
            redirectAttributes.addFlashAttribute("error", "Username is required.");
            return "redirect:/register";
        }
        if (email == null || email.isBlank()) {
            redirectAttributes.addFlashAttribute("error", "Email is required.");
            return "redirect:/register";
        }
        if (!email.toLowerCase().endsWith("@iitb.ac.in")) {
            redirectAttributes.addFlashAttribute("error", "Only IIT Bombay email addresses (@iitb.ac.in) are allowed.");
            return "redirect:/register";
        }
        if (password == null || password.length() < 4) {
            redirectAttributes.addFlashAttribute("error", "Password must be at least 4 characters.");
            return "redirect:/register";
        }
        if (!password.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Passwords do not match.");
            return "redirect:/register";
        }
        if (userRepository.existsByUsername(username)) {
            redirectAttributes.addFlashAttribute("error", "Username already taken.");
            return "redirect:/register";
        }
        if (userRepository.existsByEmail(email)) {
            redirectAttributes.addFlashAttribute("error", "Email already registered.");
            return "redirect:/register";
        }

        User user = new User();
        user.setUsername(username.trim());
        user.setEmail(email.trim());
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);

        redirectAttributes.addFlashAttribute("message", "Account created. Please log in.");
        return "redirect:/login";
    }

    @GetMapping("/")
    public String home(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails != null) {
            return "redirect:/dashboard";
        }
        model.addAttribute("loggedIn", false);
        return "home";
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        model.addAttribute("username", userDetails.getUsername());
        model.addAttribute("activeTab", "buysell");
        addIitbHighlights(model);
        return "dashboard";
    }

    @GetMapping("/dashboard/lost-found")
    public String lostFound(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        model.addAttribute("username", userDetails.getUsername());
        model.addAttribute("activeTab", "lostfound");
        addIitbHighlights(model);
        return "dashboard";
    }

    @GetMapping("/dashboard/auction")
    public String auction(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        model.addAttribute("username", userDetails.getUsername());
        model.addAttribute("activeTab", "auction");
        addIitbHighlights(model);
        return "dashboard";
    }

    @GetMapping("/dashboard/teams")
    public String teams(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        model.addAttribute("username", userDetails.getUsername());
        model.addAttribute("activeTab", "teams");
        model.addAttribute("allTeams", teamService.getAllTeams());
        model.addAttribute("joinedTeamIds", teamService.getJoinedTeamIds(userDetails.getUsername()));
        model.addAttribute("createdTeams", teamService.getTeamsCreatedBy(userDetails.getUsername()));
        addIitbHighlights(model);
        return "dashboard";
    }

    @GetMapping("/dashboard/stats")
    public String stats(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        model.addAttribute("username", userDetails.getUsername());
        model.addAttribute("activeTab", "stats");
        addIitbHighlights(model);
        return "dashboard";
    }

    private void addIitbHighlights(Model model) {
        var doc = iitbHighlightsService.loadDocument();
        model.addAttribute("iitbHighlights", iitbHighlightsService.getItems());
        model.addAttribute("iitbHighlightsFetchedAt", doc.fetchedAt());
        model.addAttribute("iitbHighlightsSource", doc.source());
    }
}
