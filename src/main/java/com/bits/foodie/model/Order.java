package com.bits.foodie.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
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

    private int restaurantId;
    private double amount;
    private boolean isCancelled;
    private String status;
    private double deliveryTimeInMinutes;

    //Extra attribute for joins
    @ManyToOne
    @JoinColumn(name = "customer_id")
    @JsonBackReference
    private Customer customer; //Foreign key column in Customer

}
