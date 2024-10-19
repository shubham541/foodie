package com.bits.foodie.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Item {
    private String itemId;
    private String itemName;
    private double itemPrice;
    private String description;
    private boolean isAvailable;
}
