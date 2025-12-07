package com.ecommerce.controllers;

import java.util.List;

import org.springframework.boot.jackson.autoconfigure.JacksonProperties.Json;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.ecommerce.enums.PageTemplate;
import com.ecommerce.models.Page;
import com.ecommerce.models.admin.Product;
import com.ecommerce.services.FrontendService;
import org.springframework.web.bind.annotation.RequestParam;

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
        model.addAttribute("pages", allPages());

        model.addAttribute("PageTemplate", PageTemplate.class);

        return "pages/welcome";
    }

    public List<Page> allPages() {
        return frontendService.getAllActiveAndOnMainMenuPages();
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
        model.addAttribute("pages", allPages());

        return "pages/" + page.getTemplateName();
    }

    // @GetMapping("/pages/json")
    // @ResponseBody
    // public List<Page> getPagesJson() {
    // return pageService.getAllPages();
    // }

}
