package com.SE.final_project.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.SE.final_project.service.LibraryService;

@Controller
public class LibraryController {

    private final LibraryService libraryService;

    public LibraryController(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    @PostMapping("/dashboard/library/add")
    public String addBook(@RequestParam String title,
                          @RequestParam(required = false) String author,
                          @RequestParam(required = false) String courseCode,
                          @RequestParam(required = false) String conditionNote,
                          Principal principal,
                          RedirectAttributes redirectAttributes) {
        try {
            libraryService.addBook(principal.getName(), title, author, courseCode, conditionNote);
            redirectAttributes.addFlashAttribute("successMessage", "Book added to library exchange.");
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }
        return "redirect:/dashboard/library";
    }

    @PostMapping("/dashboard/library/borrow")
    public String borrowBook(@RequestParam Long bookId,
                             @RequestParam(defaultValue = "14") int returnInDays,
                             Principal principal,
                             RedirectAttributes redirectAttributes) {
        try {
            libraryService.borrowBook(principal.getName(), bookId, returnInDays);
            redirectAttributes.addFlashAttribute("successMessage", "Book borrowed successfully.");
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }
        return "redirect:/dashboard/library";
    }

    @PostMapping("/dashboard/library/request-return")
    public String requestReturn(@RequestParam Long bookId,
                                Principal principal,
                                RedirectAttributes redirectAttributes) {
        try {
            libraryService.requestReturn(principal.getName(), bookId);
            redirectAttributes.addFlashAttribute("successMessage", "Return request sent to owner.");
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }
        return "redirect:/dashboard/library";
    }

    @PostMapping("/dashboard/library/mark-returned")
    public String markReturned(@RequestParam Long bookId,
                               Principal principal,
                               RedirectAttributes redirectAttributes) {
        try {
            libraryService.markReturned(principal.getName(), bookId);
            redirectAttributes.addFlashAttribute("successMessage", "Book marked as returned.");
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("errorMessage", exception.getMessage());
        }
        return "redirect:/dashboard/library";
    }
}
