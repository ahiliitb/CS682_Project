package com.SE.final_project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;

import com.SE.final_project.model.User;
import com.SE.final_project.repository.UserRepository;

public class Auth_controller {

    @Autowired
    UserRepository userRepository;

    @Autowired
    User user;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        userRepository.save(user);
        return "Registration Page";
    }

}
