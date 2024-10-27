package com.bits.foodie.dto;

import com.bits.foodie.model.CartItem;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CartDTO {
    private int cartId;
    private String restId;
    private double price;
    private List<CartItem> items;
}

