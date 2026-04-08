package com.SE.final_project.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.SE.final_project.model.AuctionBid;
import com.SE.final_project.model.AuctionListing;
import com.SE.final_project.model.Item;
import com.SE.final_project.model.NotificationType;
import com.SE.final_project.model.RelationType;
import com.SE.final_project.model.User;
import com.SE.final_project.model.UserItemRelation;
import com.SE.final_project.repository.AuctionBidRepository;
import com.SE.final_project.repository.AuctionListingRepository;
import com.SE.final_project.repository.ItemRepository;
import com.SE.final_project.repository.UserItemRelationRepository;
import com.SE.final_project.repository.UserRepository;

@Service
public class AuctionService {

    private final AuctionListingRepository listingRepository;
    private final AuctionBidRepository bidRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final UserItemRelationRepository relationRepository;
    private final NotificationService notificationService;

    public AuctionService(AuctionListingRepository listingRepository,
                          AuctionBidRepository bidRepository,
                          UserRepository userRepository,
                          ItemRepository itemRepository,
                          UserItemRelationRepository relationRepository,
                          NotificationService notificationService) {
        this.listingRepository = listingRepository;
        this.bidRepository = bidRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.relationRepository = relationRepository;
        this.notificationService = notificationService;
    }

    public List<AuctionListing> getAllListings() {
        List<AuctionListing> listings = listingRepository.findAllByOrderByCreatedAtDesc();
        LocalDateTime now = LocalDateTime.now();
        for (AuctionListing listing : listings) {
            listing.closeIfExpired(now);
        }
        return listingRepository.saveAll(listings);
    }

    public List<AuctionBid> getRecentBids(AuctionListing listing) {
        return bidRepository.findTop5ByListingOrderByCreatedAtDesc(listing);
    }

    @Transactional
    public AuctionListing createListing(String sellerUsername,
                                        String itemName,
                                        String description,
                                        double startPrice,
                                        int durationHours) {
        if (itemName == null || itemName.isBlank()) {
            throw new IllegalArgumentException("Auction item name is required.");
        }
        if (startPrice <= 0) {
            throw new IllegalArgumentException("Start price must be greater than zero.");
        }
        if (durationHours < 1 || durationHours > 168) {
            throw new IllegalArgumentException("Duration must be between 1 and 168 hours.");
        }

        User seller = requireUser(sellerUsername);
        AuctionListing listing = new AuctionListing(
                itemName.trim(),
                safeText(description),
                startPrice,
                LocalDateTime.now().plusHours(durationHours),
                seller);

        AuctionListing savedListing = listingRepository.save(listing);

        Item trackingItem = new Item("Auction: " + itemName.trim(), safeText(description), startPrice, List.of());
        trackingItem.setActive(false);
        Item savedItem = itemRepository.save(trackingItem);
        relationRepository.save(new UserItemRelation(seller, savedItem, RelationType.AUCTIONED));

        notificationService.notifyUser(seller.getUsername(),
            "Auction listed",
            "Your auction for \"" + itemName.trim() + "\" is now live.",
            NotificationType.AUCTION);

        return savedListing;
    }

    @Transactional
    public void placeBid(String bidderUsername, Long listingId, double amount) {
        User bidder = requireUser(bidderUsername);
        AuctionListing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new IllegalArgumentException("Auction listing not found."));

        listing.closeIfExpired(LocalDateTime.now());
        if (!listing.isActive()) {
            throw new IllegalArgumentException("This auction is closed.");
        }
        if (listing.getSeller().getUsername().equalsIgnoreCase(bidder.getUsername())) {
            throw new IllegalArgumentException("You cannot bid on your own auction.");
        }
        if (amount <= listing.getCurrentBid()) {
            throw new IllegalArgumentException("Bid must be greater than current bid.");
        }

        User previousHighestBidder = listing.getHighestBidder();
        listing.placeBid(bidder, amount);
        listingRepository.save(listing);
        bidRepository.save(new AuctionBid(listing, bidder, amount));

        notificationService.notifyUser(listing.getSeller().getUsername(),
            "New bid received",
            bidder.getUsername() + " bid Rs. " + amount + " on \"" + listing.getItemName() + "\".",
            NotificationType.AUCTION);

        if (previousHighestBidder != null && !previousHighestBidder.getUsername().equalsIgnoreCase(bidder.getUsername())) {
            notificationService.notifyUser(previousHighestBidder.getUsername(),
                "You were outbid",
                "Another bidder placed a higher amount on \"" + listing.getItemName() + "\".",
                NotificationType.AUCTION);
        }
    }

    private User requireUser(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("User not found: " + username);
        }
        return user;
    }

    private String safeText(String value) {
        return value == null ? "" : value.trim();
    }
}
