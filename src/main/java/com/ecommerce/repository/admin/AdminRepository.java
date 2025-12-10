package com.ecommerce.repository.admin;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ecommerce.models.admin.Admin;
import java.util.Optional;
import java.util.List;

public interface AdminRepository extends JpaRepository<Admin, Long> {

    // Find by email with all roles and permissions loaded
    @Query("SELECT DISTINCT a FROM Admin a " +
            "LEFT JOIN FETCH a.roles r " +
            "LEFT JOIN FETCH r.permissions " +
            "WHERE a.email = :email")
    Optional<Admin> findByEmail(@Param("email") String email);

    // Find by ID with all roles and permissions loaded
    @Query("SELECT DISTINCT a FROM Admin a " +
            "LEFT JOIN FETCH a.roles r " +
            "LEFT JOIN FETCH r.permissions " +
            "WHERE a.id = :id")
    Optional<Admin> findById(@Param("id") Long id);

    // Find all admins with roles and permissions
    @Query("SELECT DISTINCT a FROM Admin a " +
            "LEFT JOIN FETCH a.roles r " +
            "LEFT JOIN FETCH r.permissions")
    List<Admin> findAll();

    boolean existsByEmail(String email);
}
