package com.bits.foodie.repository;

import com.bits.foodie.model.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@EnableJpaRepositories
@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Integer> {
    Optional<Restaurant> findOneByRestaurantEmailAndPassword(String restaurantEmail, String password);

    Restaurant findByRestaurantEmail(String adminEmail);

}
