package com.bits.foodie.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class Cart {
    private String cartId;
    private String restId;
    private double price;
    private Map<Item, Integer> items;
}
