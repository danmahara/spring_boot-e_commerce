package com.ecommerce.services.admin;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ecommerce.dtos.AdminLoginDTO;
import com.ecommerce.dtos.AdminRegisterDTO;
import com.ecommerce.dtos.AdminResponseDTO;
import com.ecommerce.dtos.PermissionDTO;
import com.ecommerce.dtos.RoleDTO;
import com.ecommerce.models.admin.Admin;
import com.ecommerce.models.admin.Permission;
import com.ecommerce.models.admin.Role;
import com.ecommerce.repository.admin.AdminRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminService {

    private final AdminRepository adminRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    public Admin register(AdminRegisterDTO dto) {
        if (adminRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        Admin admin = Admin.builder()
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .fullName(dto.getFullName())
                .status("ACTIVE")
                .build();

        // Initialize roles set
        if (admin.getRoles() == null) {
            admin.setRoles(new HashSet<>());
        }

        // Assign default ADMIN role
        try {
            Role defaultRole = roleService.findByName("ADMIN");
            admin.addRole(defaultRole);
            log.info("Default ADMIN role assigned to new admin");
        } catch (Exception e) {
            log.warn("Default role 'ADMIN' not found: {}", e.getMessage());
        }

        Admin savedAdmin = adminRepository.save(admin);
        log.info("Admin registered successfully: {}", savedAdmin.getEmail());
        return savedAdmin;
    }

    /**
     * Login method - returns Admin with all roles and permissions loaded
     */
    public Admin login(AdminLoginDTO dto) {
        log.debug("Attempting to find admin by email: {}", dto.getEmail());

        Admin admin = adminRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> {
                    log.error("Admin not found with email: {}", dto.getEmail());
                    return new RuntimeException("Invalid email or password");
                });

        log.debug("Admin found: {}", admin.getEmail());

        if (!passwordEncoder.matches(dto.getPassword(), admin.getPassword())) {
            log.error("Password mismatch for admin: {}", admin.getEmail());
            throw new RuntimeException("Invalid email or password");
        }

        if (!"ACTIVE".equals(admin.getStatus())) {
            log.error("Admin account is not active: {}", admin.getEmail());
            throw new RuntimeException("Admin account is not active");
        }

        // Initialize roles if null
        if (admin.getRoles() == null) {
            log.warn("Admin roles is null, initializing empty set");
            admin.setRoles(new HashSet<>());
        }

        // Log loaded roles and permissions
        int roleCount = admin.getRoles().size();
        Set<Permission> permissions = admin.getAllPermissions();
        int permissionCount = permissions != null ? permissions.size() : 0;

        log.info("Admin {} logged in with {} roles", admin.getEmail(), roleCount);
        log.info("Admin {} has {} permissions", admin.getEmail(), permissionCount);

        // Log role names
        if (!admin.getRoles().isEmpty()) {
            String roleNames = admin.getRoles().stream()
                    .map(Role::getName)
                    .collect(Collectors.joining(", "));
            log.info("Admin roles: {}", roleNames);
        }

        // Log permission names
        if (!permissions.isEmpty()) {
            String permNames = permissions.stream()
                    .map(Permission::getName)
                    .collect(Collectors.joining(", "));
            log.info("Admin permissions: {}", permNames);
        }

        return admin;
    }

    public List<AdminResponseDTO> getAllAdmins() {
        return adminRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    public AdminResponseDTO getAdminById(Long id) {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
        return convertToResponseDTO(admin);
    }

    public Admin getAdminEntityById(Long id) {
        return adminRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
    }

    public Admin getAdminByEmailWithPermissions(String email) {
        return adminRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
    }

    public AdminResponseDTO getAdminByEmail(String email) {
        Admin admin = adminRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
        return convertToResponseDTO(admin);
    }

    public void assignRoleToAdmin(Long adminId, Long roleId) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        if (admin.getRoles() == null) {
            admin.setRoles(new HashSet<>());
        }

        Role role = roleService.getRoleEntity(roleId);
        admin.addRole(role);
        adminRepository.save(admin);

        log.info("Role {} assigned to admin {}", role.getName(), admin.getEmail());
    }

    public void removeRoleFromAdmin(Long adminId, Long roleId) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        Role role = roleService.getRoleEntity(roleId);
        admin.removeRole(role);
        adminRepository.save(admin);

        log.info("Role {} removed from admin {}", role.getName(), admin.getEmail());
    }

    public boolean hasPermission(Long adminId, String permissionName) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
        return admin.hasPermission(permissionName);
    }

    public boolean hasRole(Long adminId, String roleName) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
        return admin.hasRole(roleName);
    }

    public boolean hasAnyRole(Long adminId, String... roleNames) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
        return admin.hasAnyRole(roleNames);
    }

    public boolean hasAllPermissions(Long adminId, String... permissionNames) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
        return admin.hasAllPermissions(permissionNames);
    }

    public Set<Permission> getAdminPermissions(Long adminId) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
        return admin.getAllPermissions();
    }

    public void updateAdminStatus(Long adminId, String status) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
        admin.setStatus(status);
        adminRepository.save(admin);

        log.info("Admin {} status updated to {}", admin.getEmail(), status);
    }

    public void deleteAdmin(Long id) {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
        adminRepository.deleteById(id);

        log.info("Admin {} deleted", admin.getEmail());
    }

    private AdminResponseDTO convertToResponseDTO(Admin admin) {
        AdminResponseDTO dto = new AdminResponseDTO();
        dto.setId(admin.getId());
        dto.setEmail(admin.getEmail());
        dto.setFullName(admin.getFullName());
        dto.setStatus(admin.getStatus());

        // Safe null checking
        Set<RoleDTO> roleDTOs = new HashSet<>();
        if (admin.getRoles() != null && !admin.getRoles().isEmpty()) {
            roleDTOs = admin.getRoles().stream()
                    .map(role -> RoleDTO.builder()
                            .id(role.getId())
                            .name(role.getName())
                            .description(role.getDescription())
                            .isActive(role.getIsActive())
                            .permissions(role.getPermissions() != null ? role.getPermissions().stream()
                                    .map(permission -> PermissionDTO.builder()
                                            .id(permission.getId())
                                            .name(permission.getName())
                                            .description(permission.getDescription())
                                            .resource(permission.getResource())
                                            .action(permission.getAction())
                                            .isActive(permission.getIsActive())
                                            .build())
                                    .collect(Collectors.toSet()) : new HashSet<>())
                            .build())
                    .collect(Collectors.toSet());
        }
        dto.setRoles(roleDTOs);

        // Get all permission names safely
        Set<String> permissionNames = new HashSet<>();
        Set<Permission> permissions = admin.getAllPermissions();
        if (permissions != null && !permissions.isEmpty()) {
            permissionNames = permissions.stream()
                    .map(Permission::getName)
                    .collect(Collectors.toSet());
        }
        dto.setPermissions(permissionNames);

        return dto;
    }
}