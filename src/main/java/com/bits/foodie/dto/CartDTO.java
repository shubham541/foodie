package com.bits.foodie.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CartDTO {

    private int cartId;

    @NotNull(message = "Restaurant ID is required")
    private int restId;

    private double price;

    @Size(min = 1, message = "At least one item must be in the cart")
    private List<CartItemDTO> cartItems;

    public void updatePrice() {
        if (cartItems != null) {
            this.price = cartItems.stream().mapToDouble(i -> i.getQuantity() * i.getItem().getItemPrice()).sum();
        }
    }

}

