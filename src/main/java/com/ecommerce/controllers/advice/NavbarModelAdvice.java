package com.ecommerce.controllers.advice;

import java.util.List;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import com.ecommerce.enums.PageTemplate;
import com.ecommerce.models.admin.Page;
import com.ecommerce.models.admin.SiteSetting;
import com.ecommerce.services.FrontendService;
import com.ecommerce.services.admin.SettingService;

@ControllerAdvice
public class NavbarModelAdvice {

    private final FrontendService frontendService;
    private final SettingService settingService;

    public NavbarModelAdvice(FrontendService frontendService, SettingService settingService) {
        this.frontendService = frontendService;
        this.settingService = settingService;
    }

    // Regular navbar pages (excluding login/register)
    @ModelAttribute("navPages")
    public List<Page> navbarPages() {
        return frontendService.getAllActiveAndOnMainMenuPages().stream()
                .filter(page -> !page.getTemplateName().equalsIgnoreCase(PageTemplate.LOGIN.getTemplateName())
                        && !page.getTemplateName().equalsIgnoreCase(PageTemplate.REGISTER.getTemplateName()))
                .toList();
    }

    // Login page (if exists)
    @ModelAttribute("loginPage")
    public Page loginPage() {
        return frontendService.getAllActiveAndOnMainMenuPages().stream()
                .filter(page -> page.getTemplateName().equalsIgnoreCase(PageTemplate.LOGIN.getTemplateName()))
                .findFirst()
                .orElse(null);
    }

    // Register page (if exists)
    @ModelAttribute("registerPage")
    public Page registerPage() {
        return frontendService.getAllActiveAndOnMainMenuPages().stream()
                .filter(page -> page.getTemplateName().equalsIgnoreCase(PageTemplate.REGISTER.getTemplateName()))
                .findFirst()
                .orElse(null);
    }

    @ModelAttribute("setting")
    public SiteSetting globalSiteSetting() {
        return settingService.findFirst();
    }
}