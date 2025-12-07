package com.ecommerce.repository.admin;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.models.admin.Admin;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByEmail(String email);

    boolean existsByEmail(String email);
}
