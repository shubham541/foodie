package com.bits.foodie.repository;

import com.bits.foodie.domain.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    List<Restaurant> findByActiveTrueOrderByIdAsc();

    Optional<Restaurant> findByIdAndActiveTrue(Long id);

    Optional<Restaurant> findByOwnerId(Long ownerId);
}
