package com.SE.final_project.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.SE.final_project.service.ModerationService;

@Controller
public class ModerationController {

    private final ModerationService moderationService;

    public ModerationController(ModerationService moderationService) {
        this.moderationService = moderationService;
    }

    @PostMapping("/dashboard/report")
    public String reportContent(Principal principal,
                                @RequestParam String targetType,
                                @RequestParam Long targetId,
                                @RequestParam String reason,
                                @RequestParam(required = false, defaultValue = "/dashboard") String returnTo,
                                RedirectAttributes redirectAttributes) {
        try {
            moderationService.createReport(principal.getName(), targetType, targetId, reason);
            redirectAttributes.addFlashAttribute("successMessage", "Report submitted to admins.");
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }
        return "redirect:" + returnTo;
    }

    @GetMapping("/admin/moderation")
    public String moderationDashboard(Model model) {
        model.addAttribute("openReports", moderationService.getOpenReports());
        model.addAttribute("recentReports", moderationService.getRecentReports());
        return "admin-moderation";
    }

    @PostMapping("/admin/moderation/{reportId}/resolve")
    public String resolveReport(@PathVariable Long reportId,
                                @RequestParam(required = false) String note,
                                Principal principal,
                                RedirectAttributes redirectAttributes) {
        try {
            moderationService.resolveReport(principal.getName(), reportId, note);
            redirectAttributes.addFlashAttribute("successMessage", "Report resolved.");
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }
        return "redirect:/admin/moderation";
    }

    @PostMapping("/admin/moderation/{reportId}/reject")
    public String rejectReport(@PathVariable Long reportId,
                               @RequestParam(required = false) String note,
                               Principal principal,
                               RedirectAttributes redirectAttributes) {
        try {
            moderationService.rejectReport(principal.getName(), reportId, note);
            redirectAttributes.addFlashAttribute("successMessage", "Report marked as reviewed.");
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }
        return "redirect:/admin/moderation";
    }
}
