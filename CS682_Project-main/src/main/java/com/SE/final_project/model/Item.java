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

    private LocalDateTime createdAt;

    // Store multiple photo URLs
    @ElementCollection
    @CollectionTable(
        name = "item_photos",
        joinColumns = @JoinColumn(name = "item_id")
    )
    @Column(name = "photo_url")
    private List<String> photos;

    public Item() {}

    public Item(String name, String description, double price, List<String> photos) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.photos = photos;
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