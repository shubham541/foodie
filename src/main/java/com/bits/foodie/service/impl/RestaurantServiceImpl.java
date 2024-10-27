package com.bits.foodie.service.impl;

import com.bits.foodie.dto.payload.LoginRequestDTO;
import com.bits.foodie.dto.RestaurantDTO;
import com.bits.foodie.model.Restaurant;
import com.bits.foodie.repository.CustomerRepository;
import com.bits.foodie.repository.RestaurantRepository;
import com.bits.foodie.dto.response.ResponseDTO;
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
}
