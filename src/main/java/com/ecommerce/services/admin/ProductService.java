package com.ecommerce.services.admin;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecommerce.models.Page;
import com.ecommerce.models.admin.Image;
import com.ecommerce.repository.admin.ImageRepository;
import com.ecommerce.repository.admin.ProductRepository;

@Service
public class ProductService {
    @Autowired
    ProductRepository productRepository;
    @Autowired
    ImageRepository imageRepository;

    public List<Page> getAllProducts(String type) {

        List<Page> products = productRepository.findByType(type);

        // Load images for each page
        for (Page page : products) {
            List<Image> imgs = imageRepository.findByImageableTypeAndImageableId("page", page.getId());
            page.setImages(imgs);
        }

        return products;
    }

    public Optional<Page> findById(Long id) {
        return productRepository.findById(id)
                .map(product -> {
                    // Load images
                    List<Image> imgs = imageRepository.findByImageableTypeAndImageableId("page", product.getId());
                    product.setImages(imgs);
                    return product;
                });
    }

    public Page findByIdOrThrow(Long id) {
        Page product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Page not found"));

        List<Image> imgs = imageRepository.findByImageableTypeAndImageableId("page", product.getId());
        product.setImages(imgs);

        return product;
    }

    public Page saveProduct(Page product) {
        return productRepository.save(product);
    }

    public void toggleStatus(Long id) {
        Page page = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        page.setStatus(!page.isStatus()); // toggle true/false
        productRepository.save(page);
    }

}
