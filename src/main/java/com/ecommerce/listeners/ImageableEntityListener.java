package com.ecommerce.listeners;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ecommerce.services.admin.ImageService;
import com.ecommerce.traits.Imageable;

import jakarta.persistence.PostLoad;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ImageableEntityListener {

    private static ImageService staticImageService;

    // Inject ImageService statically (because JPA listeners don't allow normal DI)
    @Autowired
    public void init(ImageService imageService) {
        ImageableEntityListener.staticImageService = imageService;
    }

    @PostLoad
    public void loadImages(Object entity) {
        try {
            if (entity instanceof Imageable imageable) {
                var images = staticImageService.getImagesByImageable(
                        imageable.getImageableType(),
                        imageable.getId());
                imageable.setImages(images);
            }
        } catch (Exception e) {
            log.error("Failed to load images for: {}", entity.getClass().getSimpleName(), e);
        }
    }
}
