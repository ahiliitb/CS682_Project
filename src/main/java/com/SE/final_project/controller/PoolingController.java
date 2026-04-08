package com.SE.final_project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.SE.final_project.service.PoolingService;

@Controller
public class PoolingController {

    private final PoolingService poolingService;

    public PoolingController(PoolingService poolingService) {
        this.poolingService = poolingService;
    }

    @PostMapping("/dashboard/pooling")
    public String createRequest(@RequestParam String title,
                                @RequestParam String origin,
                                @RequestParam String destination,
                                @RequestParam(required = false) String departureInfo,
                                @RequestParam(required = false) String details,
                                java.security.Principal principal,
                                RedirectAttributes redirectAttributes) {
        try {
            poolingService.createRequest(principal.getName(), title, origin, destination, departureInfo, details);
            redirectAttributes.addFlashAttribute("successMessage", "Pooling request created successfully.");
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }

        return "redirect:/dashboard/pooling";
    }

    @PostMapping("/dashboard/pooling/join")
    public String joinRequest(@RequestParam Long requestId,
                              java.security.Principal principal,
                              RedirectAttributes redirectAttributes) {
        try {
            poolingService.joinRequest(principal.getName(), requestId);
            redirectAttributes.addFlashAttribute("successMessage", "You joined the pooling request.");
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }

        return "redirect:/dashboard/pooling";
    }
}
