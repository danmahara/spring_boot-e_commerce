package com.ecommerce.controllers.admin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ecommerce.models.admin.Category;
import com.ecommerce.requests.admin.CategoryRequest;
import com.ecommerce.services.admin.CategoryService;

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
    public List<Category> getPagesJson() {
        return categoryService.getAllCategories();
    }

    @GetMapping("create")
    public String create(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        return "admin/category/create";
    }

    @PostMapping("/store")
    public ResponseEntity<?> store(CategoryRequest request) {
        Map<String, Object> response = new HashMap<>();

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

    @GetMapping("/tree")
    public ResponseEntity<?> getTree() {
        return ResponseEntity.ok(categoryService.getCategoryTree());
    }
}
