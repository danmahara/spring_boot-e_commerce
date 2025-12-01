package com.ecommerce.helpers;

import java.io.File;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
@Getter
@Setter
@ConfigurationProperties(prefix = "app.images")
public class ImageProperties {
    private String uploadDir = "uploads";
    // private long maxFileSize = 5242880; // 5MB
    private long maxFileSize = 20971520; // 20MB

    private String allowedTypes = "image/jpeg,image/png,image/gif,image/webp";

    public String getStoragePath(String imageableType) {
        return uploadDir + File.separator + imageableType;
    }
}
