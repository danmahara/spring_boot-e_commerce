package com.ecommerce.controllers;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ecommerce.models.Order;
import com.ecommerce.models.User;
import com.ecommerce.repository.WishListRepository;
import com.ecommerce.services.OrderService;
import com.ecommerce.services.UserService;

@Controller
@RequestMapping("/user")
public class UserDashboardController {

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private WishListRepository wishListRepository;

    /**
     * Display customer dashboard
     */
    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        // Get the logged-in user
        User user = userService.findByEmail(userDetails.getUsername());

        System.out.println("Authorities: " + userDetails.getAuthorities());

        if (user == null) {
            return "redirect:/login";
        }

        // Get user's orders (recent 5)
        List<Order> recentOrders = orderService.findRecentOrdersByUser(user.getId(), 5);

        // Calculate statistics
        int totalOrders = orderService.countOrdersByUserId(user.getId());
        BigDecimal totalSpent = orderService.calculateTotalSpentByUser(user.getId());
        int pendingOrders = orderService.countPendingOrdersByUser(user.getId());
        int savedItems = 0; // Implement wishlist count if you have wishlist feature

        // Add attributes to model
        model.addAttribute("user", user);
        model.addAttribute("orders", recentOrders);
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("totalSpent", totalSpent != null ? totalSpent : BigDecimal.ZERO);
        model.addAttribute("pendingOrders", pendingOrders);
        model.addAttribute("savedItems", savedItems);
        model.addAttribute("wishlistCount", wishListRepository.countByUser(user));

        return "pages/customer/dashboard";

    }

    /**
     * Display wishlist
     */
    @GetMapping("/wishlist")
    public String wishlist(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByEmail(userDetails.getUsername());

        if (user == null) {
            return "redirect:/login";
        }

        // Implement wishlist logic here
        model.addAttribute("user", user);

        return "customer/wishlist";
    }

    /**
     * Display addresses
     */
    @GetMapping("/addresses")
    public String addresses(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByEmail(userDetails.getUsername());

        if (user == null) {
            return "redirect:/login";
        }

        // Implement address management logic here
        model.addAttribute("user", user);

        return "customer/addresses";
    }
}