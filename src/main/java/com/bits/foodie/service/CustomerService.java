package com.bits.foodie.service;

import com.bits.foodie.dto.CustomerDTO;
import com.bits.foodie.dto.RegisterDTO;
import com.bits.foodie.dto.LoginDTO;
import com.bits.foodie.response.LoginResponse;


public interface CustomerService {
    String addCustomer(CustomerDTO customerDTO);

    LoginResponse loginCustomer(LoginDTO loginDTO);
}
