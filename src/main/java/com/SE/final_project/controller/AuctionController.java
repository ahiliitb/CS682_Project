package com.SE.final_project.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.SE.final_project.service.AuctionService;

@Controller
public class AuctionController {

    private final AuctionService auctionService;

    public AuctionController(AuctionService auctionService) {
        this.auctionService = auctionService;
    }

    @PostMapping("/dashboard/auction")
    public String createAuction(@RequestParam String itemName,
                                @RequestParam(required = false) String description,
                                @RequestParam double startPrice,
                                @RequestParam(defaultValue = "24") int durationHours,
                                java.security.Principal principal,
                                RedirectAttributes redirectAttributes) {
        try {
            auctionService.createListing(principal.getName(), itemName, description, startPrice, durationHours);
            redirectAttributes.addFlashAttribute("successMessage", "Auction listing created.");
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }

        return "redirect:/dashboard/auction";
    }

    @PostMapping("/dashboard/auction/bid")
    public String placeBid(@RequestParam Long listingId,
                           @RequestParam double amount,
                           java.security.Principal principal,
                           RedirectAttributes redirectAttributes) {
        try {
            auctionService.placeBid(principal.getName(), listingId, amount);
            redirectAttributes.addFlashAttribute("successMessage", "Bid placed successfully.");
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }

        return "redirect:/dashboard/auction";
    }
}
