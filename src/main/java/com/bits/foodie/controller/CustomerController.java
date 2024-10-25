package com.bits.foodie.controller;

import com.bits.foodie.dto.AdminDTO;
import com.bits.foodie.dto.CustomerDTO;
import com.bits.foodie.dto.RegisterDTO;
import com.bits.foodie.dto.LoginDTO;
import com.bits.foodie.response.LoginResponse;
import com.bits.foodie.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("api/v1/customer")
public class CustomerController {
    @Autowired
    CustomerService customerService;

    @PostMapping(path = "/save")
    public String saveCustomer(@RequestBody RegisterDTO registerDTO){
        String role=registerDTO.getRole();
        if(role.equalsIgnoreCase("Customer")) {
            CustomerDTO customerDTO = new CustomerDTO(registerDTO.getCustomerEmail(), registerDTO.getCustomerName(), registerDTO.getPassword(), registerDTO.getRole());
            return customerService.addCustomer(customerDTO);
        } else if (role.equalsIgnoreCase("Admin")) {

        }else if(role.equalsIgnoreCase("DeliveryPerson")){

        }else if (role.equalsIgnoreCase("Restaurant")){

        }

        return role;
    }

    @PostMapping(path = "/login")
    public ResponseEntity<?> loginCustomer(@RequestBody LoginDTO loginDTO){
        LoginResponse loginResponse=customerService.loginCustomer(loginDTO);
        return ResponseEntity.ok(loginResponse);
    }
}
