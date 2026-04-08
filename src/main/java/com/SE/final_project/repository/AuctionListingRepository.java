package com.SE.final_project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.SE.final_project.model.AuctionListing;

public interface AuctionListingRepository extends JpaRepository<AuctionListing, Long> {

    List<AuctionListing> findAllByOrderByCreatedAtDesc();
}
