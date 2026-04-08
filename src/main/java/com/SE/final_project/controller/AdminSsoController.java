package com.SE.final_project.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.SE.final_project.service.SsoCodeService;

@Controller
public class AdminSsoController {

    private final SsoCodeService ssoCodeService;

    public AdminSsoController(SsoCodeService ssoCodeService) {
        this.ssoCodeService = ssoCodeService;
    }

    @GetMapping("/admin/sso-codes")
    public String ssoCodesPage(Model model) {
        model.addAttribute("activeCodes", ssoCodeService.getActiveCodes());
        model.addAttribute("recentCodes", ssoCodeService.getRecentCodes());
        return "admin-sso-codes";
    }

    @PostMapping("/admin/sso-codes/generate")
    public String generateCode(Principal principal, RedirectAttributes redirectAttributes) {
        try {
            var code = ssoCodeService.issueCode(principal.getName());
            redirectAttributes.addFlashAttribute("successMessage", "New SSO code generated: " + code.getCode());
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }
        return "redirect:/admin/sso-codes";
    }
}
