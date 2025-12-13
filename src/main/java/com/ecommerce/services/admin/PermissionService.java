package com.ecommerce.services.admin;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ecommerce.dtos.PermissionDTO;
import com.ecommerce.dtos.RoleDTO;
import com.ecommerce.models.admin.Permission;
import com.ecommerce.models.admin.Role;
import com.ecommerce.repository.admin.PermissionRepository;
import com.ecommerce.repository.admin.RoleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;

    public List<PermissionDTO> getAllPermissions() {

        return permissionRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

    }

    public List<PermissionDTO> getAllPermissionsWithRoles() {

        return permissionRepository.findAll().stream()
                .map(this::convertToDTOWithRoles)
                .collect(Collectors.toList());

    }

    public List<PermissionDTO> getPermissionsByResource(String resource) {
        return permissionRepository.findByResource(resource).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public PermissionDTO getPermissionById(Long id) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Permission not found"));
        return convertToDTO(permission);
    }

    public PermissionDTO createPermission(PermissionDTO dto) {
        if (permissionRepository.existsByName(dto.getName())) {
            throw new RuntimeException("Permission already exists");
        }

        Permission permission = Permission.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .resource(dto.getResource())
                .action(dto.getAction())
                .isActive(true)
                .build();

        return convertToDTO(permissionRepository.save(permission));
    }

    public PermissionDTO updatePermission(Long id, PermissionDTO dto) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Permission not found"));

        permission.setDescription(dto.getDescription());
        permission.setIsActive(dto.getIsActive());

        return convertToDTO(permissionRepository.save(permission));
    }

    public void deletePermission(Long id) {
        permissionRepository.deleteById(id);
    }

    public Permission getPermissionEntity(Long id) {
        return permissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Permission not found"));
    }

    public Long countPermissions() {
        return permissionRepository.count();
    }

    public Permission toggleStatus(Long id) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Permission not found"));
        permission.setIsActive(!permission.getIsActive());
        return permissionRepository.save(permission);
    }

    // Replace your assignRoles method in PermissionService with this:

    // @Transactional
    public void assignRoles(Long permissionId, List<Long> roleIds) {
        // Find the permission
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new RuntimeException("Permission not found with id: " + permissionId));

        // Find all new roles by IDs
        List<Role> newRoles = roleRepository.findAllById(roleIds);

        if (newRoles.size() != roleIds.size()) {
            throw new RuntimeException("Some roles were not found");
        }

        // CRITICAL FIX: Since Role is the owning side, we need to update from the Role
        // side

        // Step 1: Remove this permission from all roles that currently have it
        Set<Role> existingRoles = new HashSet<>(permission.getRoles());
        for (Role role : existingRoles) {
            role.getPermissions().remove(permission);
            roleRepository.save(role); // Save to persist the removal
        }

        // Step 2: Add this permission to all new roles
        for (Role role : newRoles) {
            if (!role.getPermissions().contains(permission)) {
                role.getPermissions().add(permission);
                roleRepository.save(role); // Save to persist the addition
            }
        }

        System.out
                .println("Successfully assigned " + newRoles.size() + " roles to permission: " + permission.getName());
    }

    // ALTERNATIVE APPROACH - More efficient with fewer DB queries:

    // @Transactional
    public void assignRolesEfficient(Long permissionId, List<Long> roleIds) {
        // Find the permission
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new RuntimeException("Permission not found with id: " + permissionId));

        // Find ALL roles (to handle both removal and addition)
        List<Role> allRoles = roleRepository.findAll();

        // Find the new roles that should have this permission
        Set<Long> newRoleIdSet = new HashSet<>(roleIds);

        for (Role role : allRoles) {
            boolean shouldHavePermission = newRoleIdSet.contains(role.getId());
            boolean currentlyHasPermission = role.getPermissions().contains(permission);

            if (shouldHavePermission && !currentlyHasPermission) {
                // Add permission to this role
                role.getPermissions().add(permission);
            } else if (!shouldHavePermission && currentlyHasPermission) {
                // Remove permission from this role
                role.getPermissions().remove(permission);
            }
        }

        // Save all roles at once
        roleRepository.saveAll(allRoles);

        System.out.println("Successfully assigned " + roleIds.size() + " roles to permission: " + permission.getName());
    }

    private PermissionDTO convertToDTO(Permission permission) {
        return PermissionDTO.builder()
                .id(permission.getId())
                .name(permission.getName())
                .description(permission.getDescription())
                .resource(permission.getResource())
                .action(permission.getAction())
                .isActive(permission.getIsActive())
                .build();
    }

    private PermissionDTO convertToDTOWithRoles(Permission permission) {
        PermissionDTO dto = PermissionDTO.builder()
                .id(permission.getId())
                .name(permission.getName())
                .description(permission.getDescription())
                .resource(permission.getResource())
                .action(permission.getAction())
                .isActive(permission.getIsActive())
                .build();

        // map roles
        Set<RoleDTO> roleDTOs = permission.getRoles().stream()
                .map(role -> RoleDTO.builder()
                        .id(role.getId())
                        .name(role.getName())
                        .description(role.getDescription())
                        .isActive(role.getIsActive())
                        .build())
                .collect(Collectors.toSet());

        dto.setRoles(roleDTOs);

        return dto;
    }

}