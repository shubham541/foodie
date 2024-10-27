package com.bits.foodie.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "item") // Table name as per the entity name
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int itemId;

    private String itemName;
    private double itemPrice;
    private String description;
    private boolean isAvailable;
}
