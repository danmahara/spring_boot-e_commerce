package com.ecommerce.services.admin;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ecommerce.dtos.PermissionDTO;
import com.ecommerce.dtos.RoleDTO;
import com.ecommerce.models.admin.Role;
import com.ecommerce.repository.admin.RoleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionService permissionService;

    public List<RoleDTO> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public RoleDTO getRoleById(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        return convertToDTO(role);
    }

    public RoleDTO createRole(RoleDTO dto) {
        if (roleRepository.existsByName(dto.getName())) {
            throw new RuntimeException("Role already exists");
        }

        Role role = Role.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .isActive(true)
                .build();

        return convertToDTO(roleRepository.save(role));
    }

    public RoleDTO updateRole(Long id, RoleDTO dto) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        role.setName(dto.getName());
        role.setDescription(dto.getDescription());
        role.setIsActive(dto.getIsActive());

        return convertToDTO(roleRepository.save(role));
    }

    public void deleteRole(Long id) {
        roleRepository.deleteById(id);
    }

    public void assignPermissionToRole(Long roleId, Long permissionId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        var permission = permissionService.getPermissionEntity(permissionId);
        role.addPermission(permission);
        roleRepository.save(role);
    }

    public void removePermissionFromRole(Long roleId, Long permissionId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));

        var permission = permissionService.getPermissionEntity(permissionId);
        role.removePermission(permission);
        roleRepository.save(role);
    }

    public Role getRoleEntity(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Role not found"));
    }

    public Role findByName(String name) {
        return roleRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Role not found: " + name));
    }

    private RoleDTO convertToDTO(Role role) {
        return RoleDTO.builder()
                .id(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .isActive(role.getIsActive())
                .permissions(role.getPermissions().stream()
                        .map(p -> PermissionDTO.builder()
                                .id(p.getId())
                                .name(p.getName())
                                .description(p.getDescription())
                                .resource(p.getResource())
                                .action(p.getAction())
                                .isActive(p.getIsActive())
                                .build())
                        .collect(Collectors.toSet()))
                .build();
    }
}