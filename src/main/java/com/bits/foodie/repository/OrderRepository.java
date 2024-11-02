package com.bits.foodie.repository;

import com.bits.foodie.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

@EnableJpaRepositories
@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    Order findByOrderId(int orderId);
    Order findByRestaurantId(int restaurantId);

}
