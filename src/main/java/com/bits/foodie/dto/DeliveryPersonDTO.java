package com.bits.foodie.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DeliveryPersonDTO {

    private int deliveryPersonId;
    private String deliveryPersonEmail;
    private String deliveryPersonName;
    private String password;
    private String contactDetails;
    private String vehicleType;
    private String devAvailable;
    private String role;
    private boolean isActive;


    public DeliveryPersonDTO(String deliveryPersonEmail, String deliveryPersonName, String password, String contactDetails, String vehicleType, String role) {
        this.deliveryPersonEmail = deliveryPersonEmail;
        this.deliveryPersonName = deliveryPersonName;
        this.password = password;
        this.contactDetails = contactDetails;
        this.vehicleType = vehicleType;
        this.role=role;
    }
}
