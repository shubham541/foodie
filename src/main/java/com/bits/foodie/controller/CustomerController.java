package com.bits.foodie.controller;

import com.bits.foodie.dto.*;
import com.bits.foodie.dto.payload.LoginRequestDTO;
import com.bits.foodie.dto.payload.RegisterRequestDTO;
import com.bits.foodie.dto.response.ResponseDTO;
import com.bits.foodie.model.Customer;
import com.bits.foodie.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/customer")
public class CustomerController {
    @Autowired
    CustomerService customerService;

    public ResponseDTO saveCustomer(@RequestBody RegisterRequestDTO registerRequestDTO) {
        CustomerDTO customerDTO = new CustomerDTO(registerRequestDTO.getCustomerEmail(), registerRequestDTO.getCustomerName(), registerRequestDTO.getPassword(), registerRequestDTO.getRole());
        return customerService.addCustomer(customerDTO);
    }

    public ResponseDTO loginCustomer(@RequestBody LoginRequestDTO loginRequestDTO) {
        return customerService.loginCustomer(loginRequestDTO);
    }

    @PatchMapping(path = "/")
    public Customer updateCustomer(@RequestBody CustomerDTO customerDTO) {
        return customerService.updateCustomer(customerDTO);
    }

    @GetMapping(path = "/id/{customerId}")
    public Customer fetchCustomerById(@PathVariable("customerId") int customerId) {
        return customerService.fetchCustomerById(customerId);
    }

}
