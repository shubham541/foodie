package com.bits.foodie.controller;

import com.bits.foodie.dto.*;
import com.bits.foodie.dto.payload.LoginRequestDTO;
import com.bits.foodie.dto.payload.RegisterRequestDTO;
import com.bits.foodie.dto.response.ResponseDTO;
import com.bits.foodie.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
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
}
