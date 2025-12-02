package com.ecommerce.dtos.config;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TableResponse {
    private List<TableColumn> columns;
    private List<?> rows;
    private TableConfig config;
}
