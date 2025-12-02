package com.ecommerce.dtos.config;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TableColumn {
    private String field;
    private String label;
    private String type;
}
