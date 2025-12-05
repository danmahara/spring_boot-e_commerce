package com.ecommerce.controllers.admin;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ecommerce.dtos.config.TableColumn;
import com.ecommerce.dtos.config.TableConfig;
import com.ecommerce.dtos.config.TableResponse;
import com.ecommerce.models.admin.Product;
import com.ecommerce.requests.admin.ProductRequest;
import com.ecommerce.services.admin.ProductService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Controller
@RequestMapping("/admin/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping({ "", "/" })
    public String index(HttpSession session, Model model) {

        model.addAttribute("title", "Products");
        return "admin/product/index";
    }

    @GetMapping("json")
    @ResponseBody
    public TableResponse getPagesJson() {
        List<Product> products = productService.getAllProducts();

        List<TableColumn> columns = Arrays.asList(
                new TableColumn("featureImage", "Image", "image"),
                new TableColumn("name", "Name", "text"),
                new TableColumn("sortOrder", "Order", "number"));

        TableConfig config = new TableConfig();
        config.setHasStatus(true);
        config.setEditRoute("/admin/products/edit/{id}");
        config.setStatusRoute("/admin/products/status/{id}");
        config.setDeleteRoute("/admin/products/delete/{id}");

        return new TableResponse(columns, products, config);
    }

    @GetMapping("create")
    public String create() {
        return "admin/product/create";
    }

    @PostMapping("/store")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> store(
            @Valid @ModelAttribute ProductRequest request,
            BindingResult bindingResult) {

        Map<String, Object> response = new HashMap<>();
        Map<String, String> errors = new HashMap<>();

        // Collect field validation errors from BindingResult
        if (bindingResult.hasErrors()) {
            bindingResult.getFieldErrors().forEach(error -> errors.put(error.getField(),
                    error.getDefaultMessage()));
        }

        try {
            // Additional validation for files
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

            // Validate discount logic
            if (request.getDiscountType() != null && !request.getDiscountType().isEmpty()) {
                if ("percentage".equals(request.getDiscountType()) &&
                        (request.getDiscountPercent() == null
                                || request.getDiscountPercent().compareTo(BigDecimal.ZERO) <= 0)) {
                    // Map<String, String> errors = new HashMap<>();
                    errors.put("discountPercent", "Discount percent is required when discount type is percentage");
                    response.put("success", false);
                    response.put("errors", errors);
                    return ResponseEntity.badRequest().body(response);
                }

                if ("fixed".equals(request.getDiscountType()) &&
                        (request.getDiscountPrice() == null
                                || request.getDiscountPrice().compareTo(BigDecimal.ZERO) <= 0)) {
                    errors.put("discountPrice", "Discount price is required when discount type is fixed");
                    response.put("success", false);
                    response.put("errors", errors);
                    return ResponseEntity.badRequest().body(response);
                }
            }

            // / Validate specifications JSON if provided
            String specs = request.getSpecifications();

            if (specs == null || specs.trim().isEmpty()) {
                request.setSpecifications(null); // MySQL JSON column accepts NULL
            } else {
                try {
                    new ObjectMapper().readTree(specs);
                } catch (Exception e) {
                    errors.put("specifications", "Invalid JSON format");
                    response.put("success", false);
                    response.put("errors", errors);
                    return ResponseEntity.badRequest().body(response);
                }
            }

            productService.store(request);

            response.put("success", true);
            response.put("message", "Product created successfully");
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "An error occurred while creating the product");
            System.out.println("Error is: " + e.getMessage());

            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        System.out.println("Edit method");
        Product product = productService.findById(id);
        model.addAttribute("product", product);
        return "admin/product/edit";
    }

    @PutMapping("/update/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> update(
            @PathVariable Long id,
            @Valid @ModelAttribute ProductRequest request,
            BindingResult bindingResult) {

        Map<String, Object> response = new HashMap<>();
        Map<String, String> errors = new HashMap<>();

        try {

            // -------------------------------
            // 1. Collect default validation errors
            // -------------------------------
            if (bindingResult.hasErrors()) {
                bindingResult.getFieldErrors()
                        .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            }

            // ------------------------------------------
            // 2. DO NOT require images again on update
            // ------------------------------------------
            // BUT: If user uploads one, allow it
            if (request.getImage() != null && request.getImage().isEmpty()) {
                // errors.put("image", "Invalid thumbnail image");
            }

            if (request.getCoverImage() != null && request.getCoverImage().isEmpty()) {
                // errors.put("coverImage", "Invalid cover image");
            }

            if (!errors.isEmpty()) {
                response.put("success", false);
                response.put("message", "Validation failed");
                response.put("errors", errors);
                return ResponseEntity.badRequest().body(response);
            }

            // ------------------------------------------
            // 3. Validate discount logic
            // ------------------------------------------
            if (request.getDiscountType() != null && !request.getDiscountType().isEmpty()) {

                if ("percentage".equals(request.getDiscountType())) {
                    if (request.getDiscountPercent() == null ||
                            request.getDiscountPercent().compareTo(BigDecimal.ZERO) <= 0) {

                        errors.put("discountPercent", "Discount percent is required when discount type is percentage");
                        response.put("success", false);
                        response.put("errors", errors);
                        return ResponseEntity.badRequest().body(response);
                    }
                }

                if ("fixed".equals(request.getDiscountType())) {
                    if (request.getDiscountPrice() == null ||
                            request.getDiscountPrice().compareTo(BigDecimal.ZERO) <= 0) {

                        errors.put("discountPrice", "Discount price is required when discount type is fixed");
                        response.put("success", false);
                        response.put("errors", errors);
                        return ResponseEntity.badRequest().body(response);
                    }
                }
            }

            // ------------------------------------------
            // 4. Validate specifications JSON
            // ------------------------------------------
            String specs = request.getSpecifications();

            if (specs == null || specs.trim().isEmpty()) {
                request.setSpecifications(null); // set empty JSON to null
            } else {
                try {
                    new ObjectMapper().readTree(specs);
                } catch (Exception e) {
                    errors.put("specifications", "Invalid JSON format");
                    response.put("success", false);
                    response.put("errors", errors);
                    return ResponseEntity.badRequest().body(response);
                }
            }

            // ------------------------------------------
            // 5. Call Service Layer to update product
            // ------------------------------------------
            productService.update(id, request);

            response.put("success", true);
            response.put("message", "Product updated successfully");

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "An error occurred while updating the product");
            System.out.println("Error is: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

}