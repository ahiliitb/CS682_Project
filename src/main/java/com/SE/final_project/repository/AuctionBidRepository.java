package com.SE.final_project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.SE.final_project.model.AuctionBid;
import com.SE.final_project.model.AuctionListing;

public interface AuctionBidRepository extends JpaRepository<AuctionBid, Long> {

    List<AuctionBid> findTop5ByListingOrderByCreatedAtDesc(AuctionListing listing);
}
