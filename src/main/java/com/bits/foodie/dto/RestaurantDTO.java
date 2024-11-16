package com.bits.foodie.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class RestaurantDTO {

    private int restaurantId;

    @NotNull(message = "Restaurant name is required")
    private String restaurantName;

    @NotNull(message = "Restaurant email is required")
    @Email(message = "Restaurant email must be valid")
    private String restaurantEmail;

    @NotNull(message = "Password is required")
    private String password;

    @NotNull(message = "Restaurant address is required")
    private String restaurantAdd;

    @NotNull(message = "Menu is required")
    private MenuDTO menu;

    @NotNull(message = "Role is required")
    private String role;

    private String cuisineType;

    private boolean isVegOnly;

    private double hoursOfOperation;

    @NotNull(message = "Opening hours are required")
    private LocalTime openingHours;

    @NotNull(message = "Closing hours are required")
    private LocalTime closingHours;

    @NotNull(message = "Delivery zones are required")
    private List<String> deliveryZones;

    private boolean isActive;

    private int orderCount;

    private double timeConsumedPerOrderInMinutes;

    public RestaurantDTO(String restaurantName, String restaurantEmail, String password,
                         String restaurantAdd, String role, String cuisineType, boolean isVegOnly,
                         LocalTime openingHours, LocalTime closingHours, List<String> deliveryZones) {
        this.password = password;
        this.restaurantName = restaurantName;
        this.restaurantEmail = restaurantEmail;
        this.restaurantAdd = restaurantAdd;
        this.role = role;
        this.cuisineType = cuisineType;
        this.isVegOnly = isVegOnly;
        this.openingHours = openingHours;
        this.closingHours = closingHours;
        this.deliveryZones = deliveryZones;
    }
}
