package com.ecommerce.requests.admin;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PageRequest {

    @NotBlank(message = "Title field is required")
    private String title;

    private String slug;

    @NotBlank(message = "Description field is required")
    private String description;

    @NotBlank(message = "Template field is required")
    private String templateName;

    private boolean status;

    private Integer sortOrder;

    private boolean isMainMenu;
    private boolean isDropdownMenu;

    // No validation annotation - will be validated manually
    private MultipartFile image;

    // No validation annotation - will be validated manually
    private MultipartFile coverImage;

    @Override
    public String toString() {
        return "PageRequest [title=" + title + ", slug=" + slug + ", description=" + description
                + ", templateName=" + templateName
                + ", status=" + status + ", sortOrder=" + sortOrder
                + ", isMainMenu=" + isMainMenu + ", isDropdownMenu=" + isDropdownMenu
                + ", image=" + image + ", coverImage=" + coverImage + "]";
    }
}