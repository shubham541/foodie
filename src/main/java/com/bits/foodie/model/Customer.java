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
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int customerId;
    private String customerName;

    @Column(unique = true)
    private String customerEmail;
    private String password;
    private String role;
    private boolean isActive;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Address> addresses;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Card> savedPaymentDetails;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Cart> history;

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
