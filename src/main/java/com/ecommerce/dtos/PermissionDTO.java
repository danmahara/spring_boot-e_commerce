package com.ecommerce.dtos;

import java.util.Set;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermissionDTO {

    private Long id;

    @NotBlank(message = "Permission name is required")
    @Pattern(regexp = "^[A-Z_]+$", message = "Permission name must contain only uppercase letters and underscores (e.g., CREATE_PRODUCT)")
    @Size(min = 3, max = 100, message = "Permission name must be between 3 and 100 characters")
    private String name;

    @NotBlank(message = "Description is required")
    @Size(min = 10, max = 500, message = "Description must be between 10 and 500 characters")
    private String description;

    @NotBlank(message = "Resource is required")
    @Pattern(regexp = "^[A-Z_]+$", message = "Resource must contain only uppercase letters and underscores (e.g., PRODUCT)")
    @Size(min = 2, max = 50, message = "Resource must be between 2 and 50 characters")
    private String resource;

    @NotBlank(message = "Action is required")
    @Pattern(regexp = "^(CREATE|READ|UPDATE|DELETE)$", message = "Action must be one of: CREATE, READ, UPDATE, DELETE")
    private String action;

    @NotNull(message = "Status is required")
    private Boolean isActive;

    private Set<RoleDTO> roles;
}