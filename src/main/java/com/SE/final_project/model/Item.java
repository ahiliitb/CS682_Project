package com.SE.final_project.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "items")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(length = 2000)
    private String description;

    private double price;

    private boolean active;

    private String ownerUsername;

    @Enumerated(EnumType.STRING)
    private ListingVisibility visibility;

    private LocalDateTime createdAt;

    // Store multiple photo URLs (separate table to avoid conflict with ItemPhoto entity)
    @ElementCollection
    @CollectionTable(
        name = "item_photo_urls",
        joinColumns = @JoinColumn(name = "item_id")
    )
    @Column(name = "photo_url")
    private List<String> photos;

    public Item() {}

    public Item(String name, String description, double price, List<String> photos) {
        this(name, description, price, photos, null, ListingVisibility.PUBLIC);
    }

    public Item(String name, String description, double price, List<String> photos, String ownerUsername, ListingVisibility visibility) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.photos = photos;
        this.ownerUsername = ownerUsername;
        this.visibility = visibility == null ? ListingVisibility.PUBLIC : visibility;
        this.active = true;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
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

    public List<String> getPhotos() {
        return photos;
    }

    public void setPhotos(List<String> photos) {
        this.photos = photos;
    }
}