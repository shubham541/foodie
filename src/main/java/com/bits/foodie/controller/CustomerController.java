package com.bits.foodie.controller;

import com.bits.foodie.dto.*;
import com.bits.foodie.dto.payload.LoginRequestDTO;
import com.bits.foodie.dto.payload.RegisterRequestDTO;
import com.bits.foodie.dto.response.ResponseDTO;
import com.bits.foodie.exception.ResourceNotFoundException;
import com.bits.foodie.model.Customer;
import com.bits.foodie.model.Order;
import com.bits.foodie.model.Restaurant;
import com.bits.foodie.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/v1/customer")
public class CustomerController {
    @Autowired
    CustomerService customerService;

    @Autowired
    RestaurantController restaurantController;

    public ResponseDTO saveCustomer(@RequestBody RegisterRequestDTO registerRequestDTO) {
        CustomerDTO customerDTO = new CustomerDTO(registerRequestDTO.getCustomerEmail(), registerRequestDTO.getCustomerName(),
                registerRequestDTO.getPassword(), registerRequestDTO.getRole());
        return customerService.addCustomer(customerDTO);
    }

    public ResponseDTO loginCustomer(@RequestBody LoginRequestDTO loginRequestDTO) {
        return customerService.loginCustomer(loginRequestDTO);
    }

    @PatchMapping(path = "/")
    public Customer updateCustomer(@RequestBody CustomerDTO customerDTO) {
        try {
            Customer customer = customerService.updateCustomer(customerDTO);
            if (customer == null) {
                throw new ResourceNotFoundException("Customer not found for update.");
            }
            return customer;
        } catch (ResourceNotFoundException ex) {
            throw ex;  // Re-throwing exception to be handled globally
        } catch (Exception ex) {
            throw new IllegalArgumentException("Failed to update customer: " + ex.getMessage());
        }
    }

    @GetMapping(path = "/id/{customerId}")
    public Customer fetchCustomerById(@PathVariable("customerId") int customerId) {
        Customer customer = customerService.fetchCustomerById(customerId);
        if (customer == null) {
            throw new ResourceNotFoundException("Customer not found with ID: " + customerId);
        }
        return customer;
    }

    @GetMapping(path = "/restaurants")
    public List<Restaurant> fetchAllRestaurants() {
        return restaurantController.fetchAllRestaurants();
    }

    @GetMapping(path = "/restaurants/search")
    public List<Restaurant> searchMenus(@RequestParam(required = false) String restaurantName,
                                        @RequestParam(required = false) String cuisineType,
                                        @RequestParam(required = false) Boolean isVegOnly,
                                        @RequestParam(required = false) String foodItemName) {
        return restaurantController.searchRestaurants(restaurantName, cuisineType, isVegOnly, foodItemName);
    }

    @PostMapping(path = "/id/{customerId}/placeOrder")
    public Order placeOrder(@PathVariable("customerId") int customerId) {
        return customerService.placeOrder(customerId);
    }

    @PostMapping(path = "/id/{customerId}/updateCart")
    public Customer updateCart(@PathVariable("customerId") int customerId, @RequestBody CartDTO cartDTO) {
        return customerService.updateCart(customerId, cartDTO);
    }

    @GetMapping(path = "/id/{customerId}/trackOrders")
    public Map<Integer, String> trackOrders(@PathVariable("customerId") int customerId) {
        return customerService.trackOrders(customerId);
    }

    @GetMapping(path = "/id/{customerId}/viewOrderHistory")
    public List<Order> viewOrderHistory(@PathVariable("customerId") int customerId) {
        return customerService.viewOrderHistory(customerId);
    }

    public void deactivateCustomer(int userId) {
        customerService.deactivateCustomer(userId);
    }
}
