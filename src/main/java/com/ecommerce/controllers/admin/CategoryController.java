package com.ecommerce.controllers.admin;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ecommerce.dtos.CategoryTableDTO;
import com.ecommerce.dtos.config.TableColumn;
import com.ecommerce.dtos.config.TableConfig;
import com.ecommerce.dtos.config.TableResponse;
import com.ecommerce.models.admin.Category;
import com.ecommerce.requests.admin.CategoryRequest;
import com.ecommerce.services.admin.CategoryService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/categories")
public class CategoryController {

    @Autowired
    CategoryService categoryService;

    @GetMapping({ "", "/" })
    public String index(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        return "admin/category/index";
    }

    @GetMapping("/json")
    @ResponseBody
    public TableResponse getCategoriesJson() {
        List<Category> categories = categoryService.getAllCategories();

        // Convert to DTOs
        List<CategoryTableDTO> dtos = categories.stream()
                .map(CategoryTableDTO::new)
                .collect(Collectors.toList());

        List<TableColumn> columns = Arrays.asList(
                new TableColumn("title", "Title", "text"),
                new TableColumn("parentTitle", "Parent", "text"),
                new TableColumn("sortOrder", "Order", "number"));

        TableConfig config = new TableConfig();
        config.setHasStatus(true);
        config.setStatusRoute("categories/status/{id}");
        config.setEditRoute("categories/edit/{id}");
        config.setDeleteRoute("categories/delete/{id}");

        return new TableResponse(columns, dtos, config);
    }

    @GetMapping("create")
    public String create(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        return "admin/category/create";
    }

    @PostMapping("/store")
    public ResponseEntity<?> store(
            @Valid @ModelAttribute CategoryRequest request,
            BindingResult bindingResult) {

        Map<String, Object> response = new HashMap<>();
        Map<String, String> errors = new HashMap<>();

        // Collect field validation errors from BindingResult
        if (bindingResult.hasErrors()) {
            bindingResult.getFieldErrors().forEach(error -> errors.put(error.getField(),
                    error.getDefaultMessage()));
        }

        // Return all errors together if any exist
        if (!errors.isEmpty()) {
            response.put("success", false);
            response.put("message", "Validation failed");
            response.put("errors", errors);
            return ResponseEntity.badRequest().body(response);
        }

        try {
            Category category = categoryService.createCategory(request);

            response.put("success", true);
            response.put("message", "Category Created Successfully.");
            response.put("data", category);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to create category. Something went wrong");
            System.out.println("Failed to create Category: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);

        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deletePage(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            categoryService.deleteById(id);
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

    @GetMapping("edit/{id}")
    public String edit(@PathVariable Long id, Model model) {

        Category category = categoryService.findById(id).orElseThrow(() -> new RuntimeException("Page not found"));

        model.addAttribute("categories", categoryService.getAllExcept(id));
        model.addAttribute("category", category);
        return "admin/category/edit";
    }

    @PutMapping("update/{id}")
    public ResponseEntity<Map<String, Object>> udpate(@PathVariable Long id,
            @Valid @ModelAttribute CategoryRequest request) {
        Map<String, Object> response = new HashMap<>();

        try {
            Category category = categoryService.updateCategory(request, id);
            response.put("success", true);
            response.put("message", "Category Updated Successfully");
            response.put("data", category);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", true);
            response.put("message", "Failed to update category. Due to: " + e.getMessage());
            response.put("error", e.getStackTrace());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @PutMapping("status/{id}")
    public ResponseEntity<Map<String, Object>> changeStatus(@PathVariable Long id) {

        Map<String, Object> response = new HashMap<>();
        System.out.println("status id:" + id);

        try {
            categoryService.toogleStatus(id);

            response.put("success", true);
            response.put("message", "Status Changed Successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", true);
            response.put("message", "Failed to toogle status. Due to: " + e.getMessage());
            response.put("error", e.getStackTrace());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/tree")
    public ResponseEntity<?> getTree() {
        return ResponseEntity.ok(categoryService.getCategoryTree());
    }
}
