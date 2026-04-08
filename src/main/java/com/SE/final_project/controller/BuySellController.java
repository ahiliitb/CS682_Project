package com.SE.final_project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.SE.final_project.service.BuySellService;

@Controller
public class BuySellController {

    private final BuySellService buySellService;

    public BuySellController(BuySellService buySellService) {
        this.buySellService = buySellService;
    }

    @PostMapping("/dashboard/buysell/list")
    public String createListing(@RequestParam String name,
                                @RequestParam(required = false) String description,
                                @RequestParam double price,
                                java.security.Principal principal,
                                RedirectAttributes redirectAttributes) {
        try {
            buySellService.createListing(principal.getName(), name, description, price);
            redirectAttributes.addFlashAttribute("successMessage", "Item listed successfully.");
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }

        return "redirect:/dashboard";
    }

    @PostMapping("/dashboard/buysell/buy")
    public String buyItem(@RequestParam Long itemId,
                          java.security.Principal principal,
                          RedirectAttributes redirectAttributes) {
        try {
            buySellService.buyItem(principal.getName(), itemId);
            redirectAttributes.addFlashAttribute("successMessage", "Purchase successful. The listing is now closed.");
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }

        return "redirect:/dashboard";
    }
}
