package com.bits.foodie.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public final class CatalogDtos {

    private CatalogDtos() {
    }

    public record RestaurantDTO(Long id, String name, String description, String cuisine, String address,
                                String logoUrl, String heroUrl, boolean acceptingOrders,
                                BigDecimal taxRatePercent) {
    }

    public record MenuItemDTO(Long id, String name, String description, String category, BigDecimal price,
                              Boolean veg, String imageUrl, boolean available,
                              boolean spicy, boolean bestseller, boolean healthy) {
    }

    public record MenuCategoryDTO(String name, List<MenuItemDTO> items) {
    }

    public record MenuDTO(Long restaurantId, List<MenuCategoryDTO> categories) {
    }

    /** Partial update: only the supplied fields change. */
    public record MenuItemUpdate(
            @DecimalMin(value = "1.00", message = "Price must be at least 1")
            @DecimalMax(value = "100000.00", message = "Price is too high")
            @Digits(integer = 6, fraction = 2, message = "Price can have at most 2 decimals") BigDecimal price,
            Boolean available) {
    }

    public record SettingsRequest(@NotNull(message = "acceptingOrders is required") Boolean acceptingOrders) {
    }
}
