package com.example.Model;

import java.time.LocalDateTime;
import java.util.UUID;

public class UserBuilder {
    //private final User user;
    UUID id;
    String role;
    String username;
    String email;
    String passwordHash;
    String firstName;
    String lastName;
    boolean isActive = true;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;

    String passwordResetToken;
    LocalDateTime passwordResetTokenExpiry;

//    public UserBuilder(User user) {
//
//        this.user = user;
//    }

    public UserBuilder id(UUID id) {
        this.id = id;
        return this;
    }

    public UserBuilder role(String role) {
        this.role = role;
        return this;
    }

    public UserBuilder username(String username) {
        this.username = username;
        return this;
    }

    public UserBuilder email(String email) {
        this.email = email;
        return this;
    }

    public UserBuilder passwordHash(String passwordHash) {
        this.passwordHash = passwordHash;
        return this;
    }

    public UserBuilder firstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public UserBuilder lastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public UserBuilder isActive(boolean isActive) {
        this.isActive = isActive;
        return this;
    }

    public UserBuilder createdAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public UserBuilder updatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public UserBuilder passwordResetToken(String token) {
        this.passwordResetToken = token;
        return this;
    }

    public UserBuilder passwordResetTokenExpiry(LocalDateTime expiry) {
        this.passwordResetTokenExpiry = expiry;
        return this;
    }


    public User build() {
        if (this.username == null || this.username.trim().isEmpty()) {
            throw new IllegalStateException("Username cannot be null or empty for User creation.");
        }
        if (this.email == null || this.email.trim().isEmpty()) {
            throw new IllegalStateException("Email cannot be null or empty for User creation.");
        }
        if (this.passwordHash == null || this.passwordHash.trim().isEmpty()) {
            throw new IllegalStateException("Password hash cannot be null or empty for User creation.");
        }

        return new User(this);
    }
}