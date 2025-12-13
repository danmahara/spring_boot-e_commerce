package com.ecommerce.requests.admin;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SettingRequest {

    @NotBlank(message = "Title is required")
    @Size(min = 2, max = 255, message = "Title must be between 2 and 255 characters")
    @Column(nullable = false, length = 255)
    private String title;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    @Column(nullable = false, length = 255)
    private String email;

    @Pattern(regexp = "^[+]?[(]?[0-9]{1,4}[)]?[-\\s.]?[(]?[0-9]{1,4}[)]?[-\\s.]?[0-9]{1,9}$", message = "Please provide a valid phone number")
    @Size(max = 15, message = "Phone number must not exceed 15 characters")
    @Column(length = 50)
    private String phone;

    @Size(max = 500, message = "Facebook link must not exceed 500 characters")
    @Column(name = "fb_link", length = 500)
    private String fbLink;

    @Size(max = 500, message = "Instagram link must not exceed 500 characters")
    @Column(name = "insta_link", length = 500)
    private String instaLink;

    @Size(max = 500, message = "X link must not exceed 500 characters")
    @Column(name = "x_link", length = 500)
    private String xLink;

    @Size(max = 500, message = "LinkedIn link must not exceed 500 characters")
    @Column(name = "linkedin_link", length = 500)
    private String linkedinLink;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    private MultipartFile image;

    // private MultipartFile coverImage;

}