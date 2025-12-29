package com.ecommerce.controllers;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ecommerce.models.User;
import com.ecommerce.repository.WishListRepository;
import com.ecommerce.security.CustomUserDetails;
import com.ecommerce.services.UserService;

import lombok.AllArgsConstructor;

@Controller
@RequestMapping("user/wishlist")
@AllArgsConstructor
public class WishlistController {

    private final WishListRepository wishListRepository;
    private final UserService userService;

    @GetMapping("/count")
    public long countWishlistItem(@AuthenticationPrincipal CustomUserDetails userDetails) {

        User user = userService.findUserById(userDetails.getId());
        return wishListRepository.countByUser(user);

    }

    // public String addToWishList(){

    // }

}
