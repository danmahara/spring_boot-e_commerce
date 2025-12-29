package com.ecommerce.controllers;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.ecommerce.enums.PageTemplate;
import com.ecommerce.models.User;
import com.ecommerce.models.admin.Page;
import com.ecommerce.models.admin.Product;
import com.ecommerce.repository.WishListRepository;
import com.ecommerce.services.FrontendService;
import com.ecommerce.services.UserService;
import com.ecommerce.utils.SecurityUtils;

@Controller
public class FrontendController {

    private final FrontendService frontendService;
    private final SecurityUtils securityUtils;
    private final UserService userService;
    private final WishListRepository wishListRepository;

    FrontendController(FrontendService frontendService, SecurityUtils securityUtils, UserService userService,
            WishListRepository wishListRepository) {
        this.frontendService = frontendService;
        this.securityUtils = securityUtils;
        this.userService = userService;
        this.wishListRepository = wishListRepository;
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

        System.out.println("LOgged in user: " + securityUtils.getCurrentUsername());
        User user = userService.findByEmail(securityUtils.getCurrentUsername());
        if (user != null) {
            model.addAttribute("user", user);
            boolean isWishlisted = wishListRepository.existsByUserIdAndProductId(user.getId(), product.getId());
            model.addAttribute("isWishlisted", isWishlisted);
        }

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
                Map<String, Object> data = frontendService.getProductListData();
                model.addAttribute("data", data);
                break;

            default:
                break;
        }

        model.addAttribute("page", page);
        model.addAttribute("PageTemplate", PageTemplate.class);
        model.addAttribute("title", page.getTitle() + " - MyShop");

        // redirect to pages/customer/login /register
        if (page.getTemplateName().equalsIgnoreCase(PageTemplate.LOGIN.getTemplateName())
                || page.getTemplateName().equalsIgnoreCase(PageTemplate.REGISTER.getTemplateName())) {
            System.out.println("Loading customer login page template");
            return "pages/customer/" + page.getTemplateName();

        } else {
            return "pages/" + page.getTemplateName();
        }

    }

    @GetMapping("/forgot-password")
    public String forgotPassword() {
        return "pages/forgot_password";
    }

}
