package com.SE.final_project.model;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "pooling_requests")
public class PoolingRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String origin;

    private String destination;

    private String departureInfo;

    private String details;

    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "creator_id")
    private User creator;

    @ManyToMany
    @JoinTable(
            name = "pooling_participants",
            joinColumns = @JoinColumn(name = "request_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> participants = new LinkedHashSet<>();

    public PoolingRequest() {
    }

    public PoolingRequest(String title, String origin, String destination, String departureInfo, String details, User creator) {
        this.title = title;
        this.origin = origin;
        this.destination = destination;
        this.departureInfo = departureInfo;
        this.details = details;
        this.creator = creator;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getOrigin() {
        return origin;
    }

    public String getDestination() {
        return destination;
    }

    public String getDepartureInfo() {
        return departureInfo;
    }

    public String getDetails() {
        return details;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public User getCreator() {
        return creator;
    }

    public Set<User> getParticipants() {
        return participants;
    }

    public void addParticipant(User user) {
        this.participants.add(user);
    }
}
