package com.ecommerce.requests.admin;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductRequest {

    @NotBlank(message = "Product name is required")
    @Size(max = 255, message = "Product name must not exceed 255 characters")
    private String name;

    @Pattern(regexp = "^$|^[a-z0-9]+(?:-[a-z0-9]+)*$", message = "Slug must be lowercase with hyphens only")
    @Size(max = 255, message = "Slug must not exceed 255 characters")
    private String slug;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal price;

    @Pattern(regexp = "^(percentage|fixed)?$", message = "Discount type must be 'percentage' or 'fixed'")
    private String discountType;

    @DecimalMin(value = "0.0", message = "Discount price must be 0 or greater")
    private BigDecimal discountPrice;

    @DecimalMin(value = "0.0", message = "Discount percent must be 0 or greater")
    private BigDecimal discountPercent;

    @NotBlank(message = "Currency is required")
    @Size(max = 10, message = "Currency must not exceed 10 characters")
    private String currency = "NPR";

    private Integer quantity = 0;

    @Size(max = 100, message = "SKU must not exceed 100 characters")
    private String sku;

    private MultipartFile image;

    private MultipartFile coverImage;

    private String specifications;

    @Size(max = 5000, message = "Description must not exceed 5000 characters")
    private String description;

    private Integer sortOrder = 0;

    private Boolean status = true;

    // NEW FIELD
    @NotEmpty(message = "Category can not be empty")
    private List<Long> categories; // list of Category IDs
}
