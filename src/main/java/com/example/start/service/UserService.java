package com.example.start.service;

import com.example.start.entity.User;

public interface UserService {
    User register(User user);
    User findById(Long id);
    User findByUsername(String username);
}