package com.SE.final_project.controller;

import com.SE.final_project.dto.ItemDTO;
import com.SE.final_project.model.Item;
import com.SE.final_project.repository.ItemRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/items")
public class ItemRestController {

    private final ItemRepository itemRepository;

    public ItemRestController(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    /**
     * Get all active items as DTOs
     */
    @GetMapping
    public List<ItemDTO> getAllItems() {
        return itemRepository.findAll().stream()
                .filter(Item::isActive)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get item by ID as DTO
     */
    @GetMapping("/{id}")
    public ItemDTO getItemById(@PathVariable Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Item not found with id: " + id));
        return convertToDTO(item);
    }

    /**
     * Search items by name
     */
    @GetMapping("/search")
    public List<ItemDTO> searchByName(@RequestParam String name) {
        return itemRepository.findAll().stream()
                .filter(item -> item.getName().toLowerCase().contains(name.toLowerCase()))
                .filter(Item::isActive)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get items by owner username
     */
    @GetMapping("/owner/{username}")
    public List<ItemDTO> getItemsByOwner(@PathVariable String username) {
        return itemRepository.findAll().stream()
                .filter(item -> username.equals(item.getOwnerUsername()))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convert Item entity to ItemDTO
     */
    private ItemDTO convertToDTO(Item item) {
        return new ItemDTO(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getPrice(),
                item.isActive(),
                item.getOwnerUsername(),
                item.getVisibility(),
                item.getCreatedAt(),
                item.getPhotos()
        );
    }
}
