package com.bits.foodie.dto;

import com.bits.foodie.model.Address;
import com.bits.foodie.model.Card;
import com.bits.foodie.model.Cart;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CustomerDTO {

    private String customerName;
    private String customerEmail;
    private String password;
    private List<Address> addresses;
    private List<Card> savedPaymentDetails;
    private String role;
    private Cart cart;
    private boolean isActive;
    private List<Cart> history;

    public CustomerDTO(String customerEmail, String customerName, String password,String role) {
        this.customerEmail = customerEmail;
        this.customerName = customerName;
        this.password = password;
        this.role=role;
    }
}
