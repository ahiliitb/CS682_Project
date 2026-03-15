package com.SE.final_project.model;

import jakarta.persistence.*;

@Entity
@Table(name = "item_photos")
public class ItemPhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String photoUrl;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    public ItemPhoto() {}

    public ItemPhoto(String photoUrl, Item item) {
        this.photoUrl = photoUrl;
        this.item = item;
    }

    public Long getId() {
        return id;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }
}