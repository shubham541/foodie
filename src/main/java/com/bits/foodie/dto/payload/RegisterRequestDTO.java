package com.bits.foodie.dto.payload;

import com.bits.foodie.dto.CartDTO;
import com.bits.foodie.model.Address;
import com.bits.foodie.model.Card;
import com.bits.foodie.model.Menu;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
public class RegisterRequestDTO {

    private int registerId;

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
    private CartDTO cart;
    private List<CartDTO> history;

    //Delivery Person Attributes
    private String deliveryPersonId;
    private String deliveryPersonEmail;
    private String deliveryPersonName;
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
