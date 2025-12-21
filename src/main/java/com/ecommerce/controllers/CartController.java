package com.ecommerce.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("user/cart")
public class CartController {

    @GetMapping
    public String index() {
        return "pages/customer/cart";
    }

}
