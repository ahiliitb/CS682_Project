package com.SE.final_project.controller;

import com.SE.final_project.dto.AuctionListingDTO;
import com.SE.final_project.model.AuctionListing;
import com.SE.final_project.repository.AuctionListingRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auctions")
public class AuctionListingRestController {

    private final AuctionListingRepository auctionListingRepository;

    public AuctionListingRestController(AuctionListingRepository auctionListingRepository) {
        this.auctionListingRepository = auctionListingRepository;
    }

    /**
     * Get all active auctions as DTOs
     */
    @GetMapping
    public List<AuctionListingDTO> getAllAuctions() {
        return auctionListingRepository.findAll().stream()
                .filter(AuctionListing::isActive)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get auction by ID as DTO
     */
    @GetMapping("/{id}")
    public AuctionListingDTO getAuctionById(@PathVariable Long id) {
        AuctionListing auction = auctionListingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Auction not found with id: " + id));
        return convertToDTO(auction);
    }

    /**
     * Get auctions by seller username
     */
    @GetMapping("/seller/{sellerId}")
    public List<AuctionListingDTO> getAuctionsBySeller(@PathVariable String sellerId) {
        return auctionListingRepository.findAll().stream()
                .filter(auction -> auction.getSeller() != null && sellerId.equals(auction.getSeller().getId()))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get active auctions only
     */
    @GetMapping("/active")
    public List<AuctionListingDTO> getActiveAuctions() {
        return auctionListingRepository.findAll().stream()
                .filter(AuctionListing::isActive)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convert AuctionListing entity to AuctionListingDTO
     */
    private AuctionListingDTO convertToDTO(AuctionListing auction) {
        return new AuctionListingDTO(
                auction.getId(),
                auction.getItemName(),
                auction.getDescription(),
                auction.getStartPrice(),
                auction.getCurrentBid(),
                auction.getEndsAt(),
                auction.isActive(),
                auction.getCreatedAt(),
                auction.getSeller() != null ? auction.getSeller().getId() : null,
                auction.getSeller() != null ? auction.getSeller().getUsername() : null,
                auction.getHighestBidder() != null ? auction.getHighestBidder().getId() : null,
                auction.getHighestBidder() != null ? auction.getHighestBidder().getUsername() : null
        );
    }
}
