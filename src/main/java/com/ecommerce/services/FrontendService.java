package com.ecommerce.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecommerce.enums.PageTemplate;
import com.ecommerce.models.Page;
import com.ecommerce.models.admin.Image;
import com.ecommerce.repository.FrontendRepository;
import com.ecommerce.repository.admin.ImageRepository;

@Service
public class FrontendService {

    @Autowired
    FrontendRepository frontendRepository;

    @Autowired
    ImageRepository imageRepository;

    public List<Page> getAllActivePages() {
        return frontendRepository.findByTemplateNameInAndStatusTrueOrderByOrderAsc(Page.getPageLists());
    }

    public Page findBySlug(String slug) {

        Page page = frontendRepository.findBySlug(slug);

        List<Image> imgs = imageRepository.findByImageableTypeAndImageableId("page", page.getId());
        page.setImages(imgs);

        System.out.println("Images: "+page.getFeatureImage());
        return page;
    }

    public List<Page> getAllProducts() {
        List<Page> products = frontendRepository
                .findByTemplateNameAndStatusTrue(PageTemplate.PRODUCT_LIST.getTemplateName());

        // Load images for each page
        for (Page page : products) {
            List<Image> imgs = imageRepository.findByImageableTypeAndImageableId("page", page.getId());

            page.setImages(imgs);
        }
        return products;
    }

}
