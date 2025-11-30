package com.ecommerce.requests.admin;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductRequest {

    @NotBlank(message = "Product Name field is required")

    private String title;
    private String slug;

    @NotBlank(message = "Description field is required")
    private String description;

    private boolean status;
    private String sortOrder;

    private boolean isMainMenu;
    private boolean isDropdownMenu;

    private MultipartFile image;
    private MultipartFile coverImage;

    @Override
    public String toString() {
        return "ProductRequest [title=" + title + ", slug=" + slug + ", description=" + description + ", status="
                + status + ", sortOrder=" + sortOrder + ", isMainMenu=" + isMainMenu + ", isDropdownMenu="
                + isDropdownMenu + ", image=" + image + ", coverImage=" + coverImage + "]";
    }

}
