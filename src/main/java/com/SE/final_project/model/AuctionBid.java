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
@Table(name = "auction_bids")
public class AuctionBid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double amount;

    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "listing_id")
    private AuctionListing listing;

    @ManyToOne
    @JoinColumn(name = "bidder_id")
    private User bidder;

    public AuctionBid() {
    }

    public AuctionBid(AuctionListing listing, User bidder, double amount) {
        this.listing = listing;
        this.bidder = bidder;
        this.amount = amount;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public AuctionListing getListing() {
        return listing;
    }

    public User getBidder() {
        return bidder;
    }
}
