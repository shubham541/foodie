package com.bits.foodie;

import com.bits.foodie.controller.RestaurantController;
import com.bits.foodie.dto.MenuDTO;
import com.bits.foodie.dto.payload.RegisterRequestDTO;
import com.bits.foodie.dto.RestaurantDTO;
import com.bits.foodie.dto.payload.LoginRequestDTO;
import com.bits.foodie.dto.response.ResponseDTO;
import com.bits.foodie.model.Menu;
import com.bits.foodie.model.Order;
import com.bits.foodie.model.Restaurant;
import com.bits.foodie.service.RestaurantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RestaurantControllerTest {

    @InjectMocks
    private RestaurantController restaurantController;

    @Mock
    private RestaurantService restaurantService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveRestaurant_Success() {
        // Arrange
        RegisterRequestDTO registerRequestDTO = new RegisterRequestDTO();
        registerRequestDTO.setRestaurantName("Test Restaurant");
        registerRequestDTO.setRestaurantEmail("test@restaurant.com");
        ResponseDTO mockResponse = new ResponseDTO("Restaurant registered successfully", true);
        when(restaurantService.addRestaurant(any(RestaurantDTO.class))).thenReturn(mockResponse);

        // Act
        ResponseDTO response = restaurantController.saveRestaurant(registerRequestDTO);

        // Assert
        assertNotNull(response);
        assertEquals("Restaurant registered successfully", response.getMessage());
        verify(restaurantService, times(1)).addRestaurant(any(RestaurantDTO.class));
    }

    @Test
    void testLoginRestaurant_Success() {
        // Arrange
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO("test@restaurant.com", "password");
        ResponseDTO mockResponse = new ResponseDTO("Login successful", true);
        when(restaurantService.loginRestaurant(loginRequestDTO)).thenReturn(mockResponse);

        // Act
        ResponseDTO response = restaurantController.loginRestaurant(loginRequestDTO);

        // Assert
        assertNotNull(response);
        assertEquals("Login successful", response.getMessage());
        verify(restaurantService, times(1)).loginRestaurant(loginRequestDTO);
    }

    @Test
    void testFetchAllRestaurants_Success() {
        // Arrange
        List<Restaurant> mockRestaurants = new ArrayList<>();
        mockRestaurants.add(new Restaurant());
        when(restaurantService.fetchAllRestaurants()).thenReturn(mockRestaurants);

        // Act
        List<Restaurant> restaurants = restaurantController.fetchAllRestaurants();

        // Assert
        assertNotNull(restaurants);
        assertEquals(1, restaurants.size());
        verify(restaurantService, times(1)).fetchAllRestaurants();
    }

    @Test
    void testUpdateRestaurant_Success() {
        // Arrange
        int restaurantId = 1;
        RestaurantDTO restaurantDTO = new RestaurantDTO();
        Restaurant mockRestaurant = new Restaurant();
        mockRestaurant.setRestaurantId(restaurantId);
        when(restaurantService.updateRestaurant(restaurantId, restaurantDTO)).thenReturn(mockRestaurant);

        // Act
        Restaurant updatedRestaurant = restaurantController.updateRestaurant(restaurantId, restaurantDTO);

        // Assert
        assertNotNull(updatedRestaurant);
        assertEquals(restaurantId, updatedRestaurant.getRestaurantId());
        verify(restaurantService, times(1)).updateRestaurant(restaurantId, restaurantDTO);
    }

    @Test
    void testFetchRestaurantById_Success() {
        // Arrange
        int restaurantId = 1;
        Restaurant mockRestaurant = new Restaurant();
        mockRestaurant.setRestaurantId(restaurantId);
        when(restaurantService.fetchRestaurantById(restaurantId)).thenReturn(mockRestaurant);

        // Act
        Restaurant restaurant = restaurantController.fetchRestaurantById(restaurantId);

        // Assert
        assertNotNull(restaurant);
        assertEquals(restaurantId, restaurant.getRestaurantId());
        verify(restaurantService, times(1)).fetchRestaurantById(restaurantId);
    }

    @Test
    void testFetchAllMenuByRestaurantId_Success() {
        // Arrange
        int restaurantId = 1;
        Menu mockMenu = new Menu();
        when(restaurantService.fetchMenu(restaurantId)).thenReturn(mockMenu);

        // Act
        Menu menu = restaurantController.fetchAllMenuByRestaurantId(restaurantId);

        // Assert
        assertNotNull(menu);
        verify(restaurantService, times(1)).fetchMenu(restaurantId);
    }

    @Test
    void testUpdateMenuByRestaurantId_Success() {
        // Arrange
        int restaurantId = 1;
        MenuDTO menuDTO = new MenuDTO();
        Menu mockMenu = new Menu();
        when(restaurantService.updateMenu(restaurantId, menuDTO)).thenReturn(mockMenu);

        // Act
        Menu updatedMenu = restaurantController.updateMenuByRestaurantId(restaurantId, menuDTO);

        // Assert
        assertNotNull(updatedMenu);
        verify(restaurantService, times(1)).updateMenu(restaurantId, menuDTO);
    }

    @Test
    void testViewOrders_Success() {
        // Arrange
        int restaurantId = 1;
        List<Order> mockOrders = new ArrayList<>();
        mockOrders.add(new Order());
        when(restaurantService.viewOrders(restaurantId)).thenReturn(mockOrders);

        // Act
        List<Order> orders = restaurantController.viewOrders(restaurantId);

        // Assert
        assertNotNull(orders);
        assertEquals(1, orders.size());
        verify(restaurantService, times(1)).viewOrders(restaurantId);
    }

    @Test
    void testUpdateOrderStatus_Success() {
        // Arrange
        int restaurantId = 1;
        int orderId = 100;
        String status = "Completed";
        Order mockOrder = new Order();
        mockOrder.setOrderId(orderId);
        when(restaurantService.updateOrderStatus(restaurantId, orderId, status)).thenReturn(mockOrder);

        // Act
        Order updatedOrder = restaurantController.updateOrderStatus(restaurantId, orderId, status);

        // Assert
        assertNotNull(updatedOrder);
        assertEquals(orderId, updatedOrder.getOrderId());
        verify(restaurantService, times(1)).updateOrderStatus(restaurantId, orderId, status);
    }

    @Test
    void testSearchRestaurants_Success() {
        // Arrange
        String restaurantName = "Test Restaurant";
        String cuisineType = "Indian";
        Boolean isVegOnly = true;
        String foodItemName = "Paneer";
        List<Restaurant> mockRestaurants = new ArrayList<>();
        mockRestaurants.add(new Restaurant());
        when(restaurantService.searchRestaurant(restaurantName, cuisineType, isVegOnly, foodItemName))
                .thenReturn(mockRestaurants);

        // Act
        List<Restaurant> restaurants = restaurantController.searchRestaurants(restaurantName, cuisineType, isVegOnly, foodItemName);

        // Assert
        assertNotNull(restaurants);
        assertEquals(1, restaurants.size());
        verify(restaurantService, times(1)).searchRestaurant(restaurantName, cuisineType, isVegOnly, foodItemName);
    }

    @Test
    void testDeactivateRestaurant_Success() {
        // Arrange
        int restaurantId = 1;
        doNothing().when(restaurantService).deactivateRestaurant(restaurantId);

        // Act
        restaurantController.deactivateRestaurant(restaurantId);

        // Assert
        verify(restaurantService, times(1)).deactivateRestaurant(restaurantId);
    }
}
