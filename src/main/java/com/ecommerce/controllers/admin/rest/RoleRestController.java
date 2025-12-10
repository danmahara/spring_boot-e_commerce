package com.ecommerce.controllers.admin.rest;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.annotations.RequirePermission;
import com.ecommerce.dtos.RoleDTO;
import com.ecommerce.services.admin.RoleService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/roles")
@RequiredArgsConstructor
public class RoleRestController {

    private final RoleService roleService;

    @GetMapping({ "/", "" })
    @RequirePermission("READ_ROLES")
    public ResponseEntity<?> getAllRoles() {
        System.out.println("READING ROLES");
        try {
            return ResponseEntity.ok(createSuccessResponse("Roles retrieved successfully", roleService.getAllRoles()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(createErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    @RequirePermission("READ_ROLES")
    public ResponseEntity<?> getRoleById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(createSuccessResponse("Role retrieved successfully", roleService.getRoleById(id)));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(createErrorResponse(e.getMessage()));
        }
    }

    @PostMapping
    @RequirePermission("CREATE_ROLES")
    public ResponseEntity<?> createRole(@Valid @RequestBody RoleDTO roleDTO) {
        try {
            RoleDTO createdRole = roleService.createRole(roleDTO);
            return ResponseEntity.status(201)
                    .body(createSuccessResponse("Role created successfully", createdRole));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(createErrorResponse(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @RequirePermission("UPDATE_ROLES")
    public ResponseEntity<?> updateRole(@PathVariable Long id, @Valid @RequestBody RoleDTO roleDTO) {
        try {
            RoleDTO updatedRole = roleService.updateRole(id, roleDTO);
            return ResponseEntity.ok(createSuccessResponse("Role updated successfully", updatedRole));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(createErrorResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @RequirePermission("DELETE_ROLES")
    public ResponseEntity<?> deleteRole(@PathVariable Long id) {
        try {
            roleService.deleteRole(id);
            return ResponseEntity.ok(createSuccessResponse("Role deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(createErrorResponse(e.getMessage()));
        }
    }

    @PostMapping("/{roleId}/permissions/{permissionId}")
    @RequirePermission("UPDATE_ROLES")
    public ResponseEntity<?> assignPermissionToRole(
            @PathVariable Long roleId,
            @PathVariable Long permissionId) {
        try {
            roleService.assignPermissionToRole(roleId, permissionId);
            RoleDTO role = roleService.getRoleById(roleId);
            return ResponseEntity.ok(createSuccessResponse("Permission assigned to role", role));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(createErrorResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/{roleId}/permissions/{permissionId}")
    @RequirePermission("UPDATE_ROLES")
    public ResponseEntity<?> removePermissionFromRole(
            @PathVariable Long roleId,
            @PathVariable Long permissionId) {
        try {
            roleService.removePermissionFromRole(roleId, permissionId);
            RoleDTO role = roleService.getRoleById(roleId);
            return ResponseEntity.ok(createSuccessResponse("Permission removed from role", role));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(createErrorResponse(e.getMessage()));
        }
    }

    @GetMapping("/count")
    // @RequirePermission("READ_ROLES")
    public ResponseEntity<?> countRoles() {
        try {
            return ResponseEntity.ok(createSuccessResponse("Role count retrieved", roleService.countRoles()));
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