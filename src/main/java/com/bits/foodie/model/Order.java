package com.bits.foodie.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int orderId;

    @OneToOne
    private Cart cart;

    private int customerId;
    private int restaurantId;
    private double amount;
    private boolean isCancelled;
    private String status;
    private double deliveryTimeInMinutes;

    public void rescheduleOrder() {
        // Logic to reschedule the order
    }
}
