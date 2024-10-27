package com.bits.foodie.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressDTO {
    private int addressId;
    private String flatNo;
    private String lane;
    private String landmark;
    private String city;
    private String state;
    private String pinCode;
}
