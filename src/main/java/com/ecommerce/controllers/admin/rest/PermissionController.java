package com.ecommerce.controllers.admin.rest;

import com.ecommerce.annotations.RequirePermission;
import com.ecommerce.dtos.PermissionDTO;
import com.ecommerce.services.admin.PermissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @GetMapping({ "/", "" })
    @RequirePermission("READ_ROLES")
    public ResponseEntity<?> getAllPermissions() {
        try {
            return ResponseEntity.ok(createSuccessResponse("Permissions retrieved successfully",
                    permissionService.getAllPermissions()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(createErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/with_roles")
    @RequirePermission("READ_ROLES")
    public ResponseEntity<?> getAllPermissionsWithRoles() {
        try {
            return ResponseEntity.ok(createSuccessResponse("Permissions with roles retrieved successfully",
                    permissionService.getAllPermissionsWithRoles()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(createErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    @RequirePermission("READ_ROLES")
    public ResponseEntity<?> getPermissionById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(createSuccessResponse("Permission retrieved successfully",
                    permissionService.getPermissionById(id)));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(createErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/resource/{resource}")
    @RequirePermission("READ_ROLES")
    public ResponseEntity<?> getPermissionsByResource(@PathVariable String resource) {
        try {
            return ResponseEntity.ok(createSuccessResponse("Permissions retrieved successfully",
                    permissionService.getPermissionsByResource(resource)));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(createErrorResponse(e.getMessage()));
        }
    }

    @PostMapping
    @RequirePermission("CREATE_ROLES")
    public ResponseEntity<?> createPermission(@Valid @RequestBody PermissionDTO dto) {
        try {
            PermissionDTO createdPermission = permissionService.createPermission(dto);
            return ResponseEntity.status(201)
                    .body(createSuccessResponse("Permission created successfully", createdPermission));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(createErrorResponse(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @RequirePermission("UPDATE_ROLES")
    public ResponseEntity<?> updatePermission(@PathVariable Long id, @Valid @RequestBody PermissionDTO dto) {
        try {
            PermissionDTO updatedPermission = permissionService.updatePermission(id, dto);
            return ResponseEntity.ok(createSuccessResponse("Permission updated successfully", updatedPermission));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(createErrorResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @RequirePermission("DELETE_ROLES")
    public ResponseEntity<?> deletePermission(@PathVariable Long id) {
        try {
            permissionService.deletePermission(id);
            return ResponseEntity.ok(createSuccessResponse("Permission deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(createErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/permission_counts")
    @RequirePermission("READ_ROLES")
    public ResponseEntity<?> countPermissions() {
        try {
            return ResponseEntity.ok(createSuccessResponse("Permission count retrieved",
                    permissionService.countPermissions()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(createErrorResponse(e.getMessage()));
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