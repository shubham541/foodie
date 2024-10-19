package com.bits.foodie.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
public class Restaurant {
    private String restaurantId;
    private String restaurantName;
    private String restaurantAdd;
    private Menu menu;
    private String role;
    private String CuisineType;
    private boolean isVegOnly;
    private double hoursOfOperation;
    private LocalTime openingHours;
    private LocalTime closingHours;
    private List<String> deliveryZones;
    private boolean isActive;
    private int orderCount;
    private double timeConsumedPerOrderInMinutes;

    public void updateOrderStatus() {

    }

    public void viewUpcomingOrders() {

    }

}
