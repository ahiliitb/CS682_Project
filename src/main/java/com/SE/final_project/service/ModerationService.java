package com.SE.final_project.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.SE.final_project.model.ModerationReport;
import com.SE.final_project.model.ModerationStatus;
import com.SE.final_project.model.NotificationType;
import com.SE.final_project.model.User;
import com.SE.final_project.repository.ModerationReportRepository;
import com.SE.final_project.repository.UserRepository;

@Service
public class ModerationService {

    private final ModerationReportRepository moderationReportRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public ModerationService(ModerationReportRepository moderationReportRepository,
                             UserRepository userRepository,
                             NotificationService notificationService) {
        this.moderationReportRepository = moderationReportRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    public List<ModerationReport> getOpenReports() {
        return moderationReportRepository.findTop100ByStatusOrderByCreatedAtDesc(ModerationStatus.OPEN);
    }

    public List<ModerationReport> getRecentReports() {
        return moderationReportRepository.findTop100ByOrderByCreatedAtDesc();
    }

    @Transactional
    public ModerationReport createReport(String reporterUsername, String targetType, Long targetId, String reason) {
        if (targetType == null || targetType.isBlank()) {
            throw new IllegalArgumentException("Target type is required.");
        }
        if (targetId == null || targetId < 1) {
            throw new IllegalArgumentException("Valid target ID is required.");
        }
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("Reason is required.");
        }

        User reporter = requireUser(reporterUsername);
        ModerationReport report = new ModerationReport(reporter, targetType.trim().toUpperCase(), targetId, reason.trim());
        ModerationReport saved = moderationReportRepository.save(report);

        notificationService.notifyUser(reporter.getUsername(),
                "Report submitted",
                "Your report #" + saved.getId() + " has been submitted for moderator review.",
                NotificationType.MODERATION);

        return saved;
    }

    @Transactional
    public void resolveReport(String adminUsername, Long reportId, String note) {
        User admin = requireUser(adminUsername);
        ModerationReport report = moderationReportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Report not found."));
        report.resolve(admin, note);
        moderationReportRepository.save(report);
        notificationService.notifyUser(report.getReporter().getUsername(),
                "Report resolved",
                "Your moderation report #" + report.getId() + " was resolved by admin.",
                NotificationType.MODERATION);
    }

    @Transactional
    public void rejectReport(String adminUsername, Long reportId, String note) {
        User admin = requireUser(adminUsername);
        ModerationReport report = moderationReportRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Report not found."));
        report.reject(admin, note);
        moderationReportRepository.save(report);
        notificationService.notifyUser(report.getReporter().getUsername(),
                "Report reviewed",
                "Your moderation report #" + report.getId() + " was reviewed by admin.",
                NotificationType.MODERATION);
    }

    private User requireUser(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("User not found: " + username);
        }
        return user;
    }
}
