package com.bits.foodie.repository;

import com.bits.foodie.domain.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

    List<MenuItem> findByRestaurantIdOrderBySortOrderAscIdAsc(Long restaurantId);

    List<MenuItem> findByRestaurantIdAndIdIn(Long restaurantId, Collection<Long> ids);

    Optional<MenuItem> findByIdAndRestaurantId(Long id, Long restaurantId);

    Optional<MenuItem> findByRestaurantIdAndName(Long restaurantId, String name);
}
