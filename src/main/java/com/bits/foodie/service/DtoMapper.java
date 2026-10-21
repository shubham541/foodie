package com.bits.foodie.service;

import com.bits.foodie.domain.*;
import com.bits.foodie.dto.AuthDtos.UserDTO;
import com.bits.foodie.dto.CatalogDtos.MenuItemDTO;
import com.bits.foodie.dto.CatalogDtos.RestaurantDTO;
import com.bits.foodie.dto.OrderDtos.OrderDTO;
import com.bits.foodie.dto.OrderDtos.OrderItemDTO;

/** Entity to DTO conversion. Call inside a transaction so lazy associations can be read. */
public final class DtoMapper {

    private DtoMapper() {
    }

    public static UserDTO user(User u) {
        return new UserDTO(u.getId(), u.getName(), u.getEmail(), u.getPhone(), u.getRole(), u.isActive());
    }

    public static RestaurantDTO restaurant(Restaurant r) {
        return new RestaurantDTO(r.getId(), r.getName(), r.getDescription(), r.getCuisine(), r.getAddress(),
                r.getLogoUrl(), r.getHeroUrl(), r.isAcceptingOrders(), r.getTaxRatePercent());
    }

    public static MenuItemDTO menuItem(MenuItem i) {
        return new MenuItemDTO(i.getId(), i.getName(), i.getDescription(), i.getCategory(), i.getPrice(),
                i.getVeg(), i.getImageUrl(), i.isAvailable());
    }

    public static String orderNumber(Long orderId) {
        return "SK" + (1000 + orderId);
    }

    public static OrderDTO order(Order o) {
        return new OrderDTO(o.getId(), orderNumber(o.getId()), o.getStatus(), o.getRestaurant().getId(),
                o.getRestaurant().getName(), o.getCustomer().getId(), o.getCustomer().getName(), o.getContactPhone(),
                o.getItems().stream().map(i -> new OrderItemDTO(i.getMenuItemId(), i.getName(), i.getUnitPrice(),
                        i.getQuantity(), i.getLineTotal())).toList(),
                o.getSubtotal(), o.getTaxAmount(), o.getTotal(), o.getPaymentMethod(), o.getNotes(),
                o.getCancelReason(), o.getCreatedAt(), o.getUpdatedAt(), o.getCollectedAt());
    }
}
