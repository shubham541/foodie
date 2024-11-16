package com.bits.foodie.repository;

import com.bits.foodie.model.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@EnableJpaRepositories
@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Integer> {
    Optional<Restaurant> findOneByRestaurantEmailAndPassword(String restaurantEmail, String password);

    Restaurant findByRestaurantEmail(String restEmail);

    Restaurant findByRestaurantId(int restaurantId);

    @Query("SELECT r FROM Restaurant r " +
            "LEFT JOIN r.menu m " + // Join the Menu entity
            "LEFT JOIN m.items i " + // Join the Item entity
            "WHERE (:restaurantName IS NULL OR r.restaurantName LIKE %:restaurantName%) " +
            "AND (:cuisineType IS NULL OR r.cuisineType = :cuisineType) " +
            "AND (:isVegOnly IS NULL OR r.isVegOnly = :isVegOnly) " +
            "AND (:foodItemName IS NULL OR i.itemName LIKE %:foodItemName%)") // Check food item name
    List<Restaurant> searchRestaurants(
            @Param("restaurantName") String restaurantName,
            @Param("cuisineType") String cuisineType,
            @Param("isVegOnly") Boolean isVegOnly,
            @Param("foodItemName") String foodItemName // Add food item parameter
    );

    int countByIsActive(boolean b);
}
