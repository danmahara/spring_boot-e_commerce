package com.ecommerce.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecommerce.models.Page;
import com.ecommerce.models.admin.Image;
import com.ecommerce.models.admin.Product;
import com.ecommerce.repository.FrontendRepository;
import com.ecommerce.repository.admin.ImageRepository;
import com.ecommerce.services.admin.ProductService;

@Service
public class FrontendService {

    @Autowired
    FrontendRepository frontendRepository;

    @Autowired
    ImageRepository imageRepository;

    @Autowired
    ProductService productService;

    public List<Page> getAllActivePages() {
        return frontendRepository.findByTemplateNameInAndStatusTrueOrderByOrderAsc(Page.getPageLists());
    }

    public Page findBySlug(String slug) {

        Page page = frontendRepository.findBySlug(slug);

        List<Image> imgs = imageRepository.findByImageableTypeAndImageableId("page", page.getId());
        page.setImages(imgs);

        System.out.println("Images: " + page.getFeatureImage());
        return page;
    }

    public List<Product> getAllActiveProducts() {
        List<Product> products = productService.getAllActiveProducts();

        // Load images for each page
        for (Product page : products) {
            List<Image> imgs = imageRepository.findByImageableTypeAndImageableId("product", page.getId());

            page.setImages(imgs);
        }
        return products;
    }

}
