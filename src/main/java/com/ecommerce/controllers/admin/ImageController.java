// package com.ecommerce.controllers.admin;

// import java.io.IOException;
// import java.util.List;

// import org.springframework.core.io.Resource;
// import org.springframework.http.MediaType;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.DeleteMapping;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PatchMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.PutMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.bind.annotation.RestController;
// import org.springframework.web.multipart.MultipartFile;

// import com.ecommerce.models.Image;
// import com.ecommerce.services.admin.ImageService;

// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;

// @RestController
// @RequestMapping("/api/images")
// @RequiredArgsConstructor
// @Slf4j
// public class ImageController {
// private final ImageService imageService;

// @PostMapping("/upload/{imageableType}/{imageableId}")
// public ResponseEntity<?> uploadImage(
// @PathVariable String imageableType,
// @PathVariable Long imageableId,
// @RequestParam("file") MultipartFile file) {
// try {
// Image image = imageService.uploadImage(file, imageableType, imageableId);
// return ResponseEntity.ok(image);
// } catch (IOException e) {
// log.error("Error uploading image", e);
// return ResponseEntity.badRequest().body("Error uploading image");
// }
// }

// @GetMapping("/{imageableType}/{imageableId}")
// public ResponseEntity<?> getImages(
// @PathVariable String imageableType,
// @PathVariable Long imageableId) {
// List<Image> images = imageService.getImagesByImageable(
// imageableType, imageableId);
// return ResponseEntity.ok(images);
// }

// @GetMapping("/{imageId}/file")
// public ResponseEntity<Resource> getImage(@PathVariable Long imageId)
// throws IOException {
// Resource resource = imageService.getImageAsResource(imageId);
// return ResponseEntity.ok()
// .contentType(MediaType.IMAGE_JPEG)
// .body(resource);
// }

// @DeleteMapping("/{imageId}")
// public ResponseEntity<?> deleteImage(@PathVariable Long imageId) {
// try {
// imageService.deleteImage(imageId);
// return ResponseEntity.ok("Image deleted successfully");
// } catch (IOException e) {
// log.error("Error deleting image", e);
// return ResponseEntity.badRequest().body("Error deleting image");
// }
// }

// @PutMapping("/reorder/{imageableType}/{imageableId}")
// public ResponseEntity<?> reorderImages(
// @PathVariable String imageableType,
// @PathVariable Long imageableId,
// @RequestBody List<Long> imageIds) {
// imageService.reorderImages(imageableType, imageableId, imageIds);
// return ResponseEntity.ok("Images reordered successfully");
// }

// @PatchMapping("/{imageId}/status")
// public ResponseEntity<?> updateImageStatus(
// @PathVariable Long imageId,
// @RequestParam Boolean isActive) {
// imageService.updateImageStatus(imageId, isActive);
// return ResponseEntity.ok("Image status updated");
// }
// }