package com.bits.foodie.controller;

import com.bits.foodie.dto.*;
import com.bits.foodie.response.LoginResponse;
import com.bits.foodie.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class CustomerController {
    @Autowired
    CustomerService customerService;

    public void saveCustomer(@RequestBody RegisterDTO registerDTO) {
        CustomerDTO customerDTO = new CustomerDTO(registerDTO.getCustomerEmail(), registerDTO.getCustomerName(), registerDTO.getPassword(), registerDTO.getRole());
        customerService.addCustomer(customerDTO);
    }

    public LoginResponse loginCustomer(@RequestBody LoginDTO loginDTO) {
        return customerService.loginCustomer(loginDTO);
    }
}
