package com.bits.foodie.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressDTO {

    private int addressId;

    @NotEmpty(message = "Flat number is required")
    private String flatNo;

    @NotEmpty(message = "Lane is required")
    private String lane;

    private String landmark;

    @NotEmpty(message = "City is required")
    private String city;

    @NotEmpty(message = "State is required")
    private String state;

    @Pattern(regexp = "^[1-9][0-9]{5}$", message = "Pin code must be a valid 6-digit number")
    private String pinCode;
}
