package com.togedy.togedy_server_v2.domain.admin.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @GetMapping("/login")
    public String loginPage() {
        return "admin/login";
    }

    @GetMapping
    public String mainPage() {
        return "admin/index";
    }
}
