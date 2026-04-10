package com.SE.final_project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.SE.final_project.service.TeamService;

@Controller
public class TeamController {

    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @PostMapping("/dashboard/teams")
    public String createTeam(@RequestParam String name,
                             @RequestParam(required = false) String courseCode,
                             @RequestParam(required = false) String description,
                             @RequestParam(required = false) Integer maxSize,
                             java.security.Principal principal,
                             RedirectAttributes redirectAttributes) {
        try {
            if (maxSize == null || maxSize < 1) {
                maxSize = 50;
            }
            teamService.createTeam(principal.getName(), name, courseCode, description, maxSize);
            redirectAttributes.addFlashAttribute("successMessage", "Team created successfully.");
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }

        return "redirect:/dashboard/teams";
    }

    @PostMapping("/dashboard/teams/join")
    public String joinTeam(@RequestParam Long teamId,
                           java.security.Principal principal,
                           RedirectAttributes redirectAttributes) {
        try {
            teamService.joinTeam(principal.getName(), teamId);
            redirectAttributes.addFlashAttribute("successMessage", "You joined the team.");
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }

        return "redirect:/dashboard/teams";
    }
}