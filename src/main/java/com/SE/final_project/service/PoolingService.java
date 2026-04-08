package com.SE.final_project.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.SE.final_project.model.NotificationType;
import com.SE.final_project.model.PoolingRequest;
import com.SE.final_project.model.User;
import com.SE.final_project.repository.PoolingRequestRepository;
import com.SE.final_project.repository.UserRepository;

@Service
public class PoolingService {

    private final PoolingRequestRepository poolingRequestRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public PoolingService(PoolingRequestRepository poolingRequestRepository, UserRepository userRepository,
                          NotificationService notificationService) {
        this.poolingRequestRepository = poolingRequestRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    public List<PoolingRequest> getAllRequests() {
        return poolingRequestRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<PoolingRequest> getRequestsCreatedBy(String username) {
        return getAllRequests().stream()
                .filter(request -> request.getCreator() != null && username.equalsIgnoreCase(request.getCreator().getUsername()))
                .toList();
    }

    public Set<Long> getJoinedRequestIds(String username) {
        User user = requireUser(username);
        Set<Long> requestIds = new HashSet<>();

        for (PoolingRequest request : getAllRequests()) {
            boolean joined = request.getParticipants().stream()
                    .anyMatch(participant -> participant.getUsername().equalsIgnoreCase(user.getUsername()));
            if (joined) {
                requestIds.add(request.getId());
            }
        }

        return requestIds;
    }

    @Transactional
    public PoolingRequest createRequest(String creatorUsername, String title, String origin, String destination,
                                        String departureInfo, String details) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Pooling title is required.");
        }
        if (origin == null || origin.isBlank()) {
            throw new IllegalArgumentException("Origin is required.");
        }
        if (destination == null || destination.isBlank()) {
            throw new IllegalArgumentException("Destination is required.");
        }

        User creator = requireUser(creatorUsername);
        PoolingRequest request = new PoolingRequest(title.trim(), safeText(origin), safeText(destination),
                safeText(departureInfo), safeText(details), creator);
        request.addParticipant(creator);
        return poolingRequestRepository.save(request);
    }

    @Transactional
    public PoolingRequest joinRequest(String username, Long requestId) {
        User user = requireUser(username);
        PoolingRequest request = poolingRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Pooling request not found."));

        boolean alreadyJoined = request.getParticipants().stream()
                .anyMatch(participant -> participant.getUsername().equalsIgnoreCase(user.getUsername()));
        if (alreadyJoined) {
            throw new IllegalArgumentException("You already joined this request.");
        }

        request.addParticipant(user);
        PoolingRequest updated = poolingRequestRepository.save(request);

        if (request.getCreator() != null && !request.getCreator().getUsername().equalsIgnoreCase(user.getUsername())) {
            notificationService.notifyUser(request.getCreator().getUsername(),
                    "Pooling request joined",
                    user.getUsername() + " joined your pooling request \"" + request.getTitle() + "\".",
                    NotificationType.POOLING);
        }

        return updated;
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
