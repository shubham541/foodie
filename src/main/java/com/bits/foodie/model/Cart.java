package com.bits.foodie.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int cartId;
    private String restId;
    private double price;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CartItem> items;

    //Extra attribute for joins
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer; //Foreign key column in Card
}

