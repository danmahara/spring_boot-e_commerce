package com.ecommerce.controllers.admin;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.dtos.AdminResponseDTO;
import com.ecommerce.services.admin.AdminService;
import com.ecommerce.services.admin.RoleService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/admins")
@RequiredArgsConstructor
public class RoleController {

    private final AdminService adminService;
    private final RoleService roleService;

    @GetMapping
    public ResponseEntity<?> getAllAdmins() {
        try {
            return ResponseEntity.ok(adminService.getAllAdmins());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(createErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/roles")
    public ResponseEntity<?> getAllRoles() {
        try {
            return ResponseEntity.ok(roleService.getAllRoles());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(createErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAdminById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(adminService.getAdminById(id));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(createErrorResponse(e.getMessage()));
        }
    }

    @PostMapping("/{adminId}/roles/{roleId}")
    public ResponseEntity<?> assignRoleToAdmin(
            @PathVariable Long adminId,
            @PathVariable Long roleId) {
        try {
            adminService.assignRoleToAdmin(adminId, roleId);
            AdminResponseDTO admin = adminService.getAdminById(adminId);
            return ResponseEntity.ok(createSuccessResponse("Role assigned to admin", admin));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(createErrorResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/{adminId}/roles/{roleId}")
    public ResponseEntity<?> removeRoleFromAdmin(
            @PathVariable Long adminId,
            @PathVariable Long roleId) {
        try {
            adminService.removeRoleFromAdmin(adminId, roleId);
            AdminResponseDTO admin = adminService.getAdminById(adminId);
            return ResponseEntity.ok(createSuccessResponse("Role removed from admin", admin));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(createErrorResponse(e.getMessage()));
        }
    }

    @PutMapping("/{adminId}/status")
    public ResponseEntity<?> updateAdminStatus(
            @PathVariable Long adminId,
            @RequestParam String status) {
        try {
            adminService.updateAdminStatus(adminId, status);
            AdminResponseDTO admin = adminService.getAdminById(adminId);
            return ResponseEntity.ok(createSuccessResponse("Admin status updated", admin));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(createErrorResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAdmin(@PathVariable Long id) {
        try {
            adminService.deleteAdmin(id);
            return ResponseEntity.ok(createSuccessResponse("Admin deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(createErrorResponse(e.getMessage()));
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