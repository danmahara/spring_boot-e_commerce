package com.ecommerce.services.admin;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecommerce.enums.PageType;
import com.ecommerce.models.Page;
import com.ecommerce.models.admin.Image;
import com.ecommerce.repository.admin.ImageRepository;
import com.ecommerce.repository.admin.PageRepository;

@Service
public class PageService {

    private final ImageService imageService;

    // private final PasswordEncoder passwordEncoder;

    @Autowired
    PageRepository pageRepository;
    @Autowired
    ImageRepository imageRepository;

    PageService(ImageService imageService) {
        this.imageService = imageService;
    }

    // PageService(PasswordEncoder passwordEncoder) {
    // this.passwordEncoder = passwordEncoder;
    // }

    public Optional<Page> findById(Long id) {
        return pageRepository.findById(id)
                .map(page -> {
                    List<Image> imgs = imageRepository.findByImageableTypeAndImageableId("page",
                            page.getId());
                    page.setImages(imgs);
                    return page;
                });
    }

    public List<Page> getAllPages() {
        List<Page> pages = pageRepository.findByTemplateNameInAndType(Page.getPageLists(), PageType.PAGE.getPageName());

        // Load images for each page
        for (Page page : pages) {
            List<Image> imgs = imageRepository.findByImageableTypeAndImageableId("page", page.getId());
            page.setImages(imgs);
        }
        System.out.println("templates:" + Page.getPageLists());
        return pages;
    }

    public Page savePage(Page page) {
        return pageRepository.save(page);
    }

    public void deleteById(Long id) {
        try {
            pageRepository.deleteById(id);
            imageService.deleteImagesByImageable("page", id);
        } catch (Exception e) {
            System.out.println("Image Delete Failed: " + e.getMessage());
            System.out.println("Full Stack Trace below");
            e.printStackTrace();
        }

    }

    public void toggleStatus(Long id) {
        Page page = pageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Page not found"));

        page.setStatus(!page.isStatus()); // toggle true/false
        pageRepository.save(page);
    }

}
