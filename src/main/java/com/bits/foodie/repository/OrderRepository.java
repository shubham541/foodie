package com.bits.foodie.repository;

import com.bits.foodie.model.Order;
import com.bits.foodie.model.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

@EnableJpaRepositories
@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    Restaurant findByOrderId(int orderId);
    Restaurant findByCustomerId(int customerId);
    Restaurant findByRestaurantId(int restaurantId);

}
