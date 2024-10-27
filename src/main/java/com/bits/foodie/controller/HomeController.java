package com.bits.foodie.controller;

import com.bits.foodie.dto.payload.LoginRequestDTO;
import com.bits.foodie.dto.payload.RegisterRequestDTO;
import com.bits.foodie.dto.response.ResponseDTO;
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
    public ResponseEntity<?> saveCustomer(@RequestBody RegisterRequestDTO registerRequestDTO) {
        String role = registerRequestDTO.getRole();
        ResponseDTO registerResponseDTO = null;
        if (role.equalsIgnoreCase("Customer")) {
            registerResponseDTO= customerController.saveCustomer(registerRequestDTO);
        } else if (role.equalsIgnoreCase("Admin")) {
            registerResponseDTO= adminController.saveAdmin(registerRequestDTO);
        } else if (role.equalsIgnoreCase("DeliveryPerson")) {
            registerResponseDTO= deliveryPersonController.saveDeliveryPerson(registerRequestDTO);
        } else if (role.equalsIgnoreCase("Restaurant")) {
            registerResponseDTO= restaurantController.saveRestaurant(registerRequestDTO);
        }
        return registerResponseDTO != null && registerResponseDTO.getStatus() ?
                ResponseEntity.ok(registerResponseDTO) : new ResponseEntity<>("Registration Successful", HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ResponseBody
    @PostMapping(path = "api/v1/login")
    public ResponseEntity<?> loginCustomer(@RequestBody LoginRequestDTO loginRequestDTO) {
        String role = loginRequestDTO.getRole();
        ResponseDTO loginResponseDTO = null;
        if (role.equalsIgnoreCase("Customer")) {
            loginResponseDTO = customerController.loginCustomer(loginRequestDTO);
        } else if (role.equalsIgnoreCase("Admin")) {
            loginResponseDTO = adminController.loginAdmin(loginRequestDTO);
        } else if (role.equalsIgnoreCase("DeliveryPerson")) {
            loginResponseDTO = deliveryPersonController.loginDeliveryPerson(loginRequestDTO);
        } else if (role.equalsIgnoreCase("Restaurant")) {
            loginResponseDTO = restaurantController.loginRestaurant(loginRequestDTO);
        }
        return loginResponseDTO != null && loginResponseDTO.getStatus() ?
                ResponseEntity.ok(loginResponseDTO) : new ResponseEntity<>("Invalid email or password", HttpStatus.UNAUTHORIZED);
    }
}
