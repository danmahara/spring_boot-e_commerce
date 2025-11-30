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
import org.springframework.web.bind.annotation.DeleteMapping;
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
import com.ecommerce.repository.admin.ImageRepository;
import com.ecommerce.requests.admin.ProductRequest;
import com.ecommerce.services.admin.ImageService;
import com.ecommerce.services.admin.ProductService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequestMapping("/admin")
@Slf4j
public class ProductController {

    @Autowired
    ProductService productService;

    @Autowired
    ImageService imageService;

    @Autowired
    ImageRepository imageRepository;

    @GetMapping("/products")
    public String index(HttpSession session, Model model) {

        model.addAttribute("title", "Products");
        // model.addAttribute("products",
        // productService.getAllProducts(PageType.PRODUCT.getPageName()));
        return "admin/product/index";
    }

    @GetMapping("/products/json")
    @ResponseBody
    public List<Page> getPagesJson() {
        return productService.getAllProducts(PageType.PRODUCT.getPageName());
    }

    @GetMapping("/products/create")
    public String craete(Model model) {
        model.addAttribute("pageTemplate", PageTemplate.PRODUCT.getTemplateName());
        System.out.println("template name:" + PageTemplate.PRODUCT.getTemplateName());
        return "admin/product/create";
    }

    @PostMapping("/products/store")
    public ResponseEntity<Map<String, Object>> storePage(@Valid @ModelAttribute ProductRequest request,
            BindingResult bindingResult) {

        Map<String, Object> response = new HashMap<>();
        Map<String, String> errors = new HashMap<>();

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
            Page product = mapProductRequestToPage(request);

            // Save page
            Page savedProduct = productService.saveProduct(product);

            // Handle file uploads
            if (request.getImage() != null && !request.getImage().isEmpty()) {
                savedProduct.addFeatureImage(imageService, request.getImage());
            }
            if (request.getCoverImage() != null && !request.getCoverImage().isEmpty()) {
                savedProduct.addCoverImage(imageService, request.getCoverImage());
            }

            response.put("success", true);
            response.put("message", "Product created successfully");
            response.put("data", savedProduct);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to create product: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    public Page mapProductRequestToPage(ProductRequest request) {
        Page page = new Page();

        // Basic fields
        page.setTitle(request.getTitle());
        page.setSlug(request.getSlug() != null && !request.getSlug().isEmpty()
                ? request.getSlug()
                : request.getTitle().toLowerCase().replaceAll("\\s+", "-"));
        page.setDescription(request.getDescription());

        // Default
        page.setType(PageType.PRODUCT.getPageName());
        page.setTemplateName(PageTemplate.PRODUCT.getTemplateName());

        // Sort order
        try {
            page.setOrder(request.getSortOrder() != null
                    ? Integer.parseInt(request.getSortOrder())
                    : 0);
        } catch (NumberFormatException e) {
            page.setOrder(0); // fallback
        }

        // Status
        page.setStatus(request.isStatus()); // boolean

        // Menu flags - if you have them in the form, set here
        page.setMainMenu(request.isMainMenu()); // default false, or map from request
        page.setDropdownMenu(request.isDropdownMenu()); // default false, or map from request

        return page;
    }

    @GetMapping("/products/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {

        System.out.println("proudct id" + id);
        Page product = productService.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        model.addAttribute("product", product);

        return "admin/product/edit";
    }

    @PostMapping("/products/update/{id}")
    public ResponseEntity<Map<String, Object>> updateProduct(
            @PathVariable Long id,
            @ModelAttribute ProductRequest request) {

        Map<String, Object> response = new HashMap<>();

        try {
            // Fetch existing product
            Page existingProduct = productService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            // Map fields from request to existing product
            existingProduct.setTitle(request.getTitle());
            existingProduct.setSlug(request.getSlug() != null && !request.getSlug().isEmpty()
                    ? request.getSlug()
                    : request.getTitle().toLowerCase().replaceAll("\\s+", "-"));
            existingProduct.setDescription(request.getDescription());

            // Sort order
            try {
                existingProduct.setOrder(request.getSortOrder() != null
                        ? Integer.parseInt(request.getSortOrder())
                        : 0);
            } catch (NumberFormatException e) {
                existingProduct.setOrder(0);
            }

            // Status
            existingProduct.setStatus(request.isStatus());

            // Menu flags
            existingProduct.setMainMenu(request.isMainMenu());
            existingProduct.setDropdownMenu(request.isDropdownMenu());

            // Save updated product
            Page updatedProduct = productService.saveProduct(existingProduct);

            // Handle file uploads
            if (request.getImage() != null && !request.getImage().isEmpty()) {
                updatedProduct.updateFeatureImage(imageService, request.getImage());
            }
            if (request.getCoverImage() != null && !request.getCoverImage().isEmpty()) {
                System.out.println("product controller cover image");
                updatedProduct.updateCoverImage(imageService, request.getCoverImage());
            }

            response.put("success", true);
            response.put("message", "Product updated successfully");
            response.put("data", updatedProduct);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to update product: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/products/delete/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();

        try {
            productService.deleteById(id);
            response.put("success", true);
            response.put("message", "Page deleted Successfully");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to delete page: " + e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(response);
        }
    }

    @PostMapping("/products/status")
    @ResponseBody
    public Map<String, Object> updateStatus(@RequestParam Long id) {
        productService.toggleStatus(id);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Status Changed Successfully");

        return response;
    }
}
