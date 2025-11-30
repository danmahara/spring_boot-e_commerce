package com.ecommerce.services.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ecommerce.dtos.AdminLoginDTO;
import com.ecommerce.dtos.AdminRegisterDTO;
import com.ecommerce.models.admin.Admin;
import com.ecommerce.repository.admin.AdminRepository;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    public Admin register(AdminRegisterDTO dto) {
        Admin admin = Admin.builder()
                .fullName(dto.getFullName())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .build();

        return adminRepository.save(admin);
    }

    public Admin login(AdminLoginDTO dto) {

        Admin admin = adminRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        if (!passwordEncoder.matches(dto.getPassword(), admin.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        return admin;
    }
}
