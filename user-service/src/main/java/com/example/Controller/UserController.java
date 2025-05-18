package com.example.Controller;


import com.example.Model.User;
import com.example.Service.UserService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated; // For validating request parameters
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users") // Base path for all user-related endpoints
@Validated // Enables validation of path variables and request parameters annotated with constraints
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // --- GET Endpoints ---

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        log.info("Received request to get all users");
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable UUID id) {
        log.info("Received request to get user by id: {}", id);
        User user = userService.getUserById(id);
        // ResourceNotFoundException will be handled by GlobalExceptionHandler
        return ResponseEntity.ok(user);
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<User> getUserByUsername(
            @PathVariable @NotBlank @Size(min = 3, max = 50) String username) {
        log.info("Received request to get user by username: {}", username);
        User user = userService.getUserByUsername(username);
        // ResourceNotFoundException will be handled by GlobalExceptionHandler
        return ResponseEntity.ok(user);
    }

    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsers(@RequestParam String searchTerm) {
        log.info("Received request to search users with term: {}", searchTerm);
        List<User> users = userService.searchUsers(searchTerm);
        return ResponseEntity.ok(users);
    }

    // --- POST Endpoint (User Creation) ---

    /**
     * Option 1: Taking individual parameters for user creation.
     * This avoids needing a @RequestBody User object with a plain password field.
     */

    @PostMapping
    public ResponseEntity<User> createUser(
            @RequestParam @NotBlank @Size(min = 3, max = 50) String username,
            @RequestParam @NotBlank @jakarta.validation.constraints.Email String email,
            @RequestParam @NotBlank @Size(min = 8, message = "Password must be at least 8 characters") String password,
            @RequestParam(required = false) @Size(max = 50) String firstName,
            @RequestParam(required = false) @Size(max = 50) String lastName,
            @RequestParam(required = false, defaultValue = "USER") @Size(max = 50) String role) {

        log.info("Received request to create user with username: {}", username);
        User createdUser = userService.createUser(username, email, password, firstName, lastName, role);
        // DuplicateResourceException will be handled by GlobalExceptionHandler
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(
            @RequestParam @NotBlank String username,
            @RequestParam @NotBlank String password) {
        log.info("Received login request for username: {}", username);
        String token = userService.loginUser(username, password);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logoutUser(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader
            /* HttpServletRequest request - if managing sessions directly */) {
        log.info("Received logout request");
        // Extract user identifier or token from request/security context
        // For simplicity, assuming a conceptual user ID might be available
        // UUID currentUserId = ... // This would come from the security context

        // userService.logoutUser(currentUserId); // Call service method (which is conceptual for now)

        // Client should clear its token/session
        return ResponseEntity.ok("Logged out successfully. Please clear any local tokens/sessions.");
    }

    @PostMapping("/password-reset/request")
    public ResponseEntity<String> requestPasswordReset(
            @RequestParam @NotBlank @jakarta.validation.constraints.Email String email) {
        String token = userService.requestPasswordReset(email);
        return ResponseEntity.ok("Password reset initiated.");
    }

    @PostMapping("/password-reset/confirm")
    public ResponseEntity<String> confirmPasswordReset(
            @RequestParam @NotBlank String token,
            @RequestParam @NotBlank @Size(min = 8, message = "New password must be at least 8 characters") String newPassword) {
        log.info("Received request to confirm password reset with token");
        userService.confirmPasswordReset(token, newPassword);
        return ResponseEntity.ok("Password has been successfully reset.");
    }

    /**
     * Option 2: Taking a User object as @RequestBody for creation.
     * IMPORTANT: If using this, the 'passwordHash' field in the incoming User JSON
     * should contain the PLAIN TEXT password. The UserService is responsible for hashing it.
     * Ensure your User model's passwordHash field can accept this input temporarily.
     *
     * @PostMapping("/v2") // Different path to avoid conflict
     * public ResponseEntity<User> createUserFromObject(@Valid @RequestBody User userRequest) {
     *     log.info("Received request to create user from object with username: {}", userRequest.getUsername());
     *     // The userService.createUser(User user) method MUST hash the password.
     *     // Ensure userRequest.getPasswordHash() contains the plain password from the client.
     *     User createdUser = userService.createUser(userRequest);
     *     return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
     * }
     */


    // --- PUT Endpoint (User Update) ---

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(
            @PathVariable UUID id,
            @Valid @RequestBody User userUpdates) { // User object with fields to update
        log.info("Received request to update user with id: {}", id);
        // The userUpdates object should only contain fields the client intends to change.
        // Null fields in userUpdates should ideally mean "no change" for that field.
        // The userService.updateUser method handles merging these changes.
        User updatedUser = userService.updateUser(id, userUpdates);
        // ResourceNotFoundException or DuplicateResourceException will be handled by GlobalExceptionHandler
        return ResponseEntity.ok(updatedUser);
    }

    // --- DELETE Endpoint ---

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        log.info("Received request to delete user with id: {}", id);
        userService.deleteUser(id);
        // ResourceNotFoundException will be handled by GlobalExceptionHandler
        return ResponseEntity.noContent().build(); // HTTP 204 No Content
    }

    // Note: Password change operations, account activation/deactivation
    // should typically be separate, more specific endpoints.
}