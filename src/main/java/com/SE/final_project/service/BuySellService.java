package com.SE.final_project.service;

import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.SE.final_project.model.Item;
import com.SE.final_project.model.ListingVisibility;
import com.SE.final_project.model.NotificationType;
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
    private final NotificationService notificationService;

    public BuySellService(ItemRepository itemRepository,
                          UserRepository userRepository,
                          UserItemRelationRepository relationRepository,
                          NotificationService notificationService) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.relationRepository = relationRepository;
        this.notificationService = notificationService;
    }

    public List<Item> getActiveListings(String username) {
        return itemRepository.findByActiveTrueOrderByCreatedAtDesc().stream()
                .filter(item -> isVisibleTo(item, username))
                .toList();
    }

    public List<Item> getListingsPostedBy(String username) {
        User seller = requireUser(username);
        return relationRepository.findByUserAndRelationType(seller, RelationType.SOLD)
                .stream()
                .map(UserItemRelation::getItem)
                .toList();
    }

    public Double getSuggestedListingPrice() {
        List<Item> activeListings = itemRepository.findByActiveTrueOrderByCreatedAtDesc();
        // Return null (placeholder) if insufficient historical data
        if (activeListings.size() < 3) {
            return null;
        }

        double averagePrice = activeListings.stream()
                .map(Item::getPrice)
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);
        return Math.max(1.0, Math.round(averagePrice));
    }

    @Transactional
    public Item createListing(String sellerUsername, String name, String description, double price, String visibilityValue) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Item name is required.");
        }
        if (price <= 0) {
            throw new IllegalArgumentException("Price must be greater than zero.");
        }

        User seller = requireUser(sellerUsername);
        ListingVisibility visibility = parseVisibility(visibilityValue);
        Item item = new Item(name.trim(), safeText(description), price, List.of(), seller.getUsername(), visibility);
        Item savedItem = itemRepository.save(item);
        relationRepository.save(new UserItemRelation(seller, savedItem, RelationType.SOLD));
        return savedItem;
    }

    @Transactional
    public void buyItem(String buyerUsername, Long itemId) {
        User buyer = requireUser(buyerUsername);
        Item item = itemRepository.findByIdAndActiveTrue(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item is not available."));

        if (item.getVisibility() == ListingVisibility.PRIVATE
                && item.getOwnerUsername() != null
                && !item.getOwnerUsername().equalsIgnoreCase(buyer.getUsername())) {
            throw new IllegalArgumentException("This listing is private.");
        }

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

        notificationService.notifyUser(seller.getUsername(),
            "Listing sold",
            "Your item \"" + item.getName() + "\" was bought by " + buyer.getUsername() + ".",
            NotificationType.TRANSACTION);

        notificationService.notifyUser(buyer.getUsername(),
            "Purchase confirmed",
            "You bought \"" + item.getName() + "\" successfully.",
            NotificationType.TRANSACTION);
    }

    private boolean isVisibleTo(Item item, String username) {
        if (item.getVisibility() == null || item.getVisibility() == ListingVisibility.PUBLIC) {
            return true;
        }
        return username != null && item.getOwnerUsername() != null
                && item.getOwnerUsername().equalsIgnoreCase(username);
    }

    private ListingVisibility parseVisibility(String visibilityValue) {
        if (visibilityValue == null || visibilityValue.isBlank()) {
            return ListingVisibility.PUBLIC;
        }

        try {
            return ListingVisibility.valueOf(visibilityValue.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Unknown listing visibility: " + visibilityValue);
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
