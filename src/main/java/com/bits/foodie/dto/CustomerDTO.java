package com.bits.foodie.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CustomerDTO {

    private int customerId;
    private String customerName;
    private String customerEmail;
    private String password;
    private List<AddressDTO> addresses;
    private List<CardDTO> savedPaymentDetails;
    private String role;
    private CartDTO cart;
    private boolean isActive;
    private List<OrderDTO> history;

    public CustomerDTO(String customerEmail, String customerName, String password,String role) {
        this.customerEmail = customerEmail;
        this.customerName = customerName;
        this.password = password;
        this.role=role;
    }

}
