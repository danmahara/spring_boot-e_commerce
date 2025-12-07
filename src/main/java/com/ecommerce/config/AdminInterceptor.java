package com.ecommerce.config;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.ecommerce.models.admin.Admin;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AdminInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        String requestURI = request.getRequestURI();

        // Log all requests for debugging
        log.info("Interceptor checking: {}", requestURI);

        HttpSession session = request.getSession(false);

        if (session != null) {
            log.info("Session ID: {}", session.getId());
            log.info("Session attributes: {}", java.util.Collections.list(session.getAttributeNames()));
        }

        Admin admin = (session != null)
                ? (Admin) session.getAttribute("admin")
                : null;

        if (admin == null) {
            log.warn("No admin found in session for: {}", requestURI);
            response.sendRedirect("/admin/login");
            return false;
        }

        log.info("Admin [{}] accessing: {}", admin.getEmail(), requestURI);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
            Object handler, Exception ex) throws Exception {

        if (ex != null) {
            HttpSession session = request.getSession(false);
            Admin admin = (session != null)
                    ? (Admin) session.getAttribute("admin")
                    : null;

            log.error("Error for admin [{}] on {}: {}",
                    admin != null ? admin.getEmail() : "UNKNOWN",
                    request.getRequestURI(),
                    ex.getMessage(), ex);
        }
    }
}