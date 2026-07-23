package com.charging.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpaForwardController {

    @GetMapping({"/", "/login", "/admin/**", "/miniapp/**"})
    public String forward() {
        return "forward:/index.html";
    }
}
