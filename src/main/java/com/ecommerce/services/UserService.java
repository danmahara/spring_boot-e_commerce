package com.ecommerce.services;

import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ecommerce.models.User;
import com.ecommerce.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ============ REGISTER METHODS ============

    /**
     * Check if email already exists in database
     */
    public boolean existsByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    /**
     * Register a new user
     */
    @Transactional
    public User registerUser(String fullName, String email, String password) {
        // Create new user
        User user = new User();
        user.setFullName(fullName);
        user.setEmail(email);

        // Encode password before saving
        user.setPassword(passwordEncoder.encode(password));

        // Set default values
        user.setStatus(true); // Active account
        user.setRole("CUSTOMER"); // Default role
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        // Save and return user
        return userRepository.save(user);
    }

    // ============ EXISTING LOGIN METHODS ============

    /**
     * Find user by email (for login)
     */
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    /**
     * Find user by ID
     */
    public User findUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    /**
     * Update user information
     */
    @Transactional
    public User updateUser(User user) {
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    /**
     * Delete user (soft delete by setting status to false)
     */
    @Transactional
    public void deleteUser(Long id) {
        User user = findUserById(id);
        if (user != null) {
            user.setStatus(false);
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);
        }
    }
}