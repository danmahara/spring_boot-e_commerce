package com.ecommerce.controllers;

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
import com.ecommerce.services.OrderService;
import com.ecommerce.services.UserService;

@Controller
@RequestMapping("user/orders")
public class OrderController {

    @Autowired
    UserService userService;

    @Autowired
    OrderService orderService;

    /**
     * Display all orders
     */
    @GetMapping
    public String orders(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByEmail(userDetails.getUsername());

        if (user == null) {
            return "redirect:/login";
        }

        List<Order> orders = orderService.findAllOrdersByUser(user.getId());

        model.addAttribute("user", user);
        model.addAttribute("orders", orders);

        return "pages/customer/order";
    }

}
