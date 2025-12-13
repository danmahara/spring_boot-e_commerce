package com.ecommerce.models.admin;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.ecommerce.traits.Imageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "site_settings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SiteSetting implements Imageable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, length = 255)
    private String email;

    @Column(length = 50)
    private String phone;

    @Column(name = "fb_link", length = 500)
    private String fbLink;

    @Column(name = "insta_link", length = 500)
    private String instaLink;

    @Column(name = "x_link", length = 500)
    private String xLink;

    @Column(name = "linkedin_link", length = 500)
    private String linkedinLink;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Override
    public String getImageableType() {
        return "site_setting";
    }

    @Transient
    private List<Image> images = new ArrayList<>();

}