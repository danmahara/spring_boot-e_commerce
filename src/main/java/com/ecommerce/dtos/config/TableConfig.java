package com.ecommerce.dtos.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TableConfig {
    private Boolean hasStatus;
    private String editRoute;
    private String statusRoute;
    private String deleteRoute;
}