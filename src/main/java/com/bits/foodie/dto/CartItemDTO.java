package com.bits.foodie.dto;

import com.bits.foodie.model.Item;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartItemDTO {
    private int cartItemId;
    private ItemDTO item;
    private int quantity;
}
