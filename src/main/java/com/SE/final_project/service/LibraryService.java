package com.SE.final_project.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.SE.final_project.model.LibraryBook;
import com.SE.final_project.model.LibraryBookStatus;
import com.SE.final_project.model.NotificationType;
import com.SE.final_project.model.User;
import com.SE.final_project.repository.LibraryBookRepository;
import com.SE.final_project.repository.UserRepository;

@Service
public class LibraryService {

    private final LibraryBookRepository libraryBookRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public LibraryService(LibraryBookRepository libraryBookRepository,
                          UserRepository userRepository,
                          NotificationService notificationService) {
        this.libraryBookRepository = libraryBookRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    public List<LibraryBook> getAvailableBooks() {
        return libraryBookRepository.findByStatusOrderByCreatedAtDesc(LibraryBookStatus.AVAILABLE);
    }

    public List<LibraryBook> getBooksOwnedBy(String username) {
        User owner = requireUser(username);
        return libraryBookRepository.findByOwnerOrderByCreatedAtDesc(owner);
    }

    public List<LibraryBook> getBooksBorrowedBy(String username) {
        User borrower = requireUser(username);
        return libraryBookRepository.findByBorrowerOrderByCreatedAtDesc(borrower);
    }

    @Transactional
    public LibraryBook addBook(String ownerUsername, String title, String author, String courseCode, String conditionNote) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Book title is required.");
        }

        User owner = requireUser(ownerUsername);
        LibraryBook book = new LibraryBook(title.trim(), safe(author), safe(courseCode), safe(conditionNote), owner);
        return libraryBookRepository.save(book);
    }

    @Transactional
    public void borrowBook(String borrowerUsername, Long bookId, int returnInDays) {
        if (returnInDays < 1 || returnInDays > 60) {
            throw new IllegalArgumentException("Return duration must be 1 to 60 days.");
        }

        User borrower = requireUser(borrowerUsername);
        LibraryBook book = libraryBookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found."));

        if (book.getStatus() != LibraryBookStatus.AVAILABLE) {
            throw new IllegalArgumentException("Book is not available right now.");
        }
        if (book.getOwner().getUsername().equalsIgnoreCase(borrower.getUsername())) {
            throw new IllegalArgumentException("You cannot borrow your own book.");
        }

        book.borrow(borrower, LocalDate.now().plusDays(returnInDays));
        libraryBookRepository.save(book);

        notificationService.notifyUser(book.getOwner().getUsername(),
                "Book borrowed",
                borrower.getUsername() + " borrowed your book: " + book.getTitle(),
                NotificationType.LIBRARY,
                true);

        notificationService.notifyUser(borrower.getUsername(),
                "Borrow confirmed",
                "You borrowed " + book.getTitle() + ". Return by " + book.getDueDate() + ".",
                NotificationType.LIBRARY,
                true);
    }

    @Transactional
    public void requestReturn(String borrowerUsername, Long bookId) {
        User borrower = requireUser(borrowerUsername);
        LibraryBook book = libraryBookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found."));

        if (book.getBorrower() == null || !book.getBorrower().getUsername().equalsIgnoreCase(borrower.getUsername())) {
            throw new IllegalArgumentException("Only the current borrower can request return.");
        }

        book.requestReturn();
        libraryBookRepository.save(book);

        notificationService.notifyUser(book.getOwner().getUsername(),
                "Return requested",
                borrower.getUsername() + " requested return confirmation for " + book.getTitle(),
                NotificationType.LIBRARY,
                true);
    }

    @Transactional
    public void markReturned(String ownerUsername, Long bookId) {
        User owner = requireUser(ownerUsername);
        LibraryBook book = libraryBookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found."));

        if (!book.getOwner().getUsername().equalsIgnoreCase(owner.getUsername())) {
            throw new IllegalArgumentException("Only owner can mark return complete.");
        }

        String borrowerName = book.getBorrower() == null ? null : book.getBorrower().getUsername();
        book.markReturned();
        libraryBookRepository.save(book);

        if (borrowerName != null) {
            notificationService.notifyUser(borrowerName,
                    "Return completed",
                    "The owner has marked your borrowed book as returned: " + book.getTitle(),
                    NotificationType.LIBRARY,
                    true);
        }
    }

    private User requireUser(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("User not found: " + username);
        }
        return user;
    }

    private String safe(String value) {
        return value == null ? "" : value.trim();
    }
}
