package com.ecommerce.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ecommerce.models.User;
import com.ecommerce.models.Wishlist;
import com.ecommerce.models.admin.Image;
import com.ecommerce.models.admin.Product;
import com.ecommerce.repository.WishListRepository;
import com.ecommerce.repository.admin.ImageRepository;
import com.ecommerce.security.CustomUserDetails;
import com.ecommerce.services.UserService;

import lombok.AllArgsConstructor;

@Controller
@RequestMapping("user/wishlist")
@AllArgsConstructor
public class WishlistController {

    private final WishListRepository wishListRepository;
    private final UserService userService;
    private final ImageRepository imageRepository;

    @GetMapping
    public String index(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {

        User user = userService.findUserById(userDetails.getId());

        List<Wishlist> wishlists = wishListRepository.findAllByUser(user);

        model.addAttribute("wishlists", wishlists);

        List<Product> products = new ArrayList<>();

        for (Wishlist item : wishlists) {
            products.add(item.getProduct());

        }

        for (Product p : products) {
            List<Image> imgs = imageRepository.findByImageableTypeAndImageableId("product", p.getId());
            p.setImages(imgs);
        }

        model.addAttribute("user", user);

        return "pages/customer/wishlist";
    }

    @GetMapping("/count")
    public long countWishlistItem(@AuthenticationPrincipal CustomUserDetails userDetails) {

        User user = userService.findUserById(userDetails.getId());
        return wishListRepository.countByUser(user);

    }

    // public String addToWishList(){

    // }

}
