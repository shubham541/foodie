package com.bits.foodie.mappers;

import com.bits.foodie.dto.RestaurantDTO;
import com.bits.foodie.model.Restaurant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RestaurantMapper {

    @Autowired
    private MenuMapper menuMapper;

    public Restaurant mapRestaurantDTOtoRestaurant(RestaurantDTO restaurantDTO) {
        Restaurant restaurant = new Restaurant();
        restaurant.setRestaurantName(restaurantDTO.getRestaurantName());
        restaurant.setRestaurantEmail(restaurantDTO.getRestaurantEmail());
        restaurant.setPassword(restaurantDTO.getPassword());
        restaurant.setRestaurantAdd(restaurantDTO.getRestaurantAdd());
        restaurant.setMenu(menuMapper.mapMenuDTOtoMenu(restaurantDTO.getMenu()));
        restaurant.setRole(restaurantDTO.getRole());
        restaurant.setCuisineType(restaurantDTO.getCuisineType());
        restaurant.setVegOnly(restaurantDTO.isVegOnly());
        restaurant.setHoursOfOperation(restaurantDTO.getHoursOfOperation());
        restaurant.setOpeningHours(restaurantDTO.getOpeningHours());
        restaurant.setClosingHours(restaurantDTO.getClosingHours());
        restaurant.setDeliveryZones(restaurantDTO.getDeliveryZones());
        restaurant.setActive(restaurantDTO.isActive());
        restaurant.setOrderCount(restaurantDTO.getOrderCount());
        restaurant.setTimeConsumedPerOrderInMinutes(restaurantDTO.getTimeConsumedPerOrderInMinutes());
        return restaurant;
    }

    public RestaurantDTO mapRestaurantToRestaurantDTO(Restaurant restaurant) {
        RestaurantDTO restaurantDTO = new RestaurantDTO();
        restaurantDTO.setRestaurantName(restaurant.getRestaurantName());
        restaurantDTO.setRestaurantEmail(restaurant.getRestaurantEmail());
        restaurantDTO.setPassword(restaurant.getPassword());
        restaurantDTO.setRestaurantAdd(restaurant.getRestaurantAdd());
        restaurantDTO.setMenu(menuMapper.mapMenutoMenuDTO(restaurant.getMenu()));
        restaurantDTO.setRole(restaurant.getRole());
        restaurantDTO.setCuisineType(restaurant.getCuisineType());
        restaurantDTO.setVegOnly(restaurant.isVegOnly());
        restaurantDTO.setHoursOfOperation(restaurant.getHoursOfOperation());
        restaurantDTO.setOpeningHours(restaurant.getOpeningHours());
        restaurantDTO.setClosingHours(restaurant.getClosingHours());
        restaurantDTO.setDeliveryZones(restaurant.getDeliveryZones());
        restaurantDTO.setActive(restaurant.isActive());
        restaurantDTO.setOrderCount(restaurant.getOrderCount());
        restaurantDTO.setTimeConsumedPerOrderInMinutes(restaurant.getTimeConsumedPerOrderInMinutes());
        return restaurantDTO;
    }

}
