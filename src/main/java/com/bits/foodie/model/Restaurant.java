package com.bits.foodie.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table
public class Restaurant {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int restaurantId;
    private String restaurantName;
    private String restaurantEmail;
    private String restaurantAdd;
    private String password;
    //private Menu menu;
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

    public Restaurant(String restaurantName, String restaurantEmail,String password, String restaurantAdd, String role, String cuisineType, boolean isVegOnly, LocalTime openingHours, LocalTime closingHours, List<String> deliveryZones) {
        this.password = password;
        this.restaurantName = restaurantName;
        this.restaurantEmail = restaurantEmail;
        this.restaurantAdd = restaurantAdd;
        this.role = role;
        CuisineType = cuisineType;
        this.isVegOnly = isVegOnly;
        this.openingHours = openingHours;
        this.closingHours = closingHours;
        this.deliveryZones = deliveryZones;
    }

    public void updateOrderStatus() {

    }

    public void viewUpcomingOrders() {

    }

}
