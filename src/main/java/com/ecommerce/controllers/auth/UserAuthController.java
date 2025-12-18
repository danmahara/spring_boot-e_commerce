package com.ecommerce.controllers.auth;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.ecommerce.models.User;
import com.ecommerce.security.CustomUserDetailsService;
import com.ecommerce.services.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class UserAuthController {

    @Autowired
    UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    // ============ REGISTER METHOD ============

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();

        try {
            String fullName = request.get("fullName");
            String email = request.get("email");
            String password = request.get("password");
            String confirmPassword = request.get("confirmPassword");

            // Validate inputs
            if (fullName == null || fullName.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Full name is required");
                return ResponseEntity.badRequest().body(response);
            }

            if (email == null || email.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Email is required");
                return ResponseEntity.badRequest().body(response);
            }

            if (password == null || password.length() < 6) {
                response.put("success", false);
                response.put("message", "Password must be at least 6 characters");
                return ResponseEntity.badRequest().body(response);
            }

            // Check if passwords match
            if (!password.equals(confirmPassword)) {
                response.put("success", false);
                response.put("message", "Passwords do not match");
                return ResponseEntity.badRequest().body(response);
            }

            // Check if email already exists
            if (userService.existsByEmail(email)) {
                response.put("success", false);
                response.put("message", "Email already registered");
                return ResponseEntity.badRequest().body(response);
            }

            // Register the user
            userService.registerUser(fullName, email, password);

            response.put("success", true);
            response.put("message", "Registration successful");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Registration failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ============ LOGIN METHOD ============

    // @PostMapping("/login")
    // public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String,
    // String> request,
    // HttpSession session) {
    // Map<String, Object> response = new HashMap<>();

    // try {
    // String email = request.get("email");
    // String password = request.get("password");

    // // Validate inputs
    // if (email == null || email.trim().isEmpty()) {
    // response.put("success", false);
    // response.put("message", "Email is required");
    // return ResponseEntity.badRequest().body(response);
    // }

    // if (password == null || password.trim().isEmpty()) {
    // response.put("success", false);
    // response.put("message", "Password is required");
    // return ResponseEntity.badRequest().body(response);
    // }

    // // Find user
    // User user = userService.findByEmail(email);

    // if (user == null || !user.isStatus()) {
    // response.put("success", false);
    // response.put("message", "Invalid email or account inactive");
    // return ResponseEntity.badRequest().body(response);
    // }

    // // Check password
    // if (!passwordEncoder.matches(password, user.getPassword())) {
    // response.put("success", false);
    // response.put("message", "Incorrect password");
    // return ResponseEntity.badRequest().body(response);
    // }

    // // Successful login -> store in session
    // session.setAttribute("customer", user);

    // response.put("success", true);
    // response.put("message", "Login successful");
    // response.put("redirectUrl", "/user/dashboard");
    // return ResponseEntity.ok(response);

    // } catch (Exception e) {
    // response.put("success", false);
    // response.put("message", "Login failed: " + e.getMessage());
    // return
    // ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    // }
    // }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> request,
            HttpServletRequest httpRequest) {
        Map<String, Object> response = new HashMap<>();

        try {
            String email = request.get("email");
            String password = request.get("password");

            if (email == null || email.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Email is required");
                return ResponseEntity.badRequest().body(response);
            }

            if (password == null || password.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Password is required");
                return ResponseEntity.badRequest().body(response);
            }

            // Find user
            User user = userService.findByEmail(email);

            if (user == null || !user.isStatus()) {
                response.put("success", false);
                response.put("message", "Invalid email or account inactive");
                return ResponseEntity.badRequest().body(response);
            }

            // Check password
            if (!passwordEncoder.matches(password, user.getPassword())) {
                response.put("success", false);
                response.put("message", "Incorrect password");
                return ResponseEntity.badRequest().body(response);
            }

            // ✅ Authenticate with Spring Security
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null,
                    userDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authToken);

            // ✅ Save to session (important for persistence across requests)
            HttpSession session = httpRequest.getSession(true);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                    SecurityContextHolder.getContext());

            response.put("success", true);
            response.put("message", "Login successful");
            response.put("redirectUrl", "/user/dashboard");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Login failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // ============ DASHBOARD ============

    // @GetMapping("/user/dashboard")
    // public String dashboard(HttpSession session) {
    // User customer = (User) session.getAttribute("customer");
    // if (customer == null) {
    // return "redirect:/login";
    // }
    // return "pages/customer/dashboard";
    // }

    // ============ LOGOUT METHOD ============

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}