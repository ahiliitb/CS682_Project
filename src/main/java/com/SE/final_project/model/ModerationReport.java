package com.SE.final_project.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "moderation_reports")
public class ModerationReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "reporter_id")
    private User reporter;

    private String targetType;

    private Long targetId;

    private String reason;

    @Enumerated(EnumType.STRING)
    private ModerationStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime reviewedAt;

    @ManyToOne
    @JoinColumn(name = "reviewed_by_id")
    private User reviewedBy;

    private String adminNote;

    public ModerationReport() {
    }

    public ModerationReport(User reporter, String targetType, Long targetId, String reason) {
        this.reporter = reporter;
        this.targetType = targetType;
        this.targetId = targetId;
        this.reason = reason;
        this.status = ModerationStatus.OPEN;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public User getReporter() {
        return reporter;
    }

    public String getTargetType() {
        return targetType;
    }

    public Long getTargetId() {
        return targetId;
    }

    public String getReason() {
        return reason;
    }

    public ModerationStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getReviewedAt() {
        return reviewedAt;
    }

    public User getReviewedBy() {
        return reviewedBy;
    }

    public String getAdminNote() {
        return adminNote;
    }

    public void resolve(User admin, String note) {
        this.status = ModerationStatus.RESOLVED;
        this.reviewedAt = LocalDateTime.now();
        this.reviewedBy = admin;
        this.adminNote = note == null ? "" : note.trim();
    }

    public void reject(User admin, String note) {
        this.status = ModerationStatus.REJECTED;
        this.reviewedAt = LocalDateTime.now();
        this.reviewedBy = admin;
        this.adminNote = note == null ? "" : note.trim();
    }
}
