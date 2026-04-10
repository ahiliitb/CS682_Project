package com.SE.final_project.dto;

import java.time.LocalDateTime;

public class AuctionListingDTO {
    private Long id;
    private String itemName;
    private String description;
    private double startPrice;
    private double currentBid;
    private LocalDateTime endsAt;
    private boolean active;
    private LocalDateTime createdAt;
    private Long sellerId;
    private String sellerName;
    private Long highestBidderId;
    private String highestBidderName;

    public AuctionListingDTO() {}

    public AuctionListingDTO(Long id, String itemName, String description, double startPrice, 
                             double currentBid, LocalDateTime endsAt, boolean active, 
                             LocalDateTime createdAt, Long sellerId, String sellerName, 
                             Long highestBidderId, String highestBidderName) {
        this.id = id;
        this.itemName = itemName;
        this.description = description;
        this.startPrice = startPrice;
        this.currentBid = currentBid;
        this.endsAt = endsAt;
        this.active = active;
        this.createdAt = createdAt;
        this.sellerId = sellerId;
        this.sellerName = sellerName;
        this.highestBidderId = highestBidderId;
        this.highestBidderName = highestBidderName;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getStartPrice() {
        return startPrice;
    }

    public void setStartPrice(double startPrice) {
        this.startPrice = startPrice;
    }

    public double getCurrentBid() {
        return currentBid;
    }

    public void setCurrentBid(double currentBid) {
        this.currentBid = currentBid;
    }

    public LocalDateTime getEndsAt() {
        return endsAt;
    }

    public void setEndsAt(LocalDateTime endsAt) {
        this.endsAt = endsAt;
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

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getSellerId() {
        return sellerId != null ? sellerId.toString() : null;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getHighestBidderId() {
        return highestBidderId != null ? highestBidderId.toString() : null;
    }

    public void setHighestBidderId(Long highestBidderId) {
        this.highestBidderId = highestBidderId;
    }

    public String getHighestBidderName() {
        return highestBidderName;
    }

    public void setHighestBidderName(String highestBidderName) {
        this.highestBidderName = highestBidderName;
    }
}
