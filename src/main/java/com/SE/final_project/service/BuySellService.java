package com.SE.final_project.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.SE.final_project.model.Item;
import com.SE.final_project.model.RelationType;
import com.SE.final_project.model.User;
import com.SE.final_project.model.UserItemRelation;
import com.SE.final_project.repository.ItemRepository;
import com.SE.final_project.repository.UserItemRelationRepository;
import com.SE.final_project.repository.UserRepository;

@Service
public class BuySellService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final UserItemRelationRepository relationRepository;

    public BuySellService(ItemRepository itemRepository,
                          UserRepository userRepository,
                          UserItemRelationRepository relationRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.relationRepository = relationRepository;
    }

    public List<Item> getActiveListings() {
        return itemRepository.findByActiveTrueOrderByCreatedAtDesc();
    }

    public List<Item> getListingsPostedBy(String username) {
        User seller = requireUser(username);
        return relationRepository.findByUserAndRelationType(seller, RelationType.SOLD)
                .stream()
                .map(UserItemRelation::getItem)
                .toList();
    }

    @Transactional
    public Item createListing(String sellerUsername, String name, String description, double price) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Item name is required.");
        }
        if (price <= 0) {
            throw new IllegalArgumentException("Price must be greater than zero.");
        }

        User seller = requireUser(sellerUsername);
        Item item = new Item(name.trim(), safeText(description), price, List.of());
        Item savedItem = itemRepository.save(item);
        relationRepository.save(new UserItemRelation(seller, savedItem, RelationType.SOLD));
        return savedItem;
    }

    @Transactional
    public void buyItem(String buyerUsername, Long itemId) {
        User buyer = requireUser(buyerUsername);
        Item item = itemRepository.findByIdAndActiveTrue(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item is not available."));

        List<UserItemRelation> sellerRelations = relationRepository.findByItemAndRelationType(item, RelationType.SOLD);
        if (sellerRelations.isEmpty()) {
            throw new IllegalArgumentException("Listing has no seller record.");
        }

        User seller = sellerRelations.get(0).getUser();
        if (seller.getUsername().equalsIgnoreCase(buyer.getUsername())) {
            throw new IllegalArgumentException("You cannot buy your own listing.");
        }

        if (relationRepository.existsByUserAndItemAndRelationType(buyer, item, RelationType.BOUGHT)) {
            throw new IllegalArgumentException("You already bought this listing.");
        }

        item.setActive(false);
        itemRepository.save(item);
        relationRepository.save(new UserItemRelation(buyer, item, RelationType.BOUGHT));
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
