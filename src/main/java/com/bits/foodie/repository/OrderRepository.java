package com.bits.foodie.repository;

import com.bits.foodie.domain.Order;
import com.bits.foodie.domain.OrderStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Override
    @EntityGraph(attributePaths = {"items", "customer", "restaurant"})
    Optional<Order> findById(Long id);

    @EntityGraph(attributePaths = {"items", "customer", "restaurant"})
    List<Order> findByCustomerIdOrderByCreatedAtDesc(Long customerId);

    @EntityGraph(attributePaths = {"items", "customer", "restaurant"})
    List<Order> findByRestaurantIdOrderByCreatedAtDesc(Long restaurantId);

    long countByStatus(OrderStatus status);

    long countByStatusIn(Collection<OrderStatus> statuses);

    @Query("select coalesce(sum(o.total), 0) from Order o where o.status = com.bits.foodie.domain.OrderStatus.COLLECTED")
    BigDecimal collectedRevenue();

    @Query("select avg(o.total) from Order o where o.status = com.bits.foodie.domain.OrderStatus.COLLECTED")
    Double averageCollectedValue();

    /** [createdAt, collectedAt] for collected orders. */
    @Query("select o.createdAt, o.collectedAt from Order o "
            + "where o.status = com.bits.foodie.domain.OrderStatus.COLLECTED and o.collectedAt is not null")
    List<Object[]> pickupTimes();

    /** [dish name, units sold] for non-cancelled orders, best sellers first. */
    @Query("select i.name, sum(i.quantity) from OrderItem i where i.order.status <> com.bits.foodie.domain.OrderStatus.CANCELLED "
            + "group by i.name order by sum(i.quantity) desc")
    List<Object[]> topItems(Pageable pageable);
}
