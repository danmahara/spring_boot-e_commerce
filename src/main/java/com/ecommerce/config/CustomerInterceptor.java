package com.ecommerce.config;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import com.ecommerce.models.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CustomerInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        HttpSession session = request.getSession(false);
        User customer = (session != null) ? (User) session.getAttribute("customer") : null;

        if (customer == null) {
            log.warn("Customer not logged in, redirecting to /login");
            response.sendRedirect("/login");
            return false;
        }

        return true;
    }
}
