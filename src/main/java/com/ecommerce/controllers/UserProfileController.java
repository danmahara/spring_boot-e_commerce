package com.ecommerce.controllers;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ecommerce.models.User;
import com.ecommerce.services.OrderService;
import com.ecommerce.services.UserService;
import com.ecommerce.services.WishlistService;

import lombok.AllArgsConstructor;

@Controller
@RequestMapping("user/profile")
@AllArgsConstructor
public class UserProfileController {

    // @GetMapping
    // public String profile() {
    // return "pages/customer/profile";
    // }

    private final UserService userService;

    private final OrderService orderService;
    private final WishlistService wishlistService;

    /**
     * Display user profile
     */
    @GetMapping
    public String profile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByEmail(userDetails.getUsername());

        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);
        model.addAttribute("userOrders", orderService.countOrdersByUserId(user.getId()));
        model.addAttribute("userWishlists", wishlistService.countByUserId(user.getId()));

        return "pages/customer/profile";
    }

}
