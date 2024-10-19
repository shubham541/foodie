package com.bits.foodie.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Customer {
    private String customerId;
    private String customerName;
    private String customerEmail;
    private List<Address> addresses;
    private List<Card> savedPaymentDetails;
    private String role;
    private Cart cart;
    private boolean isActive;
    private List<Cart> history;

    public void fetchAllRestaurants() {

    }

    public void searchByFoodItems() {

    }

    public void filterByRestaurantType() {

    }
}
