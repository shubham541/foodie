package com.bits.foodie.service;

import com.bits.foodie.dto.CartDTO;
import com.bits.foodie.dto.CustomerDTO;
import com.bits.foodie.dto.payload.LoginRequestDTO;
import com.bits.foodie.dto.response.ResponseDTO;
import com.bits.foodie.model.Customer;
import com.bits.foodie.model.Order;


public interface CustomerService {
    ResponseDTO addCustomer(CustomerDTO customerDTO);

    ResponseDTO loginCustomer(LoginRequestDTO loginRequestDTO);

    Customer updateCustomer(CustomerDTO customerDTO);

    Customer fetchCustomerById(int customerId);

    Customer updateCart(int customerId, CartDTO cartDTO);

    Order placeOrder(int customerId);
}
