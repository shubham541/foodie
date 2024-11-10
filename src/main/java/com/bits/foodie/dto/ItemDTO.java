package com.bits.foodie.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemDTO {

    private int itemId;

    @NotNull(message = "Item name is required")
    private String itemName;

    @Min(value = 0, message = "Item price must be greater than or equal to 0")
    private double itemPrice;

    private String description;

    private boolean isAvailable;
}
