package com.bits.foodie.dto;

import com.bits.foodie.model.Cart;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderDTO {

    private int OrderId;

    @NotNull(message = "Cart details are required")
    private CartDTO cart;

    @NotNull(message = "Customer ID is required")
    private int customerId;

    @NotNull(message = "Restaurant ID is required")
    private int restaurantId;

    private double amount;

    private boolean isCancelled = false;

    @NotNull(message = "Order status is required")
    private String status;

    private double deliveryTimeInMinutes;

    public void rescheduleOrder() {

    }
}
