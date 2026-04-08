package com.SE.final_project.config;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.SE.final_project.model.User;
import com.SE.final_project.model.UserRole;
import com.SE.final_project.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Value("${app.security.admin-emails:}")
    private String adminEmailsConfig;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User appUser = userRepository.findByUsername(username);
        if (appUser == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }

        UserRole resolvedRole = resolveRoleForEmail(appUser.getEmail());
        if (appUser.getRole() != resolvedRole) {
            appUser.setRole(resolvedRole);
            userRepository.save(appUser);
        }

        return org.springframework.security.core.userdetails.User.builder()
                .username(appUser.getUsername())
                .password(appUser.getPassword())
            .roles(appUser.getRole() == null ? "STUDENT" : appUser.getRole().name())
                .build();
    }

    private UserRole resolveRoleForEmail(String email) {
        if (email == null) {
            return UserRole.STUDENT;
        }

        Set<String> adminEmails = Arrays.stream(adminEmailsConfig.split(","))
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        return adminEmails.contains(email.toLowerCase()) ? UserRole.ADMIN : UserRole.STUDENT;
    }
}
