package com.bits.foodie.controller;

import com.bits.foodie.dto.payload.LoginRequestDTO;
import com.bits.foodie.dto.payload.RegisterRequestDTO;
import com.bits.foodie.dto.response.ResponseDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class HomeController {
    @Autowired
    private CustomerController customerController;

    @Autowired
    private DeliveryPersonController deliveryPersonController;

    @Autowired
    private RestaurantController restaurantController;

    @GetMapping("/")
    public String home() {
        return "homepage";  // Redirects to /home if a different route is preferred
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
    public ResponseEntity<?> saveUser(@Valid @RequestBody RegisterRequestDTO registerRequestDTO) {
        String role = registerRequestDTO.getRole();
        ResponseDTO registerResponseDTO;

        switch (role.toLowerCase()) {
            case "customer":
                registerResponseDTO = customerController.saveCustomer(registerRequestDTO);
                break;
            case "deliveryperson":
                registerResponseDTO = deliveryPersonController.saveDeliveryPerson(registerRequestDTO);
                break;
            case "restaurant":
                registerResponseDTO = restaurantController.saveRestaurant(registerRequestDTO);
                break;
            default:
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid role specified.");
        }

        return registerResponseDTO != null && registerResponseDTO.getStatus() ?
                ResponseEntity.ok(registerResponseDTO) :
                new ResponseEntity<>("Registration unsuccessful", HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ResponseBody
    @PostMapping(path = "api/v1/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        String role = loginRequestDTO.getRole();
        ResponseDTO loginResponseDTO;

        switch (role.toLowerCase()) {
            case "customer":
                loginResponseDTO = customerController.loginCustomer(loginRequestDTO);
                break;
            case "deliveryperson":
                loginResponseDTO = deliveryPersonController.loginDeliveryPerson(loginRequestDTO);
                break;
            case "restaurant":
                loginResponseDTO = restaurantController.loginRestaurant(loginRequestDTO);
                break;
            default:
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid role specified.");
        }

        return loginResponseDTO != null && loginResponseDTO.getStatus() ?
                ResponseEntity.ok(loginResponseDTO) :
                new ResponseEntity<>("Invalid email or password", HttpStatus.UNAUTHORIZED);
    }
}
