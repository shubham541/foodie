package com.bits.foodie.service.impl;

import com.bits.foodie.dto.MenuDTO;
import com.bits.foodie.dto.payload.LoginRequestDTO;
import com.bits.foodie.dto.RestaurantDTO;
import com.bits.foodie.mappers.MenuMapper;
import com.bits.foodie.model.*;
import com.bits.foodie.repository.CustomerRepository;
import com.bits.foodie.repository.OrderRepository;
import com.bits.foodie.repository.RestaurantRepository;
import com.bits.foodie.dto.response.ResponseDTO;
import com.bits.foodie.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;

@Service
public class RestaurantServiceImpl implements RestaurantService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    OrderRepository orderRepository;

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
    public Restaurant updateRestaurant(int restaurantId, RestaurantDTO restaurantDTO) {
        Restaurant restaurant = restaurantRepository.findByRestaurantId(restaurantId);
        if (restaurant != null) {
            if (restaurantDTO.getRestaurantName() != null) {
                restaurant.setRestaurantName(restaurantDTO.getRestaurantName());
            }
            if (restaurantDTO.getRestaurantAdd() != null) {
                restaurant.setRestaurantAdd(restaurantDTO.getRestaurantAdd());
            }
            restaurant.setHoursOfOperation(restaurantDTO.getHoursOfOperation());
            restaurant.setOpeningHours(restaurantDTO.getOpeningHours());
            restaurant.setDeliveryZones(restaurantDTO.getDeliveryZones());
            restaurant.setCuisineType(restaurantDTO.getCuisineType());
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
    public Menu updateMenu(int restaurantId, MenuDTO menuDTO) {
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
    public List<Restaurant> searchRestaurant(String restaurantName, String cuisineType, Boolean isVegOnly, String foodItemName) {
        return restaurantRepository.searchRestaurants(restaurantName, cuisineType, isVegOnly, foodItemName);
    }

    @Override
    public List<Order> viewOrders(int restaurantId) {
        List<Order> orders = orderRepository.findAll();
        return orders.stream().filter(order -> order.getRestaurantId() == restaurantId).toList();
    }

    @Override
    public Order updateOrderStatus(int restaurantId, int orderId, String status) {
        Order order = orderRepository.findByOrderId(orderId);
        if (order != null && order.getRestaurantId() == restaurantId) {
            order.setStatus(status);
            orderRepository.save(order);
        }
        return order;
    }

    @Override
    public void deactivateRestaurant(int userId) {
        Restaurant restaurant = restaurantRepository.findByRestaurantId(userId);
        if (restaurant != null) {
            restaurant.setActive(false);
            restaurantRepository.save(restaurant);
        }
    }

}
