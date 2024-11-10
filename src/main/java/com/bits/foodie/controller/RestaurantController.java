package com.bits.foodie.controller;

import com.bits.foodie.dto.*;
import com.bits.foodie.dto.payload.LoginRequestDTO;
import com.bits.foodie.dto.payload.RegisterRequestDTO;
import com.bits.foodie.dto.response.ResponseDTO;
import com.bits.foodie.model.Menu;
import com.bits.foodie.model.Order;
import com.bits.foodie.model.Restaurant;
import com.bits.foodie.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/restaurant")
public class RestaurantController {

    @Autowired
    private RestaurantService restaurantService;

    // Internal use by HomeController for registering a restaurant
    public ResponseDTO saveRestaurant(RegisterRequestDTO registerRequestDTO) {
        RestaurantDTO restaurantDTO = new RestaurantDTO(registerRequestDTO.getRestaurantName(),
                registerRequestDTO.getRestaurantEmail(), registerRequestDTO.getPassword(),
                registerRequestDTO.getRestaurantAdd(), registerRequestDTO.getRole(),
                registerRequestDTO.getCuisineType(), registerRequestDTO.isVegOnly(),
                registerRequestDTO.getOpeningHours(), registerRequestDTO.getClosingHours(),
                registerRequestDTO.getDeliveryZones());
        return restaurantService.addRestaurant(restaurantDTO);
    }

    // Internal use by HomeController for restaurant login
    public ResponseDTO loginRestaurant(LoginRequestDTO loginRequestDTO) {
        return restaurantService.loginRestaurant(loginRequestDTO);
    }

    // Fetch all restaurants
    @GetMapping
    public List<Restaurant> fetchAllRestaurants() {
        return restaurantService.fetchAllRestaurants();
    }

    // Update restaurant details by ID
    @PatchMapping("/{restaurantId}")
    public Restaurant updateRestaurant(@PathVariable int restaurantId, @RequestBody RestaurantDTO restaurantDTO) {
        return restaurantService.updateRestaurant(restaurantId, restaurantDTO);
    }

    // Fetch restaurant by ID
    @GetMapping("/{restaurantId}")
    public Restaurant fetchRestaurantById(@PathVariable int restaurantId) {
        return restaurantService.fetchRestaurantById(restaurantId);
    }

    // Fetch menu by restaurant ID
    @GetMapping("/{restaurantId}/menu")
    public Menu fetchAllMenuByRestaurantId(@PathVariable int restaurantId) {
        return restaurantService.fetchMenu(restaurantId);
    }

    // Update menu by restaurant ID
    @PatchMapping("/{restaurantId}/menu")
    public Menu updateMenuByRestaurantId(@PathVariable int restaurantId, @RequestBody MenuDTO menuDTO) {
        return restaurantService.updateMenu(restaurantId, menuDTO);
    }

    // View orders by restaurant ID
    @GetMapping("/{restaurantId}/viewOrders")
    public List<Order> viewOrders(@PathVariable int restaurantId) {
        return restaurantService.viewOrders(restaurantId);
    }

    // Update order status
    @PatchMapping("/{restaurantId}/order/{orderId}/updateOrderStatus")
    public Order updateOrderStatus(@PathVariable int restaurantId, @PathVariable int orderId,
                                   @RequestParam String status) {
        return restaurantService.updateOrderStatus(restaurantId, orderId, status);
    }

    // Search restaurants based on multiple parameters
    @GetMapping("/search")
    public List<Restaurant> searchRestaurants(@RequestParam(required = false) String restaurantName,
                                              @RequestParam(required = false) String cuisineType,
                                              @RequestParam(required = false) Boolean isVegOnly,
                                              @RequestParam(required = false) String foodItemName) {
        return restaurantService.searchRestaurant(restaurantName, cuisineType, isVegOnly, foodItemName);
    }

    // Deactivate restaurant by ID
    @DeleteMapping("/{restaurantId}/deactivate")
    public void deactivateRestaurant(@PathVariable int restaurantId) {
        restaurantService.deactivateRestaurant(restaurantId);
    }
}
