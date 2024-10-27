package com.bits.foodie.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemDTO {
    private int itemId;
    private String itemName;
    private double itemPrice;
    private String description;
    private boolean isAvailable;
}
