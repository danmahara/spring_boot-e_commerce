package com.ecommerce.controllers.advice;

import java.util.List;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

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

    @ModelAttribute("pages")
    public List<Page> navbarPages() {
        return frontendService.getAllActiveAndOnMainMenuPages();
    }

    @ModelAttribute("setting")
    public SiteSetting globalSiteSetting() {
        return settingService.findFirst();
    }
}
