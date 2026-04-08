package com.SE.final_project.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "auction_listings")
public class AuctionListing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String itemName;

    private String description;

    private double startPrice;

    private double currentBid;

    private LocalDateTime endsAt;

    private boolean active;

    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "seller_id")
    private User seller;

    @ManyToOne
    @JoinColumn(name = "highest_bidder_id")
    private User highestBidder;

    public AuctionListing() {
    }

    public AuctionListing(String itemName, String description, double startPrice, LocalDateTime endsAt, User seller) {
        this.itemName = itemName;
        this.description = description;
        this.startPrice = startPrice;
        this.currentBid = startPrice;
        this.endsAt = endsAt;
        this.seller = seller;
        this.active = true;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getItemName() {
        return itemName;
    }

    public String getDescription() {
        return description;
    }

    public double getStartPrice() {
        return startPrice;
    }

    public double getCurrentBid() {
        return currentBid;
    }

    public LocalDateTime getEndsAt() {
        return endsAt;
    }

    public boolean isActive() {
        return active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public User getSeller() {
        return seller;
    }

    public User getHighestBidder() {
        return highestBidder;
    }

    public void placeBid(User bidder, double bidAmount) {
        this.currentBid = bidAmount;
        this.highestBidder = bidder;
    }

    public void closeIfExpired(LocalDateTime now) {
        if (active && endsAt != null && endsAt.isBefore(now)) {
            this.active = false;
        }
    }
}
