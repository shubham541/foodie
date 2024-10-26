package com.bits.foodie.controller;

import com.bits.foodie.dto.*;
import com.bits.foodie.response.LoginResponse;
import com.bits.foodie.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class RestaurantController {

    @Autowired
    RestaurantService restaurantService;

    public void saveRestaurant(@RequestBody RegisterDTO registerDTO) {
        RestaurantDTO restaurantDTO = new RestaurantDTO(registerDTO.getRestaurantName(), registerDTO.getRestaurantEmail(), registerDTO.getPassword(),
                registerDTO.getRestaurantAdd(), registerDTO.getRole(), registerDTO.getCuisineType(), registerDTO.isVegOnly(),
                registerDTO.getOpeningHours(), registerDTO.getClosingHours(), registerDTO.getDeliveryZones());
        restaurantService.addRestaurant(restaurantDTO);

    }

    public LoginResponse loginRestaurant(@RequestBody LoginDTO loginDTO) {
        return restaurantService.loginRestaurant(loginDTO);
    }
}
