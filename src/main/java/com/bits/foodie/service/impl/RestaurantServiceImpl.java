package com.bits.foodie.service.impl;

import com.bits.foodie.dto.MenuDTO;
import com.bits.foodie.dto.payload.LoginRequestDTO;
import com.bits.foodie.dto.RestaurantDTO;
import com.bits.foodie.mappers.MenuMapper;
import com.bits.foodie.model.*;
import com.bits.foodie.repository.CustomerRepository;
import com.bits.foodie.repository.RestaurantRepository;
import com.bits.foodie.dto.response.ResponseDTO;
import com.bits.foodie.service.RestaurantService;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RestaurantServiceImpl implements RestaurantService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MenuMapper menuMapper;


    @Override
    public ResponseDTO addRestaurant(RestaurantDTO restaurantDTO) {
        Restaurant restaurant = new Restaurant(
                restaurantDTO.getRestaurantName(),
                restaurantDTO.getRestaurantEmail(),
                this.passwordEncoder.encode(restaurantDTO.getPassword()),
                restaurantDTO.getRestaurantAdd(),
                restaurantDTO.getRole(),
                restaurantDTO.getCuisineType(),
                restaurantDTO.isVegOnly(),
                restaurantDTO.getOpeningHours(),
                restaurantDTO.getClosingHours(),
                restaurantDTO.getDeliveryZones());
        restaurantRepository.save(restaurant);
        return new ResponseDTO("Registration Successful", true);
    }

    @Override
    public ResponseDTO loginRestaurant(LoginRequestDTO loginRequestDTO) {
        String msg = "";
        Restaurant restaurant = restaurantRepository.findByRestaurantEmail(loginRequestDTO.getRestaurantEmail());
        if (restaurant != null && loginRequestDTO.getRole().equalsIgnoreCase(restaurant.getRole())) {
            String password = loginRequestDTO.getPassword();
            String encodedPassword = restaurant.getPassword();
            boolean isPwdRight = passwordEncoder.matches(password, encodedPassword);
            if (isPwdRight) {
                Optional<Restaurant> restaurant1 = restaurantRepository.findOneByRestaurantEmailAndPassword(restaurant.getRestaurantEmail(), restaurant.getPassword());
                if (restaurant1.isPresent()) {
                    return new ResponseDTO("Login Success", true);
                } else {
                    return new ResponseDTO("Login failed", false);
                }
            } else {
                return new ResponseDTO("Password Not Match", false);
            }
        } else {
            return new ResponseDTO("Email not exists", false);
        }
    }

    @Override
    public Restaurant updateRestaurant(RestaurantDTO restaurantDTO) {
        Restaurant restaurant = restaurantRepository.findByRestaurantEmail(restaurantDTO.getRestaurantEmail());
        if (restaurant != null) {
            if (restaurantDTO.getRestaurantName() != null) {
                restaurant.setRestaurantName(restaurantDTO.getRestaurantName());
            }
            if (restaurantDTO.getRestaurantAdd() != null) {
                restaurant.setRestaurantAdd(restaurantDTO.getRestaurantAdd());
            }
            restaurant.setHoursOfOperation(restaurantDTO.getHoursOfOperation());
            restaurantRepository.save(restaurant);
        }
        return restaurant;
    }

    @Override
    public Restaurant fetchRestaurantById(int restaurantId) {
        return restaurantRepository.findByRestaurantId(restaurantId);
    }

    @Override
    public Menu fetchMenu(int restaurantId) {
        Restaurant restaurant = restaurantRepository.findByRestaurantId(restaurantId);
        return restaurant.getMenu();
    }

    @Override
    public Menu UpdateMenu(int restaurantId, MenuDTO menuDTO) {
        Restaurant restaurant = restaurantRepository.findByRestaurantId(restaurantId);
        if (restaurant != null) {
            restaurant.setMenu(menuMapper.mapMenuDTOtoMenu(menuDTO));
            restaurantRepository.save(restaurant);
            return restaurant.getMenu();
        }
        return null;
    }

    @Override
    public List<Restaurant> fetchAllRestaurants() {
        return restaurantRepository.findAll();
    }

    @Override
    public List<Restaurant> searchRestaurant(String restaurantName, String cuisineType, Boolean isVegOnly,String foodItemName) {
        return restaurantRepository.searchRestaurants(restaurantName, cuisineType, isVegOnly,foodItemName);
    }

}
