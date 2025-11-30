package com.ecommerce.models.admin;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.ecommerce.enums.ImageType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String filename;

    @Column(nullable = false)
    private String path;

    @Column(nullable = false)
    private String originalName;

    @Column(nullable = false)
    private Long size;

    @Column(nullable = false)
    private String mimeType;

    @Column(nullable = false)
    private String imageableType;

    @Column(nullable = false)
    private Long imageableId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(50) default 'GALLERY'")
    private ImageType type = ImageType.GALLERY;

    @Column(name = "sort_order")
    private Integer sortOrder;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(nullable = false, columnDefinition = "boolean default true")
    private Boolean isActive = true;

    // Constructors
    public Image(String filename, String path, String originalName,
            Long size, String mimeType, String imageableType, Long imageableId) {
        this.filename = filename;
        this.path = path;
        this.originalName = originalName;
        this.size = size;
        this.mimeType = mimeType;
        this.imageableType = imageableType;
        this.imageableId = imageableId;
        this.type = ImageType.GALLERY;
    }

    public Image(String filename, String path, String originalName,
            Long size, String mimeType, String imageableType, Long imageableId, ImageType type) {
        this.filename = filename;
        this.path = path;
        this.originalName = originalName;
        this.size = size;
        this.mimeType = mimeType;
        this.imageableType = imageableType;
        this.imageableId = imageableId;
        this.type = type;
    }

    public String getUrl() {
        return "/uploads/" + this.imageableType + "/" + this.filename;
    }

}