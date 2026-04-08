package com.SE.final_project.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.SE.final_project.model.Item;
import com.SE.final_project.model.LostFoundPost;
import com.SE.final_project.model.LostFoundType;
import com.SE.final_project.model.NotificationType;
import com.SE.final_project.model.RelationType;
import com.SE.final_project.model.User;
import com.SE.final_project.model.UserItemRelation;
import com.SE.final_project.repository.ItemRepository;
import com.SE.final_project.repository.LostFoundPostRepository;
import com.SE.final_project.repository.UserItemRelationRepository;
import com.SE.final_project.repository.UserRepository;

@Service
public class LostFoundService {

    private final LostFoundPostRepository postRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final UserItemRelationRepository relationRepository;
    private final NotificationService notificationService;

    public LostFoundService(LostFoundPostRepository postRepository,
                            UserRepository userRepository,
                            ItemRepository itemRepository,
                            UserItemRelationRepository relationRepository,
                            NotificationService notificationService) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.relationRepository = relationRepository;
        this.notificationService = notificationService;
    }

    public List<LostFoundPost> getAllPosts() {
        return postRepository.findAllByOrderByCreatedAtDesc();
    }

    @Transactional
    public LostFoundPost createPost(String username, String type, String title, String location, String description) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title is required.");
        }

        LostFoundType postType;
        try {
            postType = LostFoundType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Invalid post type.");
        }

        User owner = requireUser(username);
        LostFoundPost post = new LostFoundPost(title.trim(), safeText(location), safeText(description), postType, owner);
        LostFoundPost savedPost = postRepository.save(post);

        // Track lost/found activity in the common item relation timeline used by stats.
        Item trackingItem = new Item("Lost/Found: " + title.trim(), safeText(description), 0.0, List.of());
        trackingItem.setActive(false);
        Item savedItem = itemRepository.save(trackingItem);
        relationRepository.save(new UserItemRelation(owner, savedItem, RelationType.LOST));

        return savedPost;
    }

    @Transactional
    public void resolvePost(String username, Long postId) {
        User resolver = requireUser(username);
        LostFoundPost post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found."));

        if (post.isResolved()) {
            throw new IllegalArgumentException("This post is already resolved.");
        }
        if (post.getOwner().getUsername().equalsIgnoreCase(resolver.getUsername())) {
            throw new IllegalArgumentException("Another user must confirm this resolution.");
        }

        post.markResolved(resolver);
        postRepository.save(post);

        notificationService.notifyUser(post.getOwner().getUsername(),
            "Lost/Found update",
            "Your post \"" + post.getTitle() + "\" was marked resolved by " + resolver.getUsername() + ".",
            NotificationType.INFO);
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
