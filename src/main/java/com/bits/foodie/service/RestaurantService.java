package com.bits.foodie.service;

import com.bits.foodie.dto.MenuDTO;
import com.bits.foodie.dto.payload.LoginRequestDTO;
import com.bits.foodie.dto.RestaurantDTO;
import com.bits.foodie.dto.response.ResponseDTO;
import com.bits.foodie.model.Menu;
import com.bits.foodie.model.Order;
import com.bits.foodie.model.Restaurant;

import java.util.List;

public interface RestaurantService {
    ResponseDTO addRestaurant(RestaurantDTO restaurantDTO);

    ResponseDTO loginRestaurant(LoginRequestDTO loginRequestDTO);

    Restaurant updateRestaurant(int restaurantId, RestaurantDTO restaurantDTO);

    Restaurant fetchRestaurantById(int restaurantId);

    Menu fetchMenu(int restaurantId);

    Menu UpdateMenu(int restaurantId, MenuDTO menuDTO);

    List<Restaurant> fetchAllRestaurants();

    List<Restaurant> searchRestaurant(String restaurantName, String cuisineType, Boolean isVegOnly,String foodItemName);

    List<Order> viewOrders(int restaurantId);

    Order updateOrderStatus(int restaurantId, int orderId, String status);
}
