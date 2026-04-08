package com.SE.final_project.controller;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.SE.final_project.model.UserRole;
import com.SE.final_project.model.User;
import com.SE.final_project.model.NotificationType;
import com.SE.final_project.service.AuctionService;
import com.SE.final_project.service.BuySellService;
import com.SE.final_project.service.LibraryService;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import com.SE.final_project.repository.UserRepository;
import com.SE.final_project.service.IitbHighlightsService;
import com.SE.final_project.service.LostFoundService;
import com.SE.final_project.service.NotificationService;
import com.SE.final_project.service.PoolingService;
import com.SE.final_project.service.SsoCodeService;
import com.SE.final_project.service.StatisticsService;
import com.SE.final_project.service.TeamService;

import org.springframework.security.crypto.password.PasswordEncoder;

@Controller
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final IitbHighlightsService iitbHighlightsService;
    private final TeamService teamService;
    private final StatisticsService statisticsService;
    private final BuySellService buySellService;
    private final LostFoundService lostFoundService;
    private final AuctionService auctionService;
    private final PoolingService poolingService;
    private final LibraryService libraryService;
    private final NotificationService notificationService;
    private final UserDetailsService userDetailsService;
    private final SsoCodeService ssoCodeService;

    @Value("${app.security.admin-emails:}")
    private String adminEmailsConfig;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder,
            IitbHighlightsService iitbHighlightsService, TeamService teamService,
            StatisticsService statisticsService, BuySellService buySellService,
            LostFoundService lostFoundService, AuctionService auctionService,
            PoolingService poolingService, LibraryService libraryService,
            NotificationService notificationService, UserDetailsService userDetailsService,
            SsoCodeService ssoCodeService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.iitbHighlightsService = iitbHighlightsService;
        this.teamService = teamService;
        this.statisticsService = statisticsService;
        this.buySellService = buySellService;
        this.lostFoundService = lostFoundService;
        this.auctionService = auctionService;
        this.poolingService = poolingService;
        this.libraryService = libraryService;
        this.notificationService = notificationService;
        this.userDetailsService = userDetailsService;
        this.ssoCodeService = ssoCodeService;
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

    @GetMapping("/sso")
    public String ssoPage(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null) {
            return "redirect:/dashboard";
        }
        return "sso";
    }

    @PostMapping("/sso-login")
    public String ssoLogin(@RequestParam String email,
                           @RequestParam String ssoCode,
                           HttpServletRequest request,
                           RedirectAttributes redirectAttributes) {
        if (email == null || email.isBlank() || !email.toLowerCase().endsWith("@iitb.ac.in")) {
            redirectAttributes.addFlashAttribute("error", "Only IIT Bombay email addresses can use SSO.");
            return "redirect:/sso";
        }
        try {
            ssoCodeService.consumeCode(ssoCode, email.trim());
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("error", exception.getMessage());
            return "redirect:/sso";
        }

        User user = userRepository.findByEmail(email.trim());
        if (user == null) {
            user = new User();
            user.setEmail(email.trim());
            user.setUsername(uniqueUsernameFromEmail(email.trim()));
            user.setPassword(passwordEncoder.encode("sso-login-placeholder"));
        }
        user.setRole(resolveRoleForEmail(user.getEmail()));
        userRepository.save(user);

        notificationService.notifyUser(user.getUsername(),
            "SSO login",
            "You logged in successfully using campus SSO.",
            NotificationType.SECURITY);

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);
        request.getSession(true)
                .setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);

        redirectAttributes.addFlashAttribute("successMessage", "SSO login successful.");
        return "redirect:/dashboard";
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
        user.setRole(resolveRoleForEmail(email));
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
        addCommonDashboardData(model, userDetails.getUsername());
        model.addAttribute("activeTab", "buysell");
        model.addAttribute("activeListings", buySellService.getActiveListings(userDetails.getUsername()));
        model.addAttribute("myListings", buySellService.getListingsPostedBy(userDetails.getUsername()));
        model.addAttribute("suggestedListingPrice", buySellService.getSuggestedListingPrice());
        addIitbHighlights(model);
        return "dashboard";
    }

    @GetMapping("/dashboard/lost-found")
    public String lostFound(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        addCommonDashboardData(model, userDetails.getUsername());
        model.addAttribute("activeTab", "lostfound");
        model.addAttribute("lostFoundPosts", lostFoundService.getAllPosts());
        addIitbHighlights(model);
        return "dashboard";
    }

    @GetMapping("/dashboard/auction")
    public String auction(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        addCommonDashboardData(model, userDetails.getUsername());
        model.addAttribute("activeTab", "auction");
        model.addAttribute("auctionListings", auctionService.getAllListings());
        addIitbHighlights(model);
        return "dashboard";
    }

    @GetMapping("/dashboard/teams")
    public String teams(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        addCommonDashboardData(model, userDetails.getUsername());
        model.addAttribute("activeTab", "teams");
        model.addAttribute("allTeams", teamService.getAllTeams());
        model.addAttribute("joinedTeamIds", teamService.getJoinedTeamIds(userDetails.getUsername()));
        model.addAttribute("createdTeams", teamService.getTeamsCreatedBy(userDetails.getUsername()));
        addIitbHighlights(model);
        return "dashboard";
    }

    @GetMapping("/dashboard/stats")
    public String stats(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        addCommonDashboardData(model, userDetails.getUsername());
        model.addAttribute("activeTab", "stats");
        model.addAttribute("totalUsers", statisticsService.getTotalUsers());
        model.addAttribute("totalItems", statisticsService.getTotalItems());
        model.addAttribute("activeItems", statisticsService.getActiveItems());
        model.addAttribute("boughtCount", statisticsService.getBoughtCount());
        model.addAttribute("soldCount", statisticsService.getSoldCount());
        model.addAttribute("lostCount", statisticsService.getLostCount());
        model.addAttribute("auctionedCount", statisticsService.getAuctionedCount());
        model.addAttribute("completedTransactions", statisticsService.getCompletedTransactions());
        model.addAttribute("campusActivityCount", statisticsService.getCampusActivityCount());
        addIitbHighlights(model);
        return "dashboard";
    }

    @GetMapping("/dashboard/pooling")
    public String pooling(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        addCommonDashboardData(model, userDetails.getUsername());
        model.addAttribute("activeTab", "pooling");
        model.addAttribute("poolingRequests", poolingService.getAllRequests());
        model.addAttribute("createdPoolingRequests", poolingService.getRequestsCreatedBy(userDetails.getUsername()));
        model.addAttribute("joinedPoolingRequestIds", poolingService.getJoinedRequestIds(userDetails.getUsername()));
        addIitbHighlights(model);
        return "dashboard";
    }

    @GetMapping("/dashboard/library")
    public String library(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        addCommonDashboardData(model, userDetails.getUsername());
        model.addAttribute("activeTab", "library");
        model.addAttribute("availableBooks", libraryService.getAvailableBooks());
        model.addAttribute("myLibraryBooks", libraryService.getBooksOwnedBy(userDetails.getUsername()));
        model.addAttribute("myBorrowedBooks", libraryService.getBooksBorrowedBy(userDetails.getUsername()));
        addIitbHighlights(model);
        return "dashboard";
    }

    private void addCommonDashboardData(Model model, String username) {
        model.addAttribute("username", username);
        model.addAttribute("notifications", notificationService.getRecentForUser(username));
        model.addAttribute("unreadNotifications", notificationService.getUnreadCount(username));
        model.addAttribute("isAdmin", isAdminUser(username));
    }

    private void addIitbHighlights(Model model) {
        var doc = iitbHighlightsService.loadDocument();
        model.addAttribute("iitbHighlights", iitbHighlightsService.getItems());
        model.addAttribute("iitbHighlightsFetchedAt", doc.fetchedAt());
        model.addAttribute("iitbHighlightsSource", doc.source());
    }

    private UserRole resolveRoleForEmail(String email) {
        if (email == null) {
            return UserRole.STUDENT;
        }

        Set<String> adminEmails = Arrays.stream(adminEmailsConfig.split(","))
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        return adminEmails.contains(email.toLowerCase()) ? UserRole.ADMIN : UserRole.STUDENT;
    }

    private boolean isAdminUser(String username) {
        User user = userRepository.findByUsername(username);
        return user != null && user.getRole() == UserRole.ADMIN;
    }

    private String uniqueUsernameFromEmail(String email) {
        String base = email.substring(0, email.indexOf('@')).replaceAll("[^a-zA-Z0-9._-]", "");
        String candidate = base.isBlank() ? "iitbuser" : base;
        int suffix = 1;
        while (userRepository.existsByUsername(candidate)) {
            candidate = base + suffix;
            suffix++;
        }
        return candidate;
    }
}
