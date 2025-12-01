package com.ecommerce.repository.admin;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecommerce.enums.ImageType;
import com.ecommerce.models.admin.Image;
// import com.ecommerce.models.admin.Image.ImageType;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {

        Optional<Image> findByImageableIdAndType(Long imageableId, ImageType type);

        List<Image> findByImageableTypeAndImageableId(String type, Long id);
        

        // Get all images for an entity
        List<Image> findByImageableTypeAndImageableIdOrderBySortOrder(
                        String imageableType, Long imageableId);

        // Get images by type for an entity
        List<Image> findByImageableTypeAndImageableIdAndTypeOrderBySortOrder(
                        String imageableType, Long imageableId, ImageType type);

        // Get single image of a specific type
        Optional<Image> findByImageableTypeAndImageableIdAndType(
                        String imageableType, Long imageableId, ImageType type);

        // Delete by entity
        void deleteByImageableTypeAndImageableId(String imageableType, Long imageableId);

        // Find by filename
        Optional<Image> findByFilename(String filename);

        // Find all by type
        List<Image> findByImageableType(String imageableType);

        // Find all by image type
        List<Image> findByType(ImageType type);
}