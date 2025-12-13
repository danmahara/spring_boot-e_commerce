package com.ecommerce.controllers.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/permissions")
public class PermissionController {

    @GetMapping
    public String index() {
        return "admin/permission/index";
    }

    @GetMapping("/create")
    public String create() {
        return "admin/permission/create";
    }

    @GetMapping("/edit/{id}")
    public String edit() {
        return "admin/permission/edit";
    }

}
