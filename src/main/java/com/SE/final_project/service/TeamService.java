package com.SE.final_project.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.SE.final_project.model.NotificationType;
import com.SE.final_project.model.Team;
import com.SE.final_project.model.User;
import com.SE.final_project.repository.TeamRepository;
import com.SE.final_project.repository.UserRepository;

@Service
public class TeamService {

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public TeamService(TeamRepository teamRepository, UserRepository userRepository, NotificationService notificationService) {
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    public List<Team> getAllTeams() {
        return teamRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<Team> getTeamsCreatedBy(String username) {
        return getAllTeams().stream()
                .filter(team -> team.getCreator() != null && username.equalsIgnoreCase(team.getCreator().getUsername()))
                .toList();
    }

    public Set<Long> getJoinedTeamIds(String username) {
        User user = requireUser(username);
        Set<Long> teamIds = new HashSet<>();

        for (Team team : getAllTeams()) {
            boolean isMember = team.getMembers().stream()
                    .anyMatch(member -> member.getUsername().equalsIgnoreCase(user.getUsername()));
            if (isMember) {
                teamIds.add(team.getId());
            }
        }

        return teamIds;
    }

    public Team createTeam(String creatorUsername, String name, String courseCode, String description) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Team name is required.");
        }

        User creator = requireUser(creatorUsername);
        Team team = new Team(name.trim(), safeText(courseCode), safeText(description), creator);
        team.addMember(creator);
        return teamRepository.save(team);
    }

    @Transactional
    public Team joinTeam(String username, Long teamId) {
        User user = requireUser(username);
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("Team not found."));

        boolean isMember = team.getMembers().stream()
                .anyMatch(member -> member.getUsername().equalsIgnoreCase(user.getUsername()));
        if (isMember) {
            throw new IllegalArgumentException("You are already in this team.");
        }

        team.addMember(user);
        Team updated = teamRepository.save(team);

        if (team.getCreator() != null && !team.getCreator().getUsername().equalsIgnoreCase(user.getUsername())) {
            notificationService.notifyUser(team.getCreator().getUsername(),
                    "Team joined",
                    user.getUsername() + " joined your team \"" + team.getName() + "\".",
                    NotificationType.TEAM);
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