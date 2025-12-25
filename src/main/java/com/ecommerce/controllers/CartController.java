package com.ecommerce.controllers;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ecommerce.models.User;
import com.ecommerce.services.UserService;

import lombok.AllArgsConstructor;

@Controller
@RequestMapping("user/cart")
@AllArgsConstructor
public class CartController {

    private final UserService userService;

    @GetMapping
    public String index() {
        return "pages/customer/cart";
    }

    @GetMapping("/checkout")
    public String checkout(@AuthenticationPrincipal UserDetails userDetails, Model model) {

        User user = userService.findByEmail(userDetails.getUsername());
        model.addAttribute("user", user);
        return "pages/customer/checkout";
    }

}
