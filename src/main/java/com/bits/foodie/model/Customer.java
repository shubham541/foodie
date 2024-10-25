package com.bits.foodie.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "customer")
public class Customer {

    @Id
    @Column(name = "customer_id", length = 45)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int customerId;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "customer_email")
    private String customerEmail;

    @Column(name = "password")
    private String password;

    //private List<Address> addresses;
    //private List<Card> savedPaymentDetails;
    private String role;
    //private Cart cart;
    private boolean isActive;
    // private List<Cart> history;

    public Customer(String role, String customerName, String customerEmail, String password) {
        this.role = role;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.password = password;
    }

    public Customer() {
    }

    public void fetchAllRestaurants() {

    }

    public void searchByFoodItems() {

    }

    public void filterByRestaurantType() {

    }
}
