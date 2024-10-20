package com.bits.foodie.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "homepage";  // Redirecting to /home if you want a different route
    }

    @GetMapping("/home")
    public String homepage() {
        return "homepage";  // Spring will look for index.html in src/main/resources/templates/ or static/
    }
}
