package com.ecommerce.requests.admin;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductRequest {

    private String title; // maps to Page.title
    private String slug; // maps to Page.slug
    private String description; // maps to Page.description
    private String type; // maps to Page.type
    private String templateName; // maps to Page.templateName
    private boolean status; // maps to Page.status
    private String sortOrder; // maps to Page.orderInput

    private boolean isMainMenu; // maps to Page.isMainMenu
    private boolean isDropdownMenu; // maps to Page.isDropdownMenu

    private MultipartFile image; // for feature image
    private MultipartFile coverImage; // for cover image

    @Override
    public String toString() {
        return "ProductRequest [title=" + title + ", slug=" + slug + ", description=" + description
                + ", type=" + type + ", templateName=" + templateName
                + ", status=" + status + ", orderInput=" + sortOrder
                + ", isMainMenu=" + isMainMenu + ", isDropdownMenu=" + isDropdownMenu
                + ", image=" + image + ", coverImage=" + coverImage + "]";
    }
}
