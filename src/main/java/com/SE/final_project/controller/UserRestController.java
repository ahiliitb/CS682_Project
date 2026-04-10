package com.SE.final_project.controller;

import com.SE.final_project.dto.UserDTO;
import com.SE.final_project.model.User;
import com.SE.final_project.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserRestController {

    private final UserRepository userRepository;

    public UserRestController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Get all active users as DTOs
     */
    @GetMapping
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get user by ID as DTO
     */
    @GetMapping("/{id}")
    public UserDTO getUserById(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
        return convertToDTO(user);
    }

    /**
     * Get user by username
     */
    @GetMapping("/username/{username}")
    public UserDTO getUserByUsername(@PathVariable String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("User not found with username: " + username);
        }
        return convertToDTO(user);
    }

    /**
     * Get user by email
     */
    @GetMapping("/email/{email}")
    public UserDTO getUserByEmail(@PathVariable String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new IllegalArgumentException("User not found with email: " + email);
        }
        return convertToDTO(user);
    }

    /**
     * Convert User entity to UserDTO
     */
    private UserDTO convertToDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole()
        );
    }
}
