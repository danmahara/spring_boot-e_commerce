package com.ecommerce.controllers.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ecommerce.dtos.AdminLoginDTO;
import com.ecommerce.models.admin.Admin;
import com.ecommerce.services.admin.AdminService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AuthController {

    @Autowired
    AdminService adminService;

    @GetMapping("/login")
    public String showLoginForm() {
        return "admin/login"; // Thymeleaf will render src/main/resources/templates/admin/login.html
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
            @RequestParam String password,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        try {
            AdminLoginDTO dto = new AdminLoginDTO();
            dto.setEmail(email);
            dto.setPassword(password);

            Admin admin = adminService.login(dto);

            // store admin in session
            session.setAttribute("admin", admin);

            redirectAttributes.addFlashAttribute("success", true);
            redirectAttributes.addFlashAttribute("message", "Welcome, " + admin.getFullName() + "!");

            return "redirect:/admin/dashboard";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("auth_error", e.getMessage());
            return "redirect:/admin/login";
        }
    }

    // @GetMapping("/dashboard")
    // public String dashboard(HttpSession session, Model model) {
    // Admin admin = (Admin) session.getAttribute("admin");
    // if (admin == null) {
    // return "redirect:/admin/login";
    // }
    // model.addAttribute("admin", admin);
    // return "admin/dashboard";
    // }

    @PostMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {

        session.removeAttribute("admin");
        session.invalidate();

        redirectAttributes.addFlashAttribute("success", true);
        redirectAttributes.addFlashAttribute("message", "You have been logged out successfully");

        return "redirect:/admin/login";
    }

}
