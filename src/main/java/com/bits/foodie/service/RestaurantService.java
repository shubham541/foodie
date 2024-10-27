package com.bits.foodie.service;

import com.bits.foodie.dto.payload.LoginRequestDTO;
import com.bits.foodie.dto.RestaurantDTO;
import com.bits.foodie.dto.response.ResponseDTO;

public interface RestaurantService {
    ResponseDTO addRestaurant(RestaurantDTO restaurantDTO);

    ResponseDTO loginRestaurant(LoginRequestDTO loginRequestDTO);
}
