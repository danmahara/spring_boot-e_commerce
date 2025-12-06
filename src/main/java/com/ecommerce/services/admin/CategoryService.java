package com.ecommerce.services.admin;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecommerce.models.admin.Category;
import com.ecommerce.repository.admin.CategoryRepository;
import com.ecommerce.requests.admin.CategoryRequest;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public Optional<Category> findById(Long id) {

        return categoryRepository.findById(id);
    }

    public Category createCategory(CategoryRequest request) {

        Category category = new Category();
        category.setTitle(request.getTitle());
        if (request.getSlug() != null && !request.getSlug().isEmpty()) {
            category.setSlug(generateSlug(request.getTitle()));
        } else {
            category.setSlug(generateSlug(request.getTitle()));
        }
        category.setStatus(request.isStatus());
        category.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0);

        if (request.getParentId() != null) {
            Category parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new RuntimeException("Parent not found"));
            category.setParent(parent);
        }

        return categoryRepository.save(category);
    }

    public Category updateCategory(CategoryRequest request, Long id) {

        Optional<Category> cat = categoryRepository.findById(id);
        Category category = cat.get();

        category.setTitle(request.getTitle());
        category.setSlug(generateSlug(!request.getSlug().isBlank() ? request.getSlug() : category.getSlug()));
        category.setSortOrder(request.getSortOrder());
        category.setStatus(request.isStatus());

        if (request.getParentId() != null) {
            Category parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new RuntimeException("Pagent Category not fond"));
            category.setParent(parent);
        } else {
            category.setParent(null);

        }

        return categoryRepository.save(category);
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public List<Category> getAllExcept(Long id) {
        return categoryRepository.findByIdNot(id);
    }

    public List<Category> getCategoryTree() {
        return categoryRepository.findByParentIsNullOrderBySortOrderAsc();
    }

    public List<Category> getChildCategories(Long id) {
        return categoryRepository.findByParentIdOrderBySortOrderAsc(id);
    }

    public void toogleStatus(Long id) {
        try {
            Category category = findById(id).orElseThrow(() -> new RuntimeException("Category not found"));
            category.setStatus(!category.isStatus());
            categoryRepository.save(category);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteById(Long id) {
        categoryRepository.deleteById(id);
    }

    private String generateSlug(String name) {
        return name.toLowerCase().replace(" ", "-");
    }
}
