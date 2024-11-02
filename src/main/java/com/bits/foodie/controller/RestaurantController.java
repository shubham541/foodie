package com.bits.foodie.controller;

import com.bits.foodie.dto.*;
import com.bits.foodie.dto.payload.LoginRequestDTO;
import com.bits.foodie.dto.payload.RegisterRequestDTO;
import com.bits.foodie.dto.response.ResponseDTO;
import com.bits.foodie.model.Customer;
import com.bits.foodie.model.Menu;
import com.bits.foodie.model.Restaurant;
import com.bits.foodie.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/restaurant")
public class RestaurantController {

    @Autowired
    RestaurantService restaurantService;

    public ResponseDTO saveRestaurant(@RequestBody RegisterRequestDTO registerRequestDTO) {
        RestaurantDTO restaurantDTO = new RestaurantDTO(registerRequestDTO.getRestaurantName(), registerRequestDTO.getRestaurantEmail(), registerRequestDTO.getPassword(),
                registerRequestDTO.getRestaurantAdd(), registerRequestDTO.getRole(), registerRequestDTO.getCuisineType(), registerRequestDTO.isVegOnly(),
                registerRequestDTO.getOpeningHours(), registerRequestDTO.getClosingHours(), registerRequestDTO.getDeliveryZones());
        return restaurantService.addRestaurant(restaurantDTO);

    }

    public ResponseDTO loginRestaurant(@RequestBody LoginRequestDTO loginRequestDTO) {
        return restaurantService.loginRestaurant(loginRequestDTO);
    }

    public List<Restaurant> fetchAllRestaurants() {
        return restaurantService.fetchAllRestaurants();
    }

    @PatchMapping(path = "/")
    public Restaurant updateRestaurant(@RequestBody RestaurantDTO restaurantDTO) {
        return restaurantService.updateRestaurant(restaurantDTO);
    }

    @GetMapping(path = "/id/{restaurantId}")
    public Restaurant fetchRestaurantById(@PathVariable("restaurantId") int restaurantId) {
        return restaurantService.fetchRestaurantById(restaurantId);
    }

    @GetMapping(path = "/id/{restaurantId}/menu")
    public Menu fetchAllMenuByRestaurantId(@PathVariable("restaurantId") int restaurantId) {
        return restaurantService.fetchMenu(restaurantId);
    }

    @PatchMapping(path = "/id/{restaurantId}/menu")
    public Menu updateMenuByRestaurantId(@PathVariable("restaurantId") int restaurantId, @RequestBody MenuDTO menuDTO) {
        return restaurantService.UpdateMenu(restaurantId, menuDTO);
    }

    public List<Restaurant> searchRestaurants(String restaurantName, String cuisineType, Boolean isVegOnly, String foodItemName) {
        return restaurantService.searchRestaurant(restaurantName, cuisineType, isVegOnly, foodItemName);
    }
}
