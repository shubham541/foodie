package com.bits.foodie.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Address {
    private String addressId;
    private String flatNo;
    private String lane;
    private String landmark;
    private String city;
    private String state;
    private String pinCode;
}
