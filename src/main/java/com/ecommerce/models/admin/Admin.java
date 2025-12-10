package com.ecommerce.models.admin;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "admins")
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // IMPORTANT: Use EAGER fetch to load roles immediately
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinTable(name = "admin_roles", joinColumns = @JoinColumn(name = "admin_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    @Builder.Default
    private Set<Role> roles = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (roles == null) {
            roles = new HashSet<>();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Check if admin has specific permission
    public boolean hasPermission(String permissionName) {
        if (roles == null || roles.isEmpty()) {
            return false;
        }

        return roles.stream()
                .filter(role -> role != null && (role.getIsActive() == null || role.getIsActive()))
                .flatMap(role -> {
                    Set<Permission> perms = role.getPermissions();
                    return perms != null ? perms.stream() : java.util.stream.Stream.empty();
                })
                .filter(permission -> permission != null
                        && (permission.getIsActive() == null || permission.getIsActive()))
                .anyMatch(permission -> permission.getName() != null && permission.getName().equals(permissionName));
    }

    // Check if admin has specific role
    public boolean hasRole(String roleName) {
        if (roles == null || roles.isEmpty()) {
            return false;
        }

        return roles.stream()
                .filter(role -> role != null && (role.getIsActive() == null || role.getIsActive()))
                .anyMatch(role -> role.getName() != null && role.getName().equals(roleName));
    }

    // Check if admin has any of the given roles
    public boolean hasAnyRole(String... roleNames) {
        if (roles == null || roles.isEmpty()) {
            return false;
        }

        Set<String> names = Set.of(roleNames);
        return roles.stream()
                .filter(role -> role != null && (role.getIsActive() == null || role.getIsActive()))
                .map(Role::getName)
                .anyMatch(names::contains);
    }

    // Check if admin has all given permissions
    public boolean hasAllPermissions(String... permissionNames) {
        if (roles == null || roles.isEmpty()) {
            return false;
        }

        Set<String> required = Set.of(permissionNames);
        Set<String> userPerms = getAllPermissions().stream()
                .map(Permission::getName)
                .collect(java.util.stream.Collectors.toSet());

        return userPerms.containsAll(required);
    }

    // Get all permissions for this admin
    public Set<Permission> getAllPermissions() {
        if (roles == null || roles.isEmpty()) {
            return new HashSet<>();
        }

        return roles.stream()
                .filter(role -> role != null && (role.getIsActive() == null || role.getIsActive()))
                .flatMap(role -> {
                    Set<Permission> perms = role.getPermissions();
                    return perms != null ? perms.stream() : java.util.stream.Stream.empty();
                })
                .filter(permission -> permission != null
                        && (permission.getIsActive() == null || permission.getIsActive()))
                .collect(java.util.stream.Collectors.toSet());
    }

    // Get permission names
    public Set<String> getPermissionNames() {
        return getAllPermissions().stream()
                .map(Permission::getName)
                .collect(java.util.stream.Collectors.toSet());
    }

    public void addRole(Role role) {
        if (roles == null) {
            roles = new HashSet<>();
        }
        if (role != null) {
            roles.add(role);
        }
    }

    public void removeRole(Role role) {
        if (roles != null && role != null) {
            roles.remove(role);
        }
    }
}