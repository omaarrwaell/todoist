package com.example.Controller;


import com.example.Service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user")
public class User {
    private final UserService userService;

    public User(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<com.example.Model.User> getUsers() {
        return userService.getAllUsers();
    }
}
