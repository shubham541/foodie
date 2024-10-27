package com.bits.foodie.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "cart_item")
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int cartItemId;

    @OneToOne(cascade = CascadeType.ALL)
    private Item item;

    private int quantity;

    @ManyToOne
    @JoinColumn(name = "cart_id") // Foreign key column in CartItem
    private Cart cart;
}
