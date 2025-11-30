package com.ecommerce.services.admin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ecommerce.enums.ImageType;
import com.ecommerce.helpers.ImageProperties;
import com.ecommerce.models.admin.Image;
import com.ecommerce.repository.admin.ImageRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageService {
    private final ImageRepository imageRepository;
    private final ImageProperties imageProperties;

    public Image uploadImage(MultipartFile file, String imageableType, Long imageableId, ImageType type)
            throws IOException {
        validateImage(file);

        log.info("Starting image upload for type: {}, id: {}", imageableType,
                imageableId);

        // Create folder structure
        String storagePath = imageProperties.getStoragePath(imageableType);
        Path uploadPath = Paths.get(storagePath);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
            log.info("Created directory: {}", uploadPath);
        }

        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String filename = generateUniqueFilename(originalFilename);
        Path filePath = uploadPath.resolve(filename);

        // Save file to disk
        Files.copy(file.getInputStream(), filePath,
                StandardCopyOption.REPLACE_EXISTING);
        log.info("File saved to: {}", filePath);

        // Create and save image entity
        Image image = new Image(
                filename,
                filePath.toString(),
                originalFilename,
                file.getSize(),
                file.getContentType(),
                imageableType,
                imageableId,
                type);

        Image savedImage = imageRepository.save(image);
        log.info("Image entity saved with ID: {}", savedImage.getId());
        return savedImage;
    }

    public Image uploadImage(MultipartFile file, String imageableType, Long imageableId)
            throws IOException {
        validateImage(file);

        log.info("Starting image upload for type: {}, id: {}", imageableType,
                imageableId);

        // Create folder structure
        String storagePath = imageProperties.getStoragePath(imageableType);
        Path uploadPath = Paths.get(storagePath);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
            log.info("Created directory: {}", uploadPath);
        }

        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String filename = generateUniqueFilename(originalFilename);
        Path filePath = uploadPath.resolve(filename);

        // Save file to disk
        Files.copy(file.getInputStream(), filePath,
                StandardCopyOption.REPLACE_EXISTING);
        log.info("File saved to: {}", filePath);

        // Create and save image entity
        Image image = new Image(
                filename,
                filePath.toString(),
                originalFilename,
                file.getSize(),
                file.getContentType(),
                imageableType,
                imageableId);

        Image savedImage = imageRepository.save(image);
        log.info("Image entity saved with ID: {}", savedImage.getId());
        return savedImage;
    }

    public Image updateImage(MultipartFile file, String imageableType, Long imageId, ImageType imageType)
            throws IOException {

        validateImage(file);
        log.info("Starting image update for imageId: {}", imageId);

        // Check if image exists
        Optional<Image> optionalImage = imageRepository.findByImageableIdAndType(imageId, imageType);

        if (optionalImage.isEmpty()) {
            log.warn("Image not found with ID: {}. Creating new image...", imageId);
            return uploadImage(file, imageableType, imageId, imageType);
        }

        Image existingImage = optionalImage.get();

        // Delete old file
        Path oldFilePath = Paths.get(existingImage.getPath());
        if (Files.exists(oldFilePath)) {
            Files.delete(oldFilePath);
            log.info("Deleted old image: {}", oldFilePath);
        }

        // Prepare directory
        String storagePath = imageProperties.getStoragePath(existingImage.getImageableType());
        Path uploadPath = Paths.get(storagePath);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
            log.info("Created directory: {}", uploadPath);
        }

        // Generate new filename
        String originalFilename = file.getOriginalFilename();
        String newFilename = generateUniqueFilename(originalFilename);
        Path newFilePath = uploadPath.resolve(newFilename);

        // Save new file
        Files.copy(file.getInputStream(), newFilePath, StandardCopyOption.REPLACE_EXISTING);
        log.info("New file saved to: {}", newFilePath);

        // Update image entity fields
        existingImage.setFilename(newFilename);
        existingImage.setPath(newFilePath.toString());
        existingImage.setOriginalName(originalFilename);
        existingImage.setSize(file.getSize());
        existingImage.setMimeType(file.getContentType());
        existingImage.setType(imageType);

        Image updatedImage = imageRepository.save(existingImage);
        log.info("Image updated successfully. New file: {}", updatedImage.getFilename());

        return updatedImage;
    }

    // public Image updateImage(Long imageId, MultipartFile file) throws IOException
    // {
    // validateImage(file);

    // log.info("Starting image update for imageId: {}", imageId);

    // Image existingImage = imageRepository.findById(imageId)
    // .orElseThrow(() -> new RuntimeException("Image not found with ID: " +
    // imageId));

    // // Delete old file
    // Path oldFilePath = Paths.get(existingImage.getPath());
    // if (Files.exists(oldFilePath)) {
    // Files.delete(oldFilePath);
    // log.info("Deleted old image: {}", oldFilePath);
    // }

    // // Prepare directory
    // String storagePath =
    // imageProperties.getStoragePath(existingImage.getImageableType());
    // Path uploadPath = Paths.get(storagePath);

    // if (!Files.exists(uploadPath)) {
    // Files.createDirectories(uploadPath);
    // log.info("Created directory: {}", uploadPath);
    // }

    // // Generate new filename
    // String originalFilename = file.getOriginalFilename();
    // String newFilename = generateUniqueFilename(originalFilename);
    // Path newFilePath = uploadPath.resolve(newFilename);

    // // Save new file
    // Files.copy(file.getInputStream(), newFilePath,
    // StandardCopyOption.REPLACE_EXISTING);
    // log.info("New file saved to: {}", newFilePath);

    // // Update image entity fields
    // existingImage.setFilename(newFilename);
    // existingImage.setPath(newFilePath.toString());
    // existingImage.setOriginalName(originalFilename);
    // existingImage.setSize(file.getSize());
    // existingImage.setMimeType(file.getContentType());

    // Image updatedImage = imageRepository.save(existingImage);
    // log.info("Image updated successfully. New file: {}",
    // updatedImage.getFilename());

    // return updatedImage;
    // }

    public List<Image> getImagesByImageable(String imageableType, Long imageableId) {
        return imageRepository.findByImageableTypeAndImageableIdOrderBySortOrder(
                imageableType, imageableId);
    }

    public void deleteImage(Long imageId) throws IOException {
        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found"));

        // Delete from filesystem
        Path filePath = Paths.get(image.getPath());
        if (Files.exists(filePath)) {
            Files.delete(filePath);
            log.info("Image deleted from filesystem: {}", filePath);
        }

        // Delete from database
        imageRepository.deleteById(imageId);
    }

    public void deleteImagesByImageable(String imageableType, Long imageableId)
            throws IOException {
        List<Image> images = getImagesByImageable(imageableType, imageableId);

        for (Image image : images) {
            deleteImage(image.getId());
        }
    }

    public void reorderImages(String imageableType, Long imageableId,
            List<Long> imageIds) {
        for (int i = 0; i < imageIds.size(); i++) {
            Image image = imageRepository.findById(imageIds.get(i))
                    .orElseThrow(() -> new RuntimeException("Image not found"));
            image.setSortOrder(i);
            imageRepository.save(image);
        }
    }

    public Resource getImageAsResource(Long imageId) throws IOException {
        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found"));

        Path filePath = Paths.get(image.getPath());
        return new FileSystemResource(filePath);
    }

    public void updateImageStatus(Long imageId, Boolean isActive) {
        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found"));
        image.setIsActive(isActive);
        imageRepository.save(image);
    }

    private void validateImage(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }

        if (file.getSize() > imageProperties.getMaxFileSize()) {
            throw new RuntimeException("File size exceeds maximum allowed size");
        }

        String[] allowedTypes = imageProperties.getAllowedTypes().split(",");
        if (!Arrays.asList(allowedTypes).contains(file.getContentType())) {
            throw new RuntimeException("Invalid file type");
        }
    }

    private String generateUniqueFilename(String originalFilename) {
        String extension = originalFilename.substring(
                originalFilename.lastIndexOf("."));
        return System.currentTimeMillis() + "_" + UUID.randomUUID() + extension;
    }
}
