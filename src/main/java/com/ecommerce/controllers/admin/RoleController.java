package com.ecommerce.controllers.admin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("admin/roles")
public class RoleController {

    @GetMapping({ "", "/" })
    public String index() {

        return "admin/role/index";
    }

    @GetMapping("create")
    public String create() {

        return "admin/role/create";
    }
}
