package com.SE.final_project.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "library_books")
public class LibraryBook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String author;

    private String courseCode;

    private String conditionNote;

    @Enumerated(EnumType.STRING)
    private LibraryBookStatus status;

    private LocalDate dueDate;

    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @ManyToOne
    @JoinColumn(name = "borrower_id")
    private User borrower;

    public LibraryBook() {
    }

    public LibraryBook(String title, String author, String courseCode, String conditionNote, User owner) {
        this.title = title;
        this.author = author;
        this.courseCode = courseCode;
        this.conditionNote = conditionNote;
        this.owner = owner;
        this.status = LibraryBookStatus.AVAILABLE;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public String getConditionNote() {
        return conditionNote;
    }

    public LibraryBookStatus getStatus() {
        return status;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public User getOwner() {
        return owner;
    }

    public User getBorrower() {
        return borrower;
    }

    public void borrow(User user, LocalDate expectedReturnDate) {
        this.borrower = user;
        this.dueDate = expectedReturnDate;
        this.status = LibraryBookStatus.BORROWED;
    }

    public void requestReturn() {
        this.status = LibraryBookStatus.RETURN_REQUESTED;
    }

    public void markReturned() {
        this.borrower = null;
        this.dueDate = null;
        this.status = LibraryBookStatus.AVAILABLE;
    }
}
