package com.bits.foodie.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Order {
    private String OrderId;
    private Cart cart;
    private String customerAdd;
    private String restaurantAdd;
    private double amount;
    private boolean isCancelled;
    private String status;
    private double deliveryTimeInMinutes;

    public void rescheduleOrder() {

    }
}
