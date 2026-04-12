package com.SE.final_project.controller;

import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.SE.final_project.service.BuySellService;
import com.SE.final_project.service.ItemImageStorageService;

@Controller
public class BuySellController {

    private final BuySellService buySellService;
    private final ItemImageStorageService itemImageStorageService;

    public BuySellController(BuySellService buySellService, ItemImageStorageService itemImageStorageService) {
        this.buySellService = buySellService;
        this.itemImageStorageService = itemImageStorageService;
    }

    @PostMapping("/dashboard/buysell/list")
    public String createListing(@RequestParam String name,
                                @RequestParam(required = false) String description,
                                @RequestParam double price,
                                @RequestParam(required = false) String visibility,
                                @RequestParam(required = false) MultipartFile image,
                                java.security.Principal principal,
                                RedirectAttributes redirectAttributes) {
        try {
            List<String> photoUrls = new ArrayList<>();
            itemImageStorageService.store(image).ifPresent(photoUrls::add);
            buySellService.createListing(principal.getName(), name, description, price, visibility, photoUrls);
            redirectAttributes.addFlashAttribute("successMessage", "Item listed successfully.");
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        } catch (UncheckedIOException exception) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Could not save the image. Try a smaller file or a JPEG/PNG/GIF/WebP image.");
        }

        return "redirect:/dashboard";
    }

    @PostMapping("/dashboard/buysell/edit")
    public String editListing(@RequestParam Long itemId,
                              @RequestParam String name,
                              @RequestParam(required = false) String description,
                              @RequestParam double price,
                              @RequestParam(required = false) String visibility,
                              @RequestParam(required = false) MultipartFile image,
                              java.security.Principal principal,
                              RedirectAttributes redirectAttributes) {
        try {
            List<String> photoUrls = new ArrayList<>();
            itemImageStorageService.store(image).ifPresent(photoUrls::add);
            List<String> passPhotos = photoUrls.isEmpty() ? null : photoUrls;
            buySellService.updateListing(principal.getName(), itemId, name, description, price, visibility, passPhotos);
            redirectAttributes.addFlashAttribute("successMessage", "Listing updated.");
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        } catch (UncheckedIOException exception) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Could not save the image. Try a smaller file or a JPEG/PNG/GIF/WebP image.");
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
