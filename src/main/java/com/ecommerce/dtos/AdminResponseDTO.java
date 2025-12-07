package com.ecommerce.dtos;

import lombok.*;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminResponseDTO {
    private Long id;
    private String email;
    private String fullName;
    private String status;
    private Set<RoleDTO> roles;
    private Set<String> permissions;
}