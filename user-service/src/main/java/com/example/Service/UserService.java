package com.example.Service;

import com.example.Model.User;
import com.example.Model.UserBuilder;
import com.example.Repository.UserRepository;
import com.example.Exception.ResourceNotFoundException;
import com.example.Exception.DuplicateResourceException;
import com.example.Exception.AuthenticationFailedException;
import com.example.Exception.InvalidTokenException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private static final long PASSWORD_RESET_TOKEN_VALIDITY_MINUTES = 60;


    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        log.info("Fetching all users");
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "UserCache", key = "#id")
    public User getUserById(UUID id) {
        log.info("Fetching user by id: {}", id);
        return userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User not found with id: {}", id);
                    return new ResourceNotFoundException("User not found with id: " + id);
                });
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "UserCache", key = "#username")
    public User getUserByUsername(String username) {
        log.info("Fetching user by username: {}", username);
        return (User) userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("User not found with username: {}", username);
                    return new ResourceNotFoundException("User not found with username: " + username);
                });
    }

    @Transactional
    // Key for @CachePut should ideally be the ID of the created user.
    // This requires the 'User' object returned by save() to have the ID.
    @CachePut(value = "UserCache", key = "#result.id")
    public User createUser(String username, String email, String plainPassword,
                           String firstName, String lastName, String role) {
        log.info("Creating new user with username: {}", username);

        if (userRepository.existsByUsername(username)) {
            log.warn("Username {} already exists", username);
            throw new DuplicateResourceException("Username already exists: " + username);
        }
        if (userRepository.existsByEmail(email)) {
            log.warn("Email {} already exists", email);
            throw new DuplicateResourceException("Email already exists: " + email);
        }

        User user = new UserBuilder()
                .username(username)
                .email(email)
                .passwordHash(passwordEncoder.encode(plainPassword)) // Hash the password
                .firstName(firstName)
                .lastName(lastName)
                .role(role)
                .isActive(true)
                .build();

        User savedUser = userRepository.save(user);
        log.info("User created successfully with id: {}", savedUser.getId());
        return savedUser;
    }

    /**
     * Alternative createUser method if you want to pass a User object from the controller.
     * The controller would construct a User object with a PLAINTEXT password.
     * The service is responsible for hashing it.
     */
    @Transactional
    @CachePut(value = "UserCache", key = "#result.id")
    public User createUser(User userWithPlainPassword) {
        log.info("Creating new user with username: {}", userWithPlainPassword.getUsername());

        if (userRepository.existsByUsername(userWithPlainPassword.getUsername())) {
            log.warn("Username {} already exists", userWithPlainPassword.getUsername());
            throw new DuplicateResourceException("Username already exists: " + userWithPlainPassword.getUsername());
        }
        if (userRepository.existsByEmail(userWithPlainPassword.getEmail())) {
            log.warn("Email {} already exists", userWithPlainPassword.getEmail());
            throw new DuplicateResourceException("Email already exists: " + userWithPlainPassword.getEmail());
        }

        // Create a new User entity or ensure the passed one is correctly built
        // and hash the password
        User userToSave = new UserBuilder()
                .username(userWithPlainPassword.getUsername())
                .email(userWithPlainPassword.getEmail())
                .passwordHash(passwordEncoder.encode(userWithPlainPassword.getPasswordHash())) // Assumes plain pass is in passwordHash field temporarily
                .firstName(userWithPlainPassword.getFirstName())
                .lastName(userWithPlainPassword.getLastName())
                .role(userWithPlainPassword.getRole())
                .isActive(userWithPlainPassword.isActive()) // Or set a default like true
                .build();
        // Ensure ID is null for creation if auto-generated
        // userToSave.setId(null); // If UserBuilder doesn't handle this

        User savedUser = userRepository.save(userToSave);
        log.info("User created successfully with id: {}", savedUser.getId());
        return savedUser;
    }


    @Transactional
    @CachePut(value = "UserCache", key = "#id")
    public User updateUser(UUID id, User userUpdates) { // userUpdates contains the new field values
        log.info("Updating user with id: {}", id);
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User not found for update with id: {}", id);
                    return new ResourceNotFoundException("User not found with id: " + id + " for update.");
                });

        // Apply updates from userUpdates to existingUser
        if (userUpdates.getEmail() != null && !userUpdates.getEmail().equalsIgnoreCase(existingUser.getEmail())) {
            if (userRepository.existsByEmailAndIdNot(userUpdates.getEmail(), id)) {
                log.warn("Email {} already exists for another user.", userUpdates.getEmail());
                throw new DuplicateResourceException("Email already exists: " + userUpdates.getEmail());
            }
            existingUser.setEmail(userUpdates.getEmail());
        }
        if (userUpdates.getFirstName() != null) {
            existingUser.setFirstName(userUpdates.getFirstName());
        }
        if (userUpdates.getLastName() != null) {
            existingUser.setLastName(userUpdates.getLastName());
        }
        if (userUpdates.getRole() != null) {
            existingUser.setRole(userUpdates.getRole());
        }
        // existingUser.setActive(userUpdates.isActive()); // If isActive is updatable

        // Password updates should be a separate, secure flow and not part of a general update.
        // Do NOT directly set passwordHash from userUpdates unless it's a specific password change operation
        // where userUpdates.getPasswordHash() contains a NEW PLAIN password to be hashed.

        User updatedUser = userRepository.save(existingUser);
        log.info("User updated successfully with id: {}", updatedUser.getId());
        return updatedUser;
    }

    @Transactional
    @CacheEvict(value = "UserCache", allEntries = false, key = "#id")
    public void deleteUser(UUID id) {
        log.info("Deleting user with id: {}", id);
        if (!userRepository.existsById(id)) {
            log.warn("User not found for deletion with id: {}", id);
            throw new ResourceNotFoundException("User not found with id: " + id + " for deletion.");
        }
        userRepository.deleteById(id);
        log.info("User deleted successfully with id: {}", id);
    }

    @Transactional(readOnly = true)
    public User loginUser(String username, String plainPassword) {
        log.info("Attempting login for user: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("Login failed: User not found - {}", username);
                    return new AuthenticationFailedException("Invalid username or password.");
                });

        if (!user.isActive()) {
            log.warn("Login failed: User account is not active - {}", username);
            throw new AuthenticationFailedException("User account is not active.");
        }

        if (!passwordEncoder.matches(plainPassword, user.getPasswordHash())) {
            log.warn("Login failed: Invalid password for user - {}", username);
            // Consider rate limiting or account locking after multiple failed attempts
            throw new AuthenticationFailedException("Invalid username or password.");
        }

        log.info("User {} logged in successfully", username);

        return user;
    }

    public void logoutUser(UUID userId /* or session/token to invalidate */) {
        log.info("User {} logging out", userId);

    }

    @Transactional
    public String requestPasswordReset(String email) {
        log.info("Password reset requested for email: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Password reset failed: No user found with email - {}", email);

                    return new ResourceNotFoundException("No user found with email: " + email);
                });

        String token = UUID.randomUUID().toString(); // Simple token generation
        user.setPasswordResetToken(token);
        user.setPasswordResetTokenExpiry(LocalDateTime.now().plusMinutes(PASSWORD_RESET_TOKEN_VALIDITY_MINUTES));
        userRepository.save(user);

        log.info("Password reset token generated for user {}. Token: {}", user.getUsername(), token);
        return token;
    }

    @Transactional
    @CacheEvict(value = "UserCache", key = "#result.id") // Evict user from cache as password changed
    public User confirmPasswordReset(String token, String newPassword) {
        log.info("Attempting to confirm password reset with token: {}", token);
        User user = userRepository.findByPasswordResetToken(token)
                .orElseThrow(() -> {
                    log.warn("Password reset failed: Invalid or non-existent token - {}", token);
                    return new InvalidTokenException("Invalid or expired password reset token.");
                });

        if (user.getPasswordResetTokenExpiry() == null || user.getPasswordResetTokenExpiry().isBefore(LocalDateTime.now())) {
            log.warn("Password reset failed: Token expired for user {} - token: {}", user.getUsername(), token);
            user.setPasswordResetToken(null); // Clear expired token
            user.setPasswordResetTokenExpiry(null);
            userRepository.save(user);
            throw new InvalidTokenException("Invalid or expired password reset token.");
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setPasswordResetToken(null); // Clear the token after successful use
        user.setPasswordResetTokenExpiry(null);
        User savedUser = userRepository.save(user);

        log.info("Password successfully reset for user: {}", user.getUsername());
        return savedUser;
    }

    @Transactional(readOnly = true)
    public List<User> searchUsers(String searchTerm) {
        log.info("Searching users with term: {}", searchTerm);
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return List.of(); // Or return all users, depending on desired behavior
        }
        return userRepository.searchUsers(searchTerm.trim());
    }
}