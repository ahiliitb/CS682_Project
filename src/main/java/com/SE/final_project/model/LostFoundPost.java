package com.SE.final_project.model;

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
@Table(name = "lost_found_posts")
public class LostFoundPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String location;

    private String description;

    @Enumerated(EnumType.STRING)
    private LostFoundType type;

    private boolean resolved;

    private LocalDateTime createdAt;

    private LocalDateTime resolvedAt;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @ManyToOne
    @JoinColumn(name = "resolved_by_id")
    private User resolvedBy;

    public LostFoundPost() {
    }

    public LostFoundPost(String title, String location, String description, LostFoundType type, User owner) {
        this.title = title;
        this.location = location;
        this.description = description;
        this.type = type;
        this.owner = owner;
        this.resolved = false;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getLocation() {
        return location;
    }

    public String getDescription() {
        return description;
    }

    public LostFoundType getType() {
        return type;
    }

    public boolean isResolved() {
        return resolved;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public User getOwner() {
        return owner;
    }

    public User getResolvedBy() {
        return resolvedBy;
    }

    public void markResolved(User resolver) {
        this.resolved = true;
        this.resolvedAt = LocalDateTime.now();
        this.resolvedBy = resolver;
    }
}
