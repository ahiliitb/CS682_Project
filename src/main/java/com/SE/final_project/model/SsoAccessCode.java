package com.SE.final_project.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "sso_access_codes")
public class SsoAccessCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;

    private LocalDateTime createdAt;

    private LocalDateTime expiresAt;

    private LocalDateTime usedAt;

    private String usedByEmail;

    @ManyToOne
    @JoinColumn(name = "created_by_id")
    private User createdBy;

    public SsoAccessCode() {
    }

    public SsoAccessCode(String code, LocalDateTime expiresAt, User createdBy) {
        this.code = code;
        this.createdAt = LocalDateTime.now();
        this.expiresAt = expiresAt;
        this.createdBy = createdBy;
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public LocalDateTime getUsedAt() {
        return usedAt;
    }

    public String getUsedByEmail() {
        return usedByEmail;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public boolean isUsable(LocalDateTime now) {
        return usedAt == null && expiresAt != null && expiresAt.isAfter(now);
    }

    public void markUsed(String email) {
        this.usedAt = LocalDateTime.now();
        this.usedByEmail = email == null ? "" : email.trim();
    }
}
