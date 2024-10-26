package com.bits.foodie.controller;

import com.bits.foodie.dto.*;
import com.bits.foodie.response.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class HomeController {
    @Autowired
    CustomerController customerController;

    @Autowired
    AdminController adminController;

    @Autowired
    DeliveryPersonController deliveryPersonController;

    @Autowired
    RestaurantController restaurantController;


    @GetMapping("/")
    public String home() {
        return "homepage";  // Redirecting to /home if you want a different route
    }

    @GetMapping("/home")
    public String homepage() {
        return "homepage";  // Spring will look for index.html in src/main/resources/templates/ or static/
    }

    @GetMapping("/login")
    public String login() {
        return "login";  // Spring will look for index.html in src/main/resources/templates/ or static/
    }

    @GetMapping("/register")
    public String register() {
        return "register";  // Spring will look for index.html in src/main/resources/templates/ or static/
    }

    @ResponseBody
    @PostMapping(path = "api/v1/save")
    public void saveCustomer(@RequestBody RegisterDTO registerDTO) {
        String role = registerDTO.getRole();
        if (role.equalsIgnoreCase("Customer")) {
            customerController.saveCustomer(registerDTO);
        } else if (role.equalsIgnoreCase("Admin")) {
            adminController.saveAdmin(registerDTO);
        } else if (role.equalsIgnoreCase("DeliveryPerson")) {
            deliveryPersonController.saveDeliveryPerson(registerDTO);
        } else if (role.equalsIgnoreCase("Restaurant")) {
            restaurantController.saveRestaurant(registerDTO);
        }
    }

    @ResponseBody
    @PostMapping(path = "api/v1/login")
    public ResponseEntity<?> loginCustomer(@RequestBody LoginDTO loginDTO) {
        String role = loginDTO.getRole();
        LoginResponse loginResponse = null;
        if (role.equalsIgnoreCase("Customer")) {
            loginResponse = customerController.loginCustomer(loginDTO);
        } else if (role.equalsIgnoreCase("Admin")) {
            loginResponse = adminController.loginAdmin(loginDTO);
        } else if (role.equalsIgnoreCase("DeliveryPerson")) {
            loginResponse = deliveryPersonController.loginDeliveryPerson(loginDTO);
        } else if (role.equalsIgnoreCase("Restaurant")) {
            loginResponse = restaurantController.loginRestaurant(loginDTO);
        }
        return loginResponse != null && loginResponse.getStatus() ?
                ResponseEntity.ok(loginResponse) : new ResponseEntity<>("Invalid email or password", HttpStatus.UNAUTHORIZED);
    }
}
