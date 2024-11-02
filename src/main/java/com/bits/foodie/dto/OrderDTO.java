package com.bits.foodie.dto;

import com.bits.foodie.model.Cart;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderDTO {
    private int OrderId;
    private CartDTO cart;
    private int customerId;
    private int restaurantId;
    private double amount;
    private boolean isCancelled;
    private String status;
    private double deliveryTimeInMinutes;

    public void rescheduleOrder() {

    }
}
