package com.bits.foodie.service;

import com.bits.foodie.dto.CustomerDTO;
import com.bits.foodie.dto.payload.LoginRequestDTO;
import com.bits.foodie.dto.response.ResponseDTO;
import com.bits.foodie.model.Customer;


public interface CustomerService {
    ResponseDTO addCustomer(CustomerDTO customerDTO);

    ResponseDTO loginCustomer(LoginRequestDTO loginRequestDTO);

    Customer updateCustomer(CustomerDTO customerDTO);

    Customer fetchCustomerById(int customerId);
}
