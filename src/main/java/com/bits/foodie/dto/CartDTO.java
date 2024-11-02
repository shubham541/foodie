package com.bits.foodie.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CartDTO {
    private int cartId;
    private int restId;
    private double price;
    private List<CartItemDTO> cartItems;

    public void updatePrice() {
        if (cartItems != null) {
            this.price = cartItems.stream().mapToDouble(i -> i.getQuantity() * i.getItem().getItemPrice()).sum();
        }
    }

}

