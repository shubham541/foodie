package com.bits.foodie.dto;

import com.bits.foodie.model.Item;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItemDTO {

    private int cartItemId;

    @NotNull(message = "Item is required")
    private ItemDTO item;

    @NotNull(message = "Quantity is required")
    private int quantity;
}
