package com.bits.foodie.web;

import com.bits.foodie.dto.AuthDtos.UserDTO;
import com.bits.foodie.dto.CustomerDtos.ProfileUpdate;
import com.bits.foodie.security.AuthUser;
import com.bits.foodie.service.AuthService;
import com.bits.foodie.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/customers/me")
public class CustomerController {

    private final CustomerService customers;
    private final AuthService auth;

    public CustomerController(CustomerService customers, AuthService auth) {
        this.customers = customers;
        this.auth = auth;
    }

    @GetMapping
    public UserDTO profile(@AuthenticationPrincipal AuthUser user) {
        return auth.me(user.id());
    }

    @PatchMapping
    public UserDTO updateProfile(@AuthenticationPrincipal AuthUser user, @Valid @RequestBody ProfileUpdate update) {
        return customers.updateProfile(user.id(), update);
    }
}
