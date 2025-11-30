// package com.ecommerce.traits;

// import java.io.IOException;
// import java.util.ArrayList;
// import java.util.List;

// import org.springframework.web.multipart.MultipartFile;

// import com.ecommerce.enums.ImageType;
// import com.ecommerce.models.admin.Image;
// // import com.ecommerce.models.admin.Image.ImageType;
// import com.ecommerce.services.admin.ImageService;

// public interface Imageable {

// Long getId();

// String getImageableType();

// List<Image> getImages();

// // ============ Image Management Methods ============

// default List<Image> getImages(ImageService imageService) {
// return imageService.getImagesByImageable(getImageableType(), getId());
// }

// default Image addImage(ImageService imageService, MultipartFile file)
// throws IOException {
// return imageService.uploadImage(file, getImageableType(), getId());
// }

// default void removeImage(ImageService imageService, Long imageId)
// throws IOException {
// imageService.deleteImage(imageId);
// }

// default void removeAllImages(ImageService imageService)
// throws IOException {
// imageService.deleteImagesByImageable(getImageableType(), getId());
// }

// default void reorderImages(ImageService imageService, List<Long> imageIds) {
// imageService.reorderImages(getImageableType(), getId(), imageIds);
// }

// // ============ Image Retrieval by Type ============

// /**
// * Get feature image URL
// */
// default String getFeatureImage() {
// return getImageUrlByType(ImageType.FEATURE);
// }

// /**
// * Get cover image URL
// */
// default String getCoverImage() {
// return getImageUrlByType(ImageType.COVER);
// }

// /**
// * Get thumbnail image URL
// */
// default String getThumbnailImage() {
// return getImageUrlByType(ImageType.THUMBNAIL);
// }

// /**
// * Get banner image URL
// */
// default String getBannerImage() {
// return getImageUrlByType(ImageType.BANNER);
// }

// /**
// * Get icon image URL
// */
// default String getIconImage() {
// return getImageUrlByType(ImageType.ICON);
// }

// /**
// * Get all gallery images as URLs
// */
// default List<String> getGalleryImages() {
// return getImageUrlsByType(ImageType.GALLERY);
// }

// /**
// * Get all gallery images as Image objects
// */
// default List<Image> getGalleryImageObjects() {
// return getImagesByType(ImageType.GALLERY);
// }

// /**
// * Get image URL by specific type
// */
// default String getImageUrlByType(ImageType type) {
// Image image = getImageByType(type);
// if (image == null) {
// return null;
// }
// return buildImageUrl(image);
// }

// /**
// * Get all image URLs by specific type
// */
// default List<String> getImageUrlsByType(ImageType type) {
// List<Image> typeImages = getImagesByType(type);
// return typeImages.stream()
// .map(this::buildImageUrl)
// .toList();
// }

// /**
// * Get first image object by type
// */
// default Image getImageByType(ImageType type) {
// List<Image> imgs = getImages();
// if (imgs == null || imgs.isEmpty()) {
// return null;
// }
// return imgs.stream()
// .filter(img -> img.getType() == type && img.getIsActive())
// .findFirst()
// .orElse(null);
// }

// /**
// * Get all image objects by type
// */
// default List<Image> getImagesByType(ImageType type) {
// List<Image> imgs = getImages();
// if (imgs == null || imgs.isEmpty()) {
// return new ArrayList<>();
// }
// return imgs.stream()
// .filter(img -> img.getType() == type && img.getIsActive())
// .toList();
// }

// /**
// * Build image URL from Image object
// */
// default String buildImageUrl(Image image) {
// if (image == null || image.getFilename() == null) {
// return null;
// }
// return "/uploads/" + image.getImageableType() + "/" + image.getFilename();
// }

// /**
// * Check if has images
// */
// default boolean hasImages() {
// List<Image> imgs = getImages();
// return imgs != null && !imgs.isEmpty();
// }

// /**
// * Check if has image of specific type
// */
// default boolean hasImageOfType(ImageType type) {
// return getImageByType(type) != null;
// }

// /**
// * Get total image count
// */
// default int getImageCount() {
// List<Image> imgs = getImages();
// return imgs == null ? 0 : imgs.size();
// }

// /**
// * Get count of images by type
// */
// default int getImageCountByType(ImageType type) {
// return getImagesByType(type).size();
// }
// }

package com.ecommerce.traits;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.ecommerce.enums.ImageType;
import com.ecommerce.models.admin.Image;
import com.ecommerce.services.admin.ImageService;

public interface Imageable {

    Long getId();

    String getImageableType();

    List<Image> getImages();

    void setImages(List<Image> images);

    default Image addImage(ImageService imageService, MultipartFile file)
            throws IOException {
        return imageService.uploadImage(file, getImageableType(), getId());
    }

    default Image addImage(ImageService service, MultipartFile file, ImageType type)
            throws IOException {
        return service.uploadImage(file, getImageableType(), getId(), type);
    }

    default Image addFeatureImage(ImageService imageService, MultipartFile file)
            throws IOException {
        return imageService.uploadImage(file, getImageableType(), getId(), ImageType.FEATURE);
    }

    default Image updateFeatureImage(ImageService imageService, MultipartFile file)
            throws IOException {
        // return imageService.updateImage(file, getImageableType(), getId(),
        return imageService.updateImage(file, getImageableType(), getId(), ImageType.FEATURE);

    }

    default Image updateCoverImage(ImageService imageService, MultipartFile file)
            throws IOException {
        // return imageService.updateImage(file, getImageableType(), getId(),
        return imageService.updateImage(file, getImageableType(), getId(), ImageType.COVER);

    }

    default Image addCoverImage(ImageService imageService, MultipartFile file)
            throws IOException {
        return imageService.uploadImage(file, getImageableType(), getId(), ImageType.COVER);
    }

    // ============ Image Retrieval Helpers ============

    default Image getImageByType(ImageType type) {
        var imgs = getImages();
        if (imgs == null || imgs.isEmpty())
            return null;

        return imgs.stream()
                .filter(i -> i.getType() == type && Boolean.TRUE.equals(i.getIsActive()))
                .findFirst()
                .orElse(null);
    }

    default String getImageUrlByType(ImageType type) {
        var img = getImageByType(type);
        if (img == null)
            return null;
        return "/uploads/" + img.getImageableType() + "/" + img.getFilename();
    }

    default String getFeatureImage() {
        return getImageUrlByType(ImageType.FEATURE);
    }

    default String getCoverImage() {
        return getImageUrlByType(ImageType.COVER);
    }

    default String getThumbnailImage() {
        return getImageUrlByType(ImageType.THUMBNAIL);
    }

    default String getIconImage() {
        return getImageUrlByType(ImageType.ICON);
    }

    default String getBannerImage() {
        return getImageUrlByType(ImageType.BANNER);
    }

    default List<String> getGalleryImages() {
        var imgs = getImages();
        if (imgs == null)
            return List.of();

        return imgs.stream()
                .filter(i -> i.getType() == ImageType.GALLERY && Boolean.TRUE.equals(i.getIsActive()))
                .map(i -> "/uploads/" + i.getImageableType() + "/" + i.getFilename())
                .toList();
    }
}
