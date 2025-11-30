package com.ecommerce.controllers.admin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ecommerce.enums.PageTemplate;
import com.ecommerce.enums.PageType;
import com.ecommerce.models.Page;
import com.ecommerce.repository.admin.PageRepository;
import com.ecommerce.requests.admin.PageRequest;
import com.ecommerce.services.admin.ImageService;
import com.ecommerce.services.admin.PageService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin")
public class PageController {

    // private final PageRepository pageRepository;

    @Autowired
    PageService pageService;

    @Autowired
    ImageService imageService;

    // PageController(PageRepository pageRepository) {
    // this.pageRepository = pageRepository;
    // }

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
            @Valid @ModelAttribute PageRequest request,
            BindingResult bindingResult) {

        Map<String, Object> response = new HashMap<>();
        Map<String, String> errors = new HashMap<>();

        // Collect field validation errors from BindingResult
        if (bindingResult.hasErrors()) {
            bindingResult.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        }

        // Add manual validation for file uploads
        if (request.getImage() == null || request.getImage().isEmpty()) {
            errors.put("image", "Thumbnail image is required");
        }

        if (request.getCoverImage() == null || request.getCoverImage().isEmpty()) {
            errors.put("coverImage", "Cover image is required");
        }

        // Return all errors together if any exist
        if (!errors.isEmpty()) {
            response.put("success", false);
            response.put("message", "Validation failed");
            response.put("errors", errors);
            return ResponseEntity.badRequest().body(response);
        }

        try {
            Page page = new Page();
            page.setTitle(request.getTitle());
            page.setSlug(
                    (request.getSlug() == null || request.getSlug().isEmpty())
                            ? request.getTitle().toLowerCase().replaceAll("\\s+", "-")
                            : request.getSlug());
            page.setType(PageType.PAGE.getPageName());
            page.setDescription(request.getDescription());
            page.setStatus(request.isStatus());
            page.setOrder(request.getSortOrder());
            page.setTemplateName(request.getTemplateName());

            Page savedPage = pageService.savePage(page);

            savedPage.addFeatureImage(imageService, request.getImage());
            savedPage.addCoverImage(imageService, request.getCoverImage());

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

    @GetMapping("/pages/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        Page page = pageService.findById(id).orElseThrow(() -> new RuntimeException("Page not found"));
        model.addAttribute("page", page);
        return "admin/page/edit";
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

    @PostMapping("/pages/update/{id}")
    public ResponseEntity<Map<String, Object>> updatePage(
            @PathVariable Long id,
            @ModelAttribute PageRequest request) {

        Map<String, Object> response = new HashMap<>();

        try {
            Page oldPage = pageService.findById(id).orElseThrow(() -> new RuntimeException("Page not found"));

            oldPage.setTitle(request.getTitle());

            oldPage.setSlug(request.getSlug() != null && !request.getSlug().isEmpty()
                    ? request.getSlug()
                    : oldPage.getSlug());

            oldPage.setTemplateName(!request.getTemplateName().isEmpty()?request.getTemplateName():oldPage.getTemplateName());
            oldPage.setDescription(request.getDescription());
            oldPage.setStatus(request.isStatus());
            oldPage.setOrder(request.getSortOrder());

            Page updatedPage = pageService.savePage(oldPage);

            if (request.getImage() != null && !request.getImage().isEmpty()) {
                updatedPage.updateFeatureImage(imageService, request.getImage());
            }
            if (request.getCoverImage() != null && !request.getCoverImage().isEmpty()) {
                updatedPage.updateCoverImage(imageService, request.getCoverImage());
            }

            response.put("success", true);
            response.put("message", "Page updated successfully");
            response.put("data", updatedPage);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            System.out.println("page update failed: ");
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    public String slugHelper(String slug) {
        return slug.toLowerCase().replaceAll("\\s+", "-");
    }

    @GetMapping("/color-reference")
    public String getMethodName() {
        return "admin/components/color-reference";
    }

}
