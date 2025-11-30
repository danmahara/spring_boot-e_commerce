package com.ecommerce.controllers.admin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.ecommerce.enums.PageTemplate;
import com.ecommerce.enums.PageType;
import com.ecommerce.models.Page;
import com.ecommerce.services.admin.ImageService;
import com.ecommerce.services.admin.PageService;

@Controller
@RequestMapping("/admin")
public class PageController {

    @Autowired
    PageService pageService;

    @Autowired
    ImageService imageService;

    @GetMapping("/pages")
    public String index(Model model) {
        model.addAttribute("title", "Pages");
        return "admin/page/index"; // Thymeleaf template
    }

    @GetMapping("/pages/json")
    @ResponseBody
    public List<Page> getPagesJson() {
        return pageService.getAllPages();
    }

    @GetMapping("/pages/create")
    public String craete(Model model) {
        model.addAttribute("templates", PageTemplate.values());
        return "admin/page/create";
    }

    @PostMapping("/pages/store")
    public ResponseEntity<Map<String, Object>> storePage(
            @ModelAttribute Page page,
            @RequestParam(value = "image") MultipartFile image,
            @RequestParam(value = "cover_image") MultipartFile coverImage) {

        Map<String, Object> response = new HashMap<>();
        try {
            page.setType(PageType.PAGE.getPageName());

            // Slug
            if (page.getSlug() == null || page.getSlug().isEmpty()) {
                page.setSlug(page.getTitle().toLowerCase().replaceAll("\\s+", "-"));
            }

            Page savedPage = pageService.savePage(page);

            // File uploads
            if (image != null && !image.isEmpty()) {
                savedPage.addFeatureImage(imageService, image);
            }
            if (coverImage != null && !coverImage.isEmpty()) {
                savedPage.addCoverImage(imageService, coverImage);
            }

            response.put("success", true);
            response.put("message", "Page created successfully");
            response.put("data", savedPage);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to create page: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/pages/status")
    @ResponseBody
    public Map<String, Object> updateStatus(@RequestParam Long id) {

        pageService.toggleStatus(id);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Status Changed Successfully");

        return response;
    }

    @GetMapping("/color-reference")
    public String getMethodName() {
        return "admin/components/color-reference";
    }

}
