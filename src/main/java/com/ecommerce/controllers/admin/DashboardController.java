package com.ecommerce.controllers.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

// import com.ecommerce.repository.UserRepository;
import com.ecommerce.repository.FrontendRepository;

@Controller
@RequestMapping("/admin")
public class DashboardController {

    // @Autowired
    // private UserRepository userRepository;

    @Autowired
    private FrontendRepository frontendRepository;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {

        // long userCount = userRepository.count(); // total users
        long pageCount = frontendRepository.count(); // total pages

        // model.addAttribute("userCount", userCount);
        model.addAttribute("pageCount", pageCount);
        model.addAttribute("title", "Home | Dashboard");

        return "admin/dashboard"; // Thymeleaf template
    }
}
