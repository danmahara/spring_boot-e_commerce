package com.ecommerce.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.ecommerce.enums.PageTemplate;
import com.ecommerce.models.Page;
import com.ecommerce.models.admin.Product;
import com.ecommerce.services.FrontendService;

@Controller
public class FrontendController {

    private final FrontendService frontendService;

    FrontendController(FrontendService frontendService) {
        this.frontendService = frontendService;
    }

    @GetMapping("/")
    public String homePageHandler(Model model) {

        model.addAttribute("title", "Home - MyShop");
        model.addAttribute("content", "pages/welcome :: content");
        model.addAttribute("PageTemplate", PageTemplate.class);
        model.addAttribute("products", frontendService.getHomeProducts());

        return "pages/welcome";
    }

    @GetMapping("/products/{slug}")
    public String getProductDetailPage(@PathVariable String slug, Model model) {

        Product product = frontendService.findProductBySlug(slug);

        model.addAttribute("product", product);
        System.out.println("Product: " + product.getName());
        return "pages/product_detail";
    }

    @GetMapping("/images/{slug}")
    public ResponseEntity<?> getMethodName(@PathVariable String slug) {
        return ResponseEntity.ok(frontendService.findProductBySlug(slug));
    }

    @GetMapping("/{slug}")
    public String loadPage(@PathVariable String slug, Model model) {

        Page page = frontendService.findBySlug(slug);

        if (page == null) {
            return "error/404"; // or redirect
        }

        PageTemplate template = PageTemplate.fromString(page.getTemplateName());

        switch (template) {
            case ABOUT_US:
                break;

            case PRODUCT_LIST:
                List<Product> p = frontendService.getAllActiveProducts();
                model.addAttribute("products", p);
                break;

            default:
                break;
        }

        model.addAttribute("page", page);
        model.addAttribute("PageTemplate", PageTemplate.class);
        model.addAttribute("title", page.getTitle() + " - MyShop");

        return "pages/" + page.getTemplateName();
    }

}
