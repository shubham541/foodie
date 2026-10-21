package com.bits.foodie.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Serves the HTML shells. Pages hold no data: they call the REST API with the user's token,
 * and each dashboard script redirects to the login page when the user lacks the right role.
 */
@Controller
public class PageController {

    @GetMapping({"/", "/menu"})
    public String store() {
        return "pages/store";
    }

    @GetMapping("/login")
    public String login() {
        return "pages/login";
    }

    @GetMapping("/register")
    public String register() {
        return "pages/register";
    }

    @GetMapping("/orders")
    public String orders() {
        return "pages/orders";
    }

    @GetMapping("/dashboard/restaurant")
    public String restaurant() {
        return "pages/restaurant";
    }

}
