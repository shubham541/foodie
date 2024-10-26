package com.bits.foodie.service;

import com.bits.foodie.dto.LoginDTO;
import com.bits.foodie.dto.RestaurantDTO;
import com.bits.foodie.response.LoginResponse;

public interface RestaurantService {
    String addRestaurant(RestaurantDTO restaurantDTO);

    LoginResponse loginRestaurant(LoginDTO loginDTO);
}
