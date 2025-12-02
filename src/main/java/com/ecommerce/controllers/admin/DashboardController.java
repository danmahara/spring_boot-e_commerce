package com.ecommerce.controllers.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ecommerce.models.Page;
import com.ecommerce.repository.admin.DashboardRepository;

@Controller
@RequestMapping("/admin")
public class DashboardController {

    // @Autowired
    // private UserRepository userRepository;

    @Autowired
    private DashboardRepository dashboardRepository;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {

        long pageCount = dashboardRepository.countByTemplateNameIn(Page.getPageLists());

        model.addAttribute("pageCount", pageCount);
        model.addAttribute("title", "Home | Dashboard");

        return "admin/dashboard"; // Thymeleaf template
    }
}
