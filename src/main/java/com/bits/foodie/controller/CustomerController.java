package com.bits.foodie.controller;

import com.bits.foodie.dto.*;
import com.bits.foodie.response.LoginResponse;
import com.bits.foodie.service.AdminService;
import com.bits.foodie.service.CustomerService;
import com.bits.foodie.service.DeliveryPersonService;
import com.bits.foodie.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("api/v1/customer")
public class CustomerController {
    @Autowired
    CustomerService customerService;

    @Autowired
    AdminService adminService;

    @Autowired
    DeliveryPersonService deliveryPersonService;

    @Autowired
    RestaurantService restaurantService;

    @PostMapping(path = "/save")
    public String saveCustomer(@RequestBody RegisterDTO registerDTO) {
        String role = registerDTO.getRole();
        if (role.equalsIgnoreCase("Customer")) {
            CustomerDTO customerDTO = new CustomerDTO(registerDTO.getCustomerEmail(), registerDTO.getCustomerName(), registerDTO.getPassword(), registerDTO.getRole());
            return customerService.addCustomer(customerDTO);
        } else if (role.equalsIgnoreCase("Admin")) {
            AdminDTO adminDTO = new AdminDTO(registerDTO.getAdminEmail(), registerDTO.getAdminName(), registerDTO.getPassword(), registerDTO.getRole());
            return adminService.addAdmin(adminDTO);
        } else if (role.equalsIgnoreCase("DeliveryPerson")) {
            DeliveryPersonDTO deliveryPersonDTO = new DeliveryPersonDTO(registerDTO.getDevEmail(), registerDTO.getDevName(),
                    registerDTO.getPassword(), registerDTO.getContactDetails(), registerDTO.getVehicleType(), registerDTO.getRole());
            return deliveryPersonService.addDeliveryPerson(deliveryPersonDTO);
        } else if (role.equalsIgnoreCase("Restaurant")) {
            RestaurantDTO restaurantDTO = new RestaurantDTO(registerDTO.getRestaurantName(), registerDTO.getRestaurantEmail(), registerDTO.getPassword(),
                    registerDTO.getRestaurantAdd(), registerDTO.getRole(), registerDTO.getCuisineType(), registerDTO.isVegOnly(),
                    registerDTO.getOpeningHours(), registerDTO.getClosingHours(), registerDTO.getDeliveryZones());
            return restaurantService.addRestaurant(restaurantDTO);
        }

        return role;
    }

    @PostMapping(path = "/login")
    public ResponseEntity<?> loginCustomer(@RequestBody LoginDTO loginDTO) {
        LoginResponse loginResponse = customerService.loginCustomer(loginDTO);
        return ResponseEntity.ok(loginResponse);
    }
}
