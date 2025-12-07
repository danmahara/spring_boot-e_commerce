package com.ecommerce.aspects;

import com.ecommerce.annotations.RequirePermission;
import com.ecommerce.annotations.RequireRole;
import com.ecommerce.models.admin.Admin;
import jakarta.servlet.http.HttpSession;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class PermissionCheckAspect {

    @Around("@annotation(requirePermission)")
    public Object checkPermission(ProceedingJoinPoint joinPoint, RequirePermission requirePermission) throws Throwable {
        Admin admin = getAdminFromSession();

        if (admin == null) {
            throw new RuntimeException("Unauthorized: Admin not logged in");
        }

        if (!admin.hasPermission(requirePermission.value())) {
            throw new RuntimeException("Access Denied: Permission '" + requirePermission.value() + "' required");
        }

        return joinPoint.proceed();
    }

    @Around("@annotation(requireRole)")
    public Object checkRole(ProceedingJoinPoint joinPoint, RequireRole requireRole) throws Throwable {
        Admin admin = getAdminFromSession();

        if (admin == null) {
            throw new RuntimeException("Unauthorized: Admin not logged in");
        }

        boolean hasRole = false;
        for (String role : requireRole.value()) {
            if (admin.hasRole(role)) {
                hasRole = true;
                break;
            }
        }

        if (!hasRole) {
            throw new RuntimeException(
                    "Access Denied: One of the roles " + java.util.Arrays.toString(requireRole.value()) + " required");
        }

        return joinPoint.proceed();
    }

    private Admin getAdminFromSession() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpSession session = attributes.getRequest().getSession(false);
            if (session != null) {
                return (Admin) session.getAttribute("admin");
            }
        }
        return null;
    }
}