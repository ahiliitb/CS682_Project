package com.SE.final_project.service;

import org.springframework.stereotype.Service;

import com.SE.final_project.model.RelationType;
import com.SE.final_project.repository.ItemRepository;
import com.SE.final_project.repository.UserItemRelationRepository;
import com.SE.final_project.repository.UserRepository;

@Service
public class StatisticsService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final UserItemRelationRepository userItemRelationRepository;

    public StatisticsService(UserRepository userRepository,
                             ItemRepository itemRepository,
                             UserItemRelationRepository userItemRelationRepository) {
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.userItemRelationRepository = userItemRelationRepository;
    }

    public long getTotalUsers() {
        return userRepository.count();
    }

    public long getTotalItems() {
        return itemRepository.count();
    }

    public long getActiveItems() {
        return itemRepository.countByActiveTrue();
    }

    public long getBoughtCount() {
        return userItemRelationRepository.countByRelationType(RelationType.BOUGHT);
    }

    public long getSoldCount() {
        return userItemRelationRepository.countByRelationType(RelationType.SOLD);
    }

    public long getLostCount() {
        return userItemRelationRepository.countByRelationType(RelationType.LOST);
    }

    public long getAuctionedCount() {
        return userItemRelationRepository.countByRelationType(RelationType.AUCTIONED);
    }

    public long getCompletedTransactions() {
        return getBoughtCount() + getSoldCount();
    }

    public long getCampusActivityCount() {
        return getBoughtCount() + getSoldCount() + getLostCount() + getAuctionedCount();
    }
}