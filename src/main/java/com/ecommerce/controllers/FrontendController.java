package com.ecommerce.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.ecommerce.enums.PageTemplate;
import com.ecommerce.models.Page;
import com.ecommerce.repository.FrontendRepository;
import com.ecommerce.services.admin.PageService;

@Controller
public class FrontendController {

    @Autowired
    FrontendRepository frontendRepository;

    @Autowired
    PageService pageService;

    @GetMapping("/")
    public String homePageHandler(Model model) {

        model.addAttribute("title", "Home - MyShop");
        model.addAttribute("content", "pages/welcome :: content");
        model.addAttribute("pages", allPages());

        model.addAttribute("PageTemplate", PageTemplate.class);

        return "pages/welcome";
    }

    public List<Page> allPages() {
        return frontendRepository.findByStatusTrueOrderByOrderAsc();
    }

    @GetMapping("/products/{slug}")
    public String getProductDetailPage(Model model) {
        // model.addAttribute("page", page);

        model.addAttribute("PageTemplate", PageTemplate.class);
        // model.addAttribute("title", page.getTitle() + " - MyShop");

        model.addAttribute("pages", allPages());
        return "pages/product_detail";
    }

    @GetMapping("/{slug}")
    public String loadPage(@PathVariable String slug, Model model) {

        Optional<Page> pageOpt = frontendRepository.findBySlug(slug);
        if (pageOpt.isEmpty()) {
            // Return a custom 404 template
            return "error/404";
        }

        Page page = pageOpt.get();
        model.addAttribute("page", page);
        model.addAttribute("PageTemplate", PageTemplate.class);
        model.addAttribute("title", page.getTitle() + " - MyShop");
        model.addAttribute("pages", allPages());

        // Render the template based on templateName
        return "pages/" + page.getTemplateName();
    }

    // @GetMapping("/pages/json")
    // @ResponseBody
    // public List<Page> getPagesJson() {
    // return pageService.getAllPages();
    // }

}
