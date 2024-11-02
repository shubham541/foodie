package com.bits.foodie.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
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

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JsonManagedReference // Prevents infinite recursion
    private List<Address> addresses = new ArrayList<>();

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JsonManagedReference
    private List<Card> savedPaymentDetails = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "cart_id")
    @JsonManagedReference
    private Cart cart;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JsonManagedReference
    private List<Order> history=new ArrayList<>();

    public Customer(String role, String customerName, String customerEmail, String password) {
        this.role = role;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.password = password;
    }

    public void addHistory(Order order) {
        this.history.add(order);
    }

    public Customer() {
    }

    public void setAddresses(List<Address> addresses) {
        // Clear current addresses
        this.addresses.forEach(address -> address.setCustomer(null)); // Remove back-reference
        this.addresses.clear();

        if (addresses != null) {
            for (Address address : addresses) {
                address.setCustomer(this);  // Set back-reference
                this.addresses.add(address); // Add new addresses
            }
        }
    }

    public void setSavedPaymentDetails(List<Card> savedPaymentDetails) {
        // Clear current savedPaymentDetails
        this.savedPaymentDetails.forEach(card -> card.setCustomer(null)); // Remove back-reference
        this.savedPaymentDetails.clear();

        if (savedPaymentDetails != null) {
            for (Card card : savedPaymentDetails) {
                card.setCustomer(this);  // Set back-reference
                this.savedPaymentDetails.add(card); // Add new saved payment details
            }
        }
    }

    public void fetchAllRestaurants() {

    }

    public void searchByFoodItems() {

    }

    public void filterByRestaurantType() {

    }
}
