package com.ecommerce.services.admin;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ecommerce.dtos.AdminLoginDTO;
import com.ecommerce.dtos.AdminRegisterDTO;
import com.ecommerce.dtos.AdminResponseDTO;
import com.ecommerce.dtos.RoleDTO;
import com.ecommerce.models.admin.Admin;
import com.ecommerce.models.admin.Role;
import com.ecommerce.repository.admin.AdminRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
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

        // Assign default ADMIN role
        try {
            Role defaultRole = roleService.findByName("ADMIN");
            admin.addRole(defaultRole);
        } catch (Exception e) {
            // If default role doesn't exist, continue without it
        }

        return adminRepository.save(admin);
    }

    public Admin login(AdminLoginDTO dto) {
        Admin admin = adminRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(dto.getPassword(), admin.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        if (!"ACTIVE".equals(admin.getStatus())) {
            throw new RuntimeException("Admin account is not active");
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

    public AdminResponseDTO getAdminByEmail(String email) {
        Admin admin = adminRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
        return convertToResponseDTO(admin);
    }

    public void assignRoleToAdmin(Long adminId, Long roleId) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        Role role = roleService.getRoleEntity(roleId);
        admin.addRole(role);
        adminRepository.save(admin);
    }

    public void removeRoleFromAdmin(Long adminId, Long roleId) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        Role role = roleService.getRoleEntity(roleId);
        admin.removeRole(role);
        adminRepository.save(admin);
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

    public void updateAdminStatus(Long adminId, String status) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
        admin.setStatus(status);
        adminRepository.save(admin);
    }

    public void deleteAdmin(Long id) {
        adminRepository.deleteById(id);
    }

    private AdminResponseDTO convertToResponseDTO(Admin admin) {
        AdminResponseDTO dto = new AdminResponseDTO();
        dto.setId(admin.getId());
        dto.setEmail(admin.getEmail());
        dto.setFullName(admin.getFullName());
        dto.setStatus(admin.getStatus());

        dto.setRoles(admin.getRoles().stream()
                .map(role -> RoleDTO.builder()
                        .id(role.getId())
                        .name(role.getName())
                        .description(role.getDescription())
                        .isActive(role.getIsActive())
                        .build())
                .collect(Collectors.toSet()));

        dto.setPermissions(admin.getPermissionNames());

        return dto;
    }
}