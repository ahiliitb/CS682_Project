package com.SE.final_project.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.SE.final_project.model.AppNotification;
import com.SE.final_project.model.NotificationType;
import com.SE.final_project.model.User;
import com.SE.final_project.repository.NotificationRepository;
import com.SE.final_project.repository.UserRepository;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationService(NotificationRepository notificationRepository,
                               UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    public List<AppNotification> getRecentForUser(String username) {
        User user = requireUser(username);
        return notificationRepository.findTop15ByUserOrderByCreatedAtDesc(user);
    }

    public long getUnreadCount(String username) {
        User user = requireUser(username);
        return notificationRepository.countByUserAndReadFalse(user);
    }

    @Transactional
    public void markAsRead(String username, Long notificationId) {
        User user = requireUser(username);
        AppNotification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found."));
        if (!notification.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Not allowed to update this notification.");
        }
        notification.markRead();
        notificationRepository.save(notification);
    }

    @Transactional
    public void notifyUser(String username, String title, String message, NotificationType type) {
        User user = requireUser(username);
        AppNotification notification = new AppNotification(user, safe(title), safe(message), type);
        notificationRepository.save(notification);
    }

    private User requireUser(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("User not found: " + username);
        }
        return user;
    }

    private String safe(String value) {
        return value == null ? "" : value.trim();
    }
}
