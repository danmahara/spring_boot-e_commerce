package com.ecommerce.services.admin;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ecommerce.dtos.PermissionDTO;
import com.ecommerce.dtos.RoleDTO;
import com.ecommerce.models.admin.Permission;
import com.ecommerce.repository.admin.PermissionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final PermissionRepository permissionRepository;

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