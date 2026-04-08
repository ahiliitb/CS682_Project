package com.SE.final_project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.SE.final_project.service.LostFoundService;

@Controller
public class LostFoundController {

    private final LostFoundService lostFoundService;

    public LostFoundController(LostFoundService lostFoundService) {
        this.lostFoundService = lostFoundService;
    }

    @PostMapping("/dashboard/lost-found")
    public String createPost(@RequestParam String type,
                             @RequestParam String title,
                             @RequestParam(required = false) String location,
                             @RequestParam(required = false) String description,
                             java.security.Principal principal,
                             RedirectAttributes redirectAttributes) {
        try {
            lostFoundService.createPost(principal.getName(), type, title, location, description);
            redirectAttributes.addFlashAttribute("successMessage", "Lost/found report posted.");
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }

        return "redirect:/dashboard/lost-found";
    }

    @PostMapping("/dashboard/lost-found/resolve")
    public String resolvePost(@RequestParam Long postId,
                              java.security.Principal principal,
                              RedirectAttributes redirectAttributes) {
        try {
            lostFoundService.resolvePost(principal.getName(), postId);
            redirectAttributes.addFlashAttribute("successMessage", "Post marked as resolved.");
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }

        return "redirect:/dashboard/lost-found";
    }
}
