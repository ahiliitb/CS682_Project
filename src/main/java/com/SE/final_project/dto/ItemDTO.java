package com.SE.final_project.dto;

import com.SE.final_project.model.ListingVisibility;
import java.time.LocalDateTime;
import java.util.List;

public class ItemDTO {
    private Long id;
    private String name;
    private String description;
    private double price;
    private boolean active;
    private String ownerUsername;
    private ListingVisibility visibility;
    private LocalDateTime createdAt;
    private List<String> photos;

    public ItemDTO() {}

    public ItemDTO(Long id, String name, String description, double price, boolean active,
                   String ownerUsername, ListingVisibility visibility, LocalDateTime createdAt,
                   List<String> photos) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.active = active;
        this.ownerUsername = ownerUsername;
        this.visibility = visibility;
        this.createdAt = createdAt;
        this.photos = photos;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getOwnerUsername() {
        return ownerUsername;
    }

    public void setOwnerUsername(String ownerUsername) {
        this.ownerUsername = ownerUsername;
    }

    public ListingVisibility getVisibility() {
        return visibility;
    }

    public void setVisibility(ListingVisibility visibility) {
        this.visibility = visibility;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<String> getPhotos() {
        return photos;
    }

    public void setPhotos(List<String> photos) {
        this.photos = photos;
    }
}
