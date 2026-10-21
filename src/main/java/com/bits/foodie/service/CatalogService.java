package com.bits.foodie.service;

import com.bits.foodie.domain.MenuItem;
import com.bits.foodie.domain.Restaurant;
import com.bits.foodie.dto.CatalogDtos.*;
import com.bits.foodie.exception.ApiException;
import com.bits.foodie.repository.MenuItemRepository;
import com.bits.foodie.repository.RestaurantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Public browsing (restaurants, menu) plus the restaurant owner's menu and settings management. */
@Service
public class CatalogService {

    private final RestaurantRepository restaurants;
    private final MenuItemRepository menuItems;

    public CatalogService(RestaurantRepository restaurants, MenuItemRepository menuItems) {
        this.restaurants = restaurants;
        this.menuItems = menuItems;
    }

    @Transactional(readOnly = true)
    public List<RestaurantDTO> listActive() {
        return restaurants.findByActiveTrueOrderByIdAsc().stream().map(DtoMapper::restaurant).toList();
    }

    @Transactional(readOnly = true)
    public RestaurantDTO get(Long id) {
        return DtoMapper.restaurant(activeRestaurant(id));
    }

    /** Customer-facing menu: every item (unavailable ones are shown greyed out), grouped by category. */
    @Transactional(readOnly = true)
    public MenuDTO menu(Long restaurantId) {
        Restaurant r = activeRestaurant(restaurantId);
        return new MenuDTO(r.getId(), group(menuItems.findByRestaurantIdOrderBySortOrderAscIdAsc(r.getId())));
    }

    // ---- restaurant owner ----

    @Transactional(readOnly = true)
    public RestaurantDTO ownRestaurant(Long ownerId) {
        return DtoMapper.restaurant(owned(ownerId));
    }

    @Transactional(readOnly = true)
    public List<MenuItemDTO> ownMenu(Long ownerId) {
        Restaurant r = owned(ownerId);
        return menuItems.findByRestaurantIdOrderBySortOrderAscIdAsc(r.getId()).stream().map(DtoMapper::menuItem).toList();
    }

    @Transactional
    public MenuItemDTO updateMenuItem(Long ownerId, Long itemId, MenuItemUpdate update) {
        Restaurant r = owned(ownerId);
        MenuItem item = menuItems.findByIdAndRestaurantId(itemId, r.getId())
                .orElseThrow(() -> ApiException.notFound("Menu item not found."));
        if (update.price() == null && update.available() == null) {
            throw ApiException.badRequest("Nothing to update.");
        }
        if (update.price() != null) item.setPrice(update.price().setScale(2, java.math.RoundingMode.HALF_UP));
        if (update.available() != null) item.setAvailable(update.available());
        return DtoMapper.menuItem(item);
    }

    @Transactional
    public RestaurantDTO updateSettings(Long ownerId, SettingsRequest req) {
        Restaurant r = owned(ownerId);
        r.setAcceptingOrders(req.acceptingOrders());
        return DtoMapper.restaurant(r);
    }

    private Restaurant owned(Long ownerId) {
        return restaurants.findByOwnerId(ownerId).orElseThrow(() -> ApiException.notFound("No restaurant is linked to this account."));
    }

    private Restaurant activeRestaurant(Long id) {
        return restaurants.findByIdAndActiveTrue(id).orElseThrow(() -> ApiException.notFound("Restaurant not found."));
    }

    private static List<MenuCategoryDTO> group(List<MenuItem> items) {
        Map<String, List<MenuItemDTO>> byCategory = new LinkedHashMap<>();
        for (MenuItem i : items) {
            byCategory.computeIfAbsent(i.getCategory(), k -> new ArrayList<>()).add(DtoMapper.menuItem(i));
        }
        return byCategory.entrySet().stream().map(e -> new MenuCategoryDTO(e.getKey(), e.getValue())).toList();
    }
}
