package com.ecommerce.controllers.admin;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.dtos.AdminLoginDTO;
import com.ecommerce.dtos.AdminRegisterDTO;
import com.ecommerce.models.admin.Admin;
import com.ecommerce.services.admin.AdminService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AdminRegisterDTO dto) {
        Admin admin = adminService.register(dto);
        return ResponseEntity.ok("Admin registered successfully with email: " + admin.getEmail());
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AdminLoginDTO dto) {
        Admin admin = adminService.login(dto);

        // Prepare admin data (without password)
        Map<String, Object> adminData = new HashMap<>();
        adminData.put("id", admin.getId());
        adminData.put("fullName", admin.getFullName());
        adminData.put("email", admin.getEmail());

        // Prepare full response
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Admin logged in successfully");
        response.put("data", adminData);

        return ResponseEntity.ok(response); // HTTP 200
    }

}
