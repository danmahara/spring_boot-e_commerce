package com.ecommerce.dtos;

import lombok.Data;

@Data
public class AdminRegisterDTO {
    private String fullName;
    private String email;
    private String password;
}
