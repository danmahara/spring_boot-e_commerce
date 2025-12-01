package com.ecommerce.services.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecommerce.models.admin.Category;
import com.ecommerce.repository.admin.CategoryRepository;
import com.ecommerce.requests.admin.CategoryRequest;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public Category createCategory(CategoryRequest request) {

        Category category = new Category();
        category.setTitle(request.getTitle());
        category.setSlug(generateSlug(request.getTitle()));
        category.setStatus(request.isStatus());
        category.setSortOrder(request.getSortOrder());

        if (request.getParentId() != null) {
            Category parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new RuntimeException("Parent not found"));
            category.setParent(parent);
        }

        return categoryRepository.save(category);
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public List<Category> getCategoryTree() {
        return categoryRepository.findByParentIsNullOrderBySortOrderAsc();
    }

    public void deleteById(Long id) {
        categoryRepository.deleteById(id);
    }

    private String generateSlug(String name) {
        return name.toLowerCase().replace(" ", "-");
    }
}
