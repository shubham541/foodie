package com.bits.foodie.test.controller;

import com.bits.foodie.controller.CustomerController;
import com.bits.foodie.controller.RestaurantController;
import com.bits.foodie.dto.CartDTO;
import com.bits.foodie.dto.CustomerDTO;
import com.bits.foodie.dto.payload.LoginRequestDTO;
import com.bits.foodie.dto.payload.RegisterRequestDTO;
import com.bits.foodie.dto.response.ResponseDTO;
import com.bits.foodie.model.Customer;
import com.bits.foodie.model.Order;
import com.bits.foodie.model.Restaurant;
import com.bits.foodie.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomerControllerTest {

    @InjectMocks
    private CustomerController customerController;

    @Mock
    private CustomerService customerService;

    @Mock
    private RestaurantController restaurantController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveCustomer_Success() {
        // Arrange
        RegisterRequestDTO registerRequestDTO = new RegisterRequestDTO();
        registerRequestDTO.setCustomerEmail("test@customer.com");
        registerRequestDTO.setCustomerName("Test Customer");
        registerRequestDTO.setPassword("password");
        ResponseDTO mockResponse = new ResponseDTO("Customer registered successfully", true);
        when(customerService.addCustomer(any(CustomerDTO.class))).thenReturn(mockResponse);

        // Act
        ResponseDTO response = customerController.saveCustomer(registerRequestDTO);

        // Assert
        assertNotNull(response);
        assertEquals("Customer registered successfully", response.getMessage());
        verify(customerService, times(1)).addCustomer(any(CustomerDTO.class));
    }

    @Test
    void testLoginCustomer_Success() {
        // Arrange
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO("test@customer.com", "password");
        ResponseDTO mockResponse = new ResponseDTO("Login successful", true);
        when(customerService.loginCustomer(loginRequestDTO)).thenReturn(mockResponse);

        // Act
        ResponseDTO response = customerController.loginCustomer(loginRequestDTO);

        // Assert
        assertNotNull(response);
        assertEquals("Login successful", response.getMessage());
        verify(customerService, times(1)).loginCustomer(loginRequestDTO);
    }

    @Test
    void testUpdateCustomer_Success() {
        // Arrange
        CustomerDTO customerDTO = new CustomerDTO();
        Customer mockCustomer = new Customer();
        mockCustomer.setCustomerId(1);
        when(customerService.updateCustomer(customerDTO)).thenReturn(mockCustomer);

        // Act
        Customer updatedCustomer = customerController.updateCustomer(customerDTO);

        // Assert
        assertNotNull(updatedCustomer);
        assertEquals(1, updatedCustomer.getCustomerId());
        verify(customerService, times(1)).updateCustomer(customerDTO);
    }

    @Test
    void testFetchCustomerById_Success() {
        // Arrange
        int customerId = 1;
        Customer mockCustomer = new Customer();
        mockCustomer.setCustomerId(customerId);
        when(customerService.fetchCustomerById(customerId)).thenReturn(mockCustomer);

        // Act
        Customer customer = customerController.fetchCustomerById(customerId);

        // Assert
        assertNotNull(customer);
        assertEquals(customerId, customer.getCustomerId());
        verify(customerService, times(1)).fetchCustomerById(customerId);
    }

    @Test
    void testFetchAllRestaurants_Success() {
        // Arrange
        List<Restaurant> mockRestaurants = new ArrayList<>();
        mockRestaurants.add(new Restaurant());
        when(restaurantController.fetchAllRestaurants()).thenReturn(mockRestaurants);

        // Act
        List<Restaurant> restaurants = customerController.fetchAllRestaurants();

        // Assert
        assertNotNull(restaurants);
        assertEquals(1, restaurants.size());
        verify(restaurantController, times(1)).fetchAllRestaurants();
    }

    @Test
    void testSearchMenus_Success() {
        // Arrange
        String restaurantName = "Test Restaurant";
        String cuisineType = "Indian";
        Boolean isVegOnly = true;
        String foodItemName = "Paneer";
        List<Restaurant> mockRestaurants = new ArrayList<>();
        mockRestaurants.add(new Restaurant());
        when(restaurantController.searchRestaurants(restaurantName, cuisineType, isVegOnly, foodItemName))
                .thenReturn(mockRestaurants);

        // Act
        List<Restaurant> restaurants = customerController.searchMenus(restaurantName, cuisineType, isVegOnly, foodItemName);

        // Assert
        assertNotNull(restaurants);
        assertEquals(1, restaurants.size());
        verify(restaurantController, times(1)).searchRestaurants(restaurantName, cuisineType, isVegOnly, foodItemName);
    }

    @Test
    void testPlaceOrder_Success() {
        // Arrange
        int customerId = 1;
        Order mockOrder = new Order();
        mockOrder.setOrderId(100);
        when(customerService.placeOrder(customerId)).thenReturn(mockOrder);

        // Act
        Order placedOrder = customerController.placeOrder(customerId);

        // Assert
        assertNotNull(placedOrder);
        assertEquals(100, placedOrder.getOrderId());
        verify(customerService, times(1)).placeOrder(customerId);
    }

    @Test
    void testUpdateCart_Success() {
        // Arrange
        int customerId = 1;
        CartDTO cartDTO = new CartDTO();
        Customer mockCustomer = new Customer();
        mockCustomer.setCustomerId(customerId);
        when(customerService.updateCart(customerId, cartDTO)).thenReturn(mockCustomer);

        // Act
        Customer updatedCustomer = customerController.updateCart(customerId, cartDTO);

        // Assert
        assertNotNull(updatedCustomer);
        assertEquals(customerId, updatedCustomer.getCustomerId());
        verify(customerService, times(1)).updateCart(customerId, cartDTO);
    }

    @Test
    void testTrackOrders_Success() {
        // Arrange
        int customerId = 1;
        Map<Integer, String> mockOrderStatus = new HashMap<>();
        mockOrderStatus.put(100, "Delivered");
        when(customerService.trackOrders(customerId)).thenReturn(mockOrderStatus);

        // Act
        Map<Integer, String> orderStatus = customerController.trackOrders(customerId);

        // Assert
        assertNotNull(orderStatus);
        assertEquals("Delivered", orderStatus.get(100));
        verify(customerService, times(1)).trackOrders(customerId);
    }

    @Test
    void testViewOrderHistory_Success() {
        // Arrange
        int customerId = 1;
        List<Order> mockOrders = new ArrayList<>();
        mockOrders.add(new Order());
        when(customerService.viewOrderHistory(customerId)).thenReturn(mockOrders);

        // Act
        List<Order> orderHistory = customerController.viewOrderHistory(customerId);

        // Assert
        assertNotNull(orderHistory);
        assertEquals(1, orderHistory.size());
        verify(customerService, times(1)).viewOrderHistory(customerId);
    }

    @Test
    void testDeactivateCustomer_Success() {
        // Arrange
        int userId = 1;
        doNothing().when(customerService).deactivateCustomer(userId);

        // Act
        customerController.deactivateCustomer(userId);

        // Assert
        verify(customerService, times(1)).deactivateCustomer(userId);
    }
}
