package com.ecommerce.controllers.admin;

import java.util.Set;

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
    public String showLoginForm(HttpSession session) {
        Admin admin = (Admin) session.getAttribute("admin");
        if (admin != null) {
            return "redirect:/admin/dashboard";
        }
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

            // login() returns Admin with all roles and permissions loaded
            Admin admin = adminService.login(dto);

            log.info("Login successful for admin: {}", admin.getEmail());
            log.info("Admin ID: {}", admin.getId());

            // Safe null checking before logging
            if (admin.getRoles() != null && !admin.getRoles().isEmpty()) {
                log.info("Admin Roles: {}", admin.getRoles().stream()
                        .map(r -> r.getName())
                        .collect(java.util.stream.Collectors.toList()));
            } else {
                log.warn("Admin has no roles assigned");
            }

            Set<?> permissions = admin.getAllPermissions();
            if (permissions != null && !permissions.isEmpty()) {
                log.info("Admin Permissions: {}", permissions.stream()
                        .map(p -> ((com.ecommerce.models.admin.Permission) p).getName())
                        .collect(java.util.stream.Collectors.toList()));
            } else {
                log.warn("Admin has no permissions assigned");
            }

            // Store admin in session
            session.setAttribute("admin", admin);
            session.setAttribute("adminId", admin.getId());
            session.setAttribute("adminEmail", admin.getEmail());
            session.setAttribute("adminName", admin.getFullName());

            // Verify it was stored
            Admin sessionAdmin = (Admin) session.getAttribute("admin");
            if (sessionAdmin != null) {
                int roleCount = sessionAdmin.getRoles() != null ? sessionAdmin.getRoles().size() : 0;
                int permCount = sessionAdmin.getAllPermissions() != null ? sessionAdmin.getAllPermissions().size() : 0;

                log.info("Admin successfully stored in session with {} roles and {} permissions",
                        roleCount, permCount);
            } else {
                log.error("Failed to store admin in session!");
                redirectAttributes.addFlashAttribute("auth_error", "Session storage failed");
                return "redirect:/admin/login";
            }

            redirectAttributes.addFlashAttribute("success", true);
            redirectAttributes.addFlashAttribute("message", "Welcome, " + admin.getFullName() + "!");

            return "redirect:/admin/dashboard";

        } catch (Exception e) {
            log.error("Login error: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("auth_error", e.getMessage());
            return "redirect:/admin/login";
        }
    }

    @PostMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        Admin admin = (Admin) session.getAttribute("admin");
        if (admin != null) {
            log.info("Logging out admin: {}", admin.getEmail());
        }

        session.removeAttribute("admin");
        session.removeAttribute("adminId");
        session.removeAttribute("adminEmail");
        session.removeAttribute("adminName");
        session.invalidate();

        log.info("Session invalidated");

        redirectAttributes.addFlashAttribute("success", true);
        redirectAttributes.addFlashAttribute("message", "You have been logged out successfully");

        return "redirect:/admin/login";
    }

    @GetMapping("/dashboard")
    public String showDashboard(HttpSession session, RedirectAttributes redirectAttributes) {
        Admin admin = (Admin) session.getAttribute("admin");
        if (admin == null) {
            redirectAttributes.addFlashAttribute("auth_error", "Please log in first");
            return "redirect:/admin/login";
        }

        log.info("Admin {} accessed dashboard", admin.getEmail());
        return "admin/dashboard";
    }
}