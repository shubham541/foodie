package com.bits.foodie.service.impl;

import com.bits.foodie.dto.CustomerDTO;
import com.bits.foodie.dto.LoginDTO;
import com.bits.foodie.dto.RestaurantDTO;
import com.bits.foodie.model.Customer;
import com.bits.foodie.model.Restaurant;
import com.bits.foodie.repository.CustomerRepository;
import com.bits.foodie.repository.RestaurantRepository;
import com.bits.foodie.response.LoginResponse;
import com.bits.foodie.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RestaurantServiceImpl implements RestaurantService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;


    @Autowired
    private PasswordEncoder passwordEncoder;


    @Override
    public String addRestaurant(RestaurantDTO restaurantDTO) {
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
        return restaurant.getRestaurantName();
    }

    @Override
    public LoginResponse loginRestaurant(LoginDTO loginDTO) {
        String msg = "";
        Restaurant restaurant = restaurantRepository.findByRestaurantEmail(loginDTO.getRestaurantEmail());
        if (restaurant != null && loginDTO.getRole().equalsIgnoreCase(restaurant.getRole())) {
            String password = loginDTO.getPassword();
            String encodedPassword = restaurant.getPassword();
            boolean isPwdRight = passwordEncoder.matches(password, encodedPassword);
            if (isPwdRight) {
                Optional<Restaurant> restaurant1 = restaurantRepository.findOneByRestaurantEmailAndPassword(restaurant.getRestaurantEmail(), restaurant.getPassword());
                if (restaurant1.isPresent()) {
                    return new LoginResponse("Login Success", true);
                } else {
                    return new LoginResponse("Login failed", false);
                }
            } else {
                return new LoginResponse("Password Not Match", false);
            }
        } else {
            return new LoginResponse("Email not exists", false);
        }
    }
}
