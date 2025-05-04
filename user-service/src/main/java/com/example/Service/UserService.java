package com.example.Service;

import com.example.Model.User;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import com.example.Repository.UserRepository;

import java.util.List;
import java.util.Optional;

/**
 * UserService class that provides user-related services.
 */
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Cacheable(value = "UserSession", key = "#id")
    public User getUserById(Integer id) {
        Optional<User> user = userRepository.findById(id);

        if (user.isPresent()) {
            return user.get();
        } else {
            throw new RuntimeException("User not found");
        }
    }

    @Cacheable(value = "UserSession", key = "#id")
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Cacheable(value = "UserSession", key = "#id")
    public User updateUser(Integer id, User user) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User existingUser = optionalUser.get();
            existingUser.setUsername(user.getUsername());
            existingUser.setEmail(user.getEmail());
            if (user.getFirstName() != null) {
                existingUser.setFirstName(user.getFirstName());
            }
            if (user.getLastName() != null) {
                existingUser.setLastName(user.getLastName());
            }
            return userRepository.save(existingUser);
        }
        else {
            throw new RuntimeException("User with id " + id + " not found");
        }
    }

    @CacheEvict(value = "UserSession", key = "#id")
    public void deleteUser(Integer id) {
        userRepository.deleteById(id);
    }



}


