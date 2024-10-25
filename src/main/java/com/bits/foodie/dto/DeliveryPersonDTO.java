package com.bits.foodie.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeliveryPersonDTO {
    private String devId;
    private String devEmail;
    private String devName;
    private String password;
    private String contactDetails;
    private String vehicleType;
    private String devAvailable;
    private String role;
    private boolean isActive;


    public DeliveryPersonDTO(String devEmail, String devName, String password, String contactDetails, String vehicleType, String role) {
        this.devEmail = devEmail;
        this.devName = devName;
        this.password = password;
        this.contactDetails = contactDetails;
        this.vehicleType = vehicleType;
        this.role=role;
    }
}
