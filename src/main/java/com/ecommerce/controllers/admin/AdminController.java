package com.ecommerce.controllers.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ecommerce.dtos.AdminLoginDTO;
import com.ecommerce.models.admin.Admin;
import com.ecommerce.services.admin.AdminService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/login")
    public String showLoginForm() {
        return "admin/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
            @RequestParam String password,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        try {
            log.info("Login attempt for email: {}", email);

            AdminLoginDTO dto = new AdminLoginDTO();
            dto.setEmail(email);
            dto.setPassword(password);

            Admin admin = adminService.login(dto);

            log.info("Login successful for admin: {}", admin.getEmail());
            log.info("Admin ID: {}", admin.getId());
            log.info("Admin Roles: {}", admin.getRoles());

            // Store admin in session
            session.setAttribute("admin", admin);

            // Verify it was stored
            Admin sessionAdmin = (Admin) session.getAttribute("admin");
            if (sessionAdmin != null) {
                log.info("Admin successfully stored in session");
            } else {
                log.error("Failed to store admin in session!");
            }

            redirectAttributes.addFlashAttribute("success", true);
            redirectAttributes.addFlashAttribute("message", "Welcome, " + admin.getFullName() + "!");

            return "redirect:/admin/dashboard";
        } catch (RuntimeException e) {
            log.error("Login error: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("auth_error", e.getMessage());
            return "redirect:/admin/login";
        }
    }

    @PostMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        log.info("Logout requested");

        session.removeAttribute("admin");
        session.invalidate();

        log.info("Session invalidated");

        redirectAttributes.addFlashAttribute("success", true);
        redirectAttributes.addFlashAttribute("message", "You have been logged out successfully");

        return "redirect:/admin/login";
    }
}