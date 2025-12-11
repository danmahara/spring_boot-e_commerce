package com.ecommerce.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleDTO {
    private Long id;

    @NotBlank(message = "Name is required")
    @Pattern(regexp = "^[A-Z_]+$", message = "Name must contain only uppercase letters and underscores (e.g., PRODUCT_MANAGER)")
    private String name;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Status is required")
    private Boolean isActive;

    private Set<PermissionDTO> permissions;
}