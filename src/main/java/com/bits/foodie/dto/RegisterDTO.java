package com.bits.foodie.dto;

import com.bits.foodie.model.Address;
import com.bits.foodie.model.Card;
import com.bits.foodie.model.Menu;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
public class RegisterDTO {

    //Common attributes
    private String name;
    private String email;
    private String role;
    private boolean isActive;

    //Customer Attributes
    private int customerId;
    private String customerName;
    private String customerEmail;
    private String password;
    private List<Address> addresses;
    private List<Card> savedPaymentDetails;
    //Content type issue with REST API with Cart class - Need to check
//   private Cart cart;
//   private List<Cart> history;

    //Delivery Person Attributes
    private String devId;
    private String devEmail;
    private String devName;
    private String contactDetails;
    private String vehicleType;
    private String devAvailable;

    //Restaurant Attributes
    private String restaurantId;
    private String restaurantName;
    private String restaurantEmail;
    private String restaurantAdd;
    private Menu menu;
    private String CuisineType;
    private boolean isVegOnly;
    private double hoursOfOperation;
    private LocalTime openingHours;
    private LocalTime closingHours;
    private List<String> deliveryZones;
    private int orderCount;
    private double timeConsumedPerOrderInMinutes;

    //Admin attributes
    private String adminId;
    private String adminName;
    private String adminEmail;
}
