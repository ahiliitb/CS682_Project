package com.SE.final_project.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.SE.final_project.service.NotificationService;

@Controller
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/dashboard/notifications/read")
    public String markRead(@RequestParam Long notificationId,
                           @RequestParam(required = false, defaultValue = "/dashboard") String returnTo,
                           Principal principal,
                           RedirectAttributes redirectAttributes) {
        try {
            notificationService.markAsRead(principal.getName(), notificationId);
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }
        return "redirect:" + returnTo;
    }
}
