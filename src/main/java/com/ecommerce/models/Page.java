package com.ecommerce.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.ecommerce.enums.ImageType;
import com.ecommerce.enums.PageTemplate;
import com.ecommerce.enums.PageType;
import com.ecommerce.models.admin.Image;
import com.ecommerce.traits.Imageable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "pages")
@Getter
@Setter
@NoArgsConstructor
public class Page implements Imageable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(unique = true)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String templateName;

    @Column(name = "sort_order")
    private Integer order = 0; // flexible ordering

    @Column(name = "is_main_menu", nullable = false)
    private boolean isMainMenu = false;

    @Column(name = "is_dropdown_menu", nullable = false)
    private boolean isDropdownMenu = false;

    @Column(nullable = false)
    private boolean status = false; // true = active

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Transient
    private List<Image> images = new ArrayList<>();

    @Override
    public String getImageableType() {
        return PageType.PAGE.getPageName(); // instance method
    }

    public Image findFeatureImage() {
        if (images == null)
            return null;
        return images.stream()
                .filter(img -> img.getType() == ImageType.FEATURE)
                .findFirst()
                .orElse(null);
    }

    @Transient
    public static List<String> getPageLists() {

        List<String> templateNames = Arrays.stream(Page.getPageTemplateList())
                .map(PageTemplate::getTemplateName)
                .toList();

        return templateNames;
    }

    // public static PageTemplate[] getPageTemplateList() {
    // return new PageTemplate[] {
    // PageTemplate.ABOUT_US, PageTemplate.CONTACT_US
    // };
    // }

    public static PageTemplate[] getPageTemplateList() {
        PageTemplate[] templates = new PageTemplate[] {
                PageTemplate.ABOUT_US,
                PageTemplate.CONTACT_US,
                PageTemplate.LOGIN,
                PageTemplate.REGISTER,
                PageTemplate.PRODUCT_LIST,
                PageTemplate.PRIVACY,
                PageTemplate.TERMS

        };
        return templates;
    }

    @Override
    public String toString() {
        return "Page [id=" + id + ", title=" + title + ", slug=" + slug + ", templateName=" + templateName
                + ", description=" + description + ", type=" + type + ", order=" + order
                + ", status=" + status + ", isMainMenu=" + isMainMenu + ", isDropdownMenu=" + isDropdownMenu
                + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + "]";
    }
}
