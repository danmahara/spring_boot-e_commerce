package com.ecommerce.config;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.ecommerce.models.admin.Admin;

import jakarta.servlet.http.HttpSession;

@ControllerAdvice("com.ecommerce.controllers.admin")
public class GlobalAdminAttributes {

    @ModelAttribute("admin")
    public Admin addAdminToModel(HttpSession session) {
        return (Admin) session.getAttribute("admin");
    }
}
