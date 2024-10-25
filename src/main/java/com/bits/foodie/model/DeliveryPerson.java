package com.bits.foodie.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeliveryPerson {
    private String devId;
    private String devEmail;
    private String devName;
    private String contactDetails;
    private String vehicleType;
    private String devAvailable;
    private String role;
    private boolean isActive;

    public void viewListOfAvailableDeliveries() {

    }

    public void updateOrderStatus() {

    }
}
