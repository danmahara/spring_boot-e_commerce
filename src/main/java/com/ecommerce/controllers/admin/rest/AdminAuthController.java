package com.ecommerce.controllers.admin.rest;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.annotations.RequirePermission;
import com.ecommerce.dtos.AdminLoginDTO;
import com.ecommerce.dtos.AdminRegisterDTO;
import com.ecommerce.models.admin.Admin;
import com.ecommerce.services.admin.AdminService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminAuthController {

    private final AdminService adminService;

    // @PostMapping("/register")
    // @RequireRole("SUPER_ADMIN") // Only SUPER_ADMIN can register new admins
    // public ResponseEntity<?> register(@RequestBody AdminRegisterDTO dto) {
    // try {
    // Admin admin = adminService.register(dto);
    // return ResponseEntity.status(201)
    // .body(createSuccessResponse("Admin registered successfully",
    // admin.getEmail()));
    // } catch (Exception e) {
    // return ResponseEntity.status(400)
    // .body(createErrorResponse(e.getMessage()));
    // }
    // }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AdminRegisterDTO dto, HttpSession session) {
        try {
            // Check if registering admin exists and has SUPER_ADMIN role (optional)
            Admin currentAdmin = (Admin) session.getAttribute("admin");

            // Uncomment below if you want to restrict registration to logged-in SUPER_ADMIN
            // only
            /*
             * if (currentAdmin == null) {
             * // Allow if it's the first admin being registered
             * long adminCount = adminService.getTotalAdminCount();
             * if (adminCount > 0) {
             * return ResponseEntity.status(403)
             * .body(createErrorResponse("Unauthorized: Must be logged in as SUPER_ADMIN"));
             * }
             * } else if (!currentAdmin.hasRole("SUPER_ADMIN")) {
             * return ResponseEntity.status(403)
             * .body(createErrorResponse("Access Denied: SUPER_ADMIN role required"));
             * }
             */

            Admin admin = adminService.register(dto);
            return ResponseEntity.status(201)
                    .body(createSuccessResponse("Admin registered successfully", admin.getEmail()));
        } catch (Exception e) {
            return ResponseEntity.status(400)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AdminLoginDTO dto) {
        try {
            Admin admin = adminService.login(dto);

            Map<String, Object> adminData = new HashMap<>();
            adminData.put("id", admin.getId());
            adminData.put("fullName", admin.getFullName());
            adminData.put("email", admin.getEmail());
            adminData.put("roles", admin.getRoles().stream()
                    .map(r -> r.getName())
                    .toList());
            adminData.put("permissions", admin.getPermissionNames());

            return ResponseEntity.ok(createSuccessResponse("Admin logged in successfully", adminData));
        } catch (Exception e) {
            return ResponseEntity.status(401)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @RequirePermission("DELETE_ADMIN") // Requires DELETE_ADMIN permission
    public ResponseEntity<?> deleteAdmin(@PathVariable Long id) {
        try {
            adminService.deleteAdmin(id);
            return ResponseEntity.ok(createSuccessResponse("Admin deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.status(400)
                    .body(createErrorResponse(e.getMessage()));
        }
    }

    private Map<String, Object> createSuccessResponse(String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        response.put("data", data);
        return response;
    }

    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        return response;
    }
}