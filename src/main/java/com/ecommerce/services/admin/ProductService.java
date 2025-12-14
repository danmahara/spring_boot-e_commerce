// package com.ecommerce.services.admin;

// import java.util.List;
// import java.util.Optional;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;

// import com.ecommerce.models.Page;
// import com.ecommerce.models.admin.Image;
// import com.ecommerce.models.admin.Product;
// import com.ecommerce.repository.admin.ImageRepository;
// import com.ecommerce.repository.admin.ProductRepository;

// @Service
// public class ProductService {

//     private final ImageService imageService;
//     @Autowired
//     ProductRepository productRepository;
//     @Autowired
//     ImageRepository imageRepository;

//     ProductService(ImageService imageService) {
//         this.imageService = imageService;
//     }

//     // public List<Page> getAllProducts(String type) {

//     // List<Page> products = productRepository.findByType(type);

//     // // Load images for each page
//     // for (Page page : products) {
//     // List<Image> imgs = imageRepository.findByImageableTypeAndImageableId("page",
//     // page.getId());
//     // page.setImages(imgs);
//     // }

//     // return products;
//     // }

//     public Optional<Product> findById(Long id) {
//         return productRepository.findById(id)
//                 .map(product -> {
//                     // Load images
//                     List<Image> imgs = imageRepository.findByImageableTypeAndImageableId("product", product.getId());
//                     product.setImages(imgs);
//                     return product;
//                 });
//     }

//     public Product findByIdOrThrow(Long id) {
//         Product product = productRepository.findById(id)
//                 .orElseThrow(() -> new RuntimeException("Product not found"));

//         List<Image> imgs = imageRepository.findByImageableTypeAndImageableId("product", product.getId());
//         product.setImages(imgs);

//         return product;
//     }

//     public Product saveProduct(Product product) {
//         return productRepository.save(product);
//     }

//     public void deleteById(Long id) {
//         try {
//             productRepository.deleteById(id);
//             imageService.deleteImagesByImageable("page", id);
//         } catch (Exception e) {
//             System.out.println("Image Delete Failed: " + e.getMessage());
//             System.out.println("Full Stack Trace below");
//             e.printStackTrace();
//         }

//     }

//     public void toggleStatus(Long id) {
//         Product page = productRepository.findById(id)
//                 .orElseThrow(() -> new RuntimeException("Product not found"));

//         page.setStatus(!page.isStatus()); // toggle true/false
//         productRepository.save(page);
//     }

// }

package com.ecommerce.services.admin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.models.admin.Category;
import com.ecommerce.models.admin.Image;
import com.ecommerce.models.admin.Product;
import com.ecommerce.repository.admin.CategoryRepository;
import com.ecommerce.repository.admin.ImageRepository;
import com.ecommerce.repository.admin.ProductRepository;
import com.ecommerce.requests.admin.ProductRequest;

@Service
public class ProductService {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    private ImageService imageService;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryRepository categoryRepository;

    public List<Product> getAllActiveProducts() {
        return productRepository.findByStatusTrueOrderBySortOrderAsc();
    }

    public List<Product> getActiveProductsLimit(int limit) {

        Pageable pageable = PageRequest.of(0, limit);

        return productRepository
                .findByStatusTrueOrderBySortOrderAsc(pageable).map(page -> {
                    List<Image> imgs = imageRepository.findByImageableTypeAndImageableId("product",
                            page.getId());
                    page.setImages(imgs);
                    return page;
                }).getContent();
    }

    public Product findBySlug(String slug) {
        return productRepository.findBySlug(slug);
    }

    public Product findById(Long id) {
        return productRepository.findById(id)
                .map(page -> {
                    List<Image> imgs = imageRepository.findByImageableTypeAndImageableId("product",
                            page.getId());
                    page.setImages(imgs);
                    return page;
                }).orElseThrow(() -> new RuntimeException("Failed to find Product with image"));
    }

    public List<Product> getAllProducts() {
        List<Product> pages = productRepository.findAll();

        // Load images for each page
        for (Product page : pages) {
            List<Image> imgs = imageRepository.findByImageableTypeAndImageableId("product", page.getId());
            page.setImages(imgs);
        }
        return pages;
    }

    @Transactional
    public Product store(ProductRequest request) {
        // Check if slug already exists
        if (productRepository.existsBySlug(request.getSlug())) {
            throw new IllegalArgumentException("A product with this slug already exists");
        }

        // Check if SKU already exists (if provided)
        if (request.getSku() != null && !request.getSku().trim().isEmpty()
                && productRepository.existsBySku(request.getSku())) {
            throw new IllegalArgumentException("A product with this SKU already exists");
        }

        Product product = new Product();
        product.setName(request.getName());
        product.setSlug(generateSlug(request.getSlug().isEmpty() ? request.getName() : request.getSlug()));
        product.setPrice(request.getPrice());
        product.setDiscountType(request.getDiscountType());
        product.setDiscountPrice(request.getDiscountPrice());
        product.setDiscountPercent(request.getDiscountPercent());
        product.setCurrency(request.getCurrency());
        product.setQuantity(request.getQuantity() != null ? request.getQuantity() : 0);
        product.setSku(request.getSku());
        product.setSpecifications(request.getSpecifications());
        product.setDescription(request.getDescription());
        product.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0);
        product.setStatus(request.getStatus() != null ? request.getStatus() : true);

        // Save the product first to get the ID
        product = productRepository.save(product);

        // CATEGORY ASSIGNMENT
        if (request.getCategories() != null && !request.getCategories().isEmpty()) {
            List<Category> categories = categoryRepository.findAllById(request.getCategories());

            // product.setCategories(new HashSet<>(categories));
            product.setCategories(categories);
        }

        // Handle image uploads
        try {
            List<Image> images = new ArrayList<>();

            // Save thumbnail image
            if (request.getImage() != null && !request.getImage().isEmpty()) {
                product.addFeatureImage(imageService, request.getImage());
            }

            // Save cover image
            if (request.getCoverImage() != null && !request.getCoverImage().isEmpty()) {
                product.addCoverImage(imageService, request.getCoverImage());
            }

            product.setImages(images);

        } catch (IOException e) {
            // Rollback will happen automatically due to @Transactional
            throw new RuntimeException("Failed to upload images: " + e.getMessage(), e);
        }

        return product;
    }

    public void update(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        product.setName(request.getName());
        product.setSlug(generateSlug(request.getSlug().isEmpty() ? request.getName() : request.getSlug()));
        product.setPrice(request.getPrice());
        product.setCurrency(request.getCurrency());
        product.setQuantity(request.getQuantity() != null ? request.getQuantity() : 0);
        product.setSku(request.getSku());
        product.setSpecifications(request.getSpecifications());
        product.setDescription(request.getDescription());
        product.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0);
        product.setStatus(request.getStatus() != null ? request.getStatus() : true);

        // -----------------------
        // Discount Logic
        // -----------------------
        product.setDiscountType(request.getDiscountType());
        if ("percentage".equals(request.getDiscountType())) {
            product.setDiscountPercent(request.getDiscountPercent());
            product.setDiscountPrice(null); // reset
        } else if ("fixed".equals(request.getDiscountType())) {
            product.setDiscountPrice(request.getDiscountPrice());
            product.setDiscountPercent(null); // reset
        } else {
            product.setDiscountType(null);
            product.setDiscountPercent(null);
            product.setDiscountPrice(null);
        }

        // -----------------------
        // UPDATE CATEGORIES
        // -----------------------
        if (request.getCategories() != null && !request.getCategories().isEmpty()) {
            List<Category> categories = categoryRepository.findAllById(request.getCategories());
            product.setCategories(categories);
        } else {
            // If no categories provided, clear all categories
            product.setCategories(new ArrayList<>());
        }

        // -----------------------
        // Image Uploads
        // -----------------------
        Product savedProduct = productRepository.save(product);

        try {
            // 1. Update thumbnail image if provided
            if (request.getImage() != null && !request.getImage().isEmpty()) {
                savedProduct.updateFeatureImage(imageService, request.getImage());
            }

            // 2. Update cover image if provided
            if (request.getCoverImage() != null && !request.getCoverImage().isEmpty()) {
                savedProduct.updateCoverImage(imageService, request.getCoverImage());
            }
        } catch (Exception e) {
            System.out.println("Failed to update product image");
            System.out.println("Error: " + e.getMessage());
        }
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Transactional
    public void delete(Long id) {
        try {

            Product product = findById(id);

            // Delete associated images first
            // imageService.deleteImagesByImageableTypeAndId("product", id);
            imageService.deleteImagesByImageable("product", id);

            productRepository.delete(product);
        } catch (Exception e) {
            System.out.println("Image Delete Failed: " + e.getMessage());
            System.out.println("Full Stack Trace below");
            e.printStackTrace();
        }
    }

    private String generateSlug(String name) {
        return name.toLowerCase().replace(" ", "-");
    }

    public List<Category> getAllActiveCategories() {
        return categoryService.getAllCategories();
    }
}