package com.bits.foodie.service;

import com.bits.foodie.dto.CartDTO;
import com.bits.foodie.dto.CustomerDTO;
import com.bits.foodie.dto.payload.LoginRequestDTO;
import com.bits.foodie.dto.response.ResponseDTO;
import com.bits.foodie.model.Customer;
import com.bits.foodie.model.Order;

import java.util.List;
import java.util.Map;


public interface CustomerService {
    ResponseDTO addCustomer(CustomerDTO customerDTO);

    ResponseDTO loginCustomer(LoginRequestDTO loginRequestDTO);

    Customer updateCustomer(CustomerDTO customerDTO);

    Customer fetchCustomerById(int customerId);

    Customer updateCart(int customerId, CartDTO cartDTO);

    Order placeOrder(int customerId);

    Map<Integer, String> trackOrders(int customerId);

    List<Order> viewOrderHistory(int customerId);
}
