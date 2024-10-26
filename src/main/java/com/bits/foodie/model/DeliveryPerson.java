package com.bits.foodie.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table
@NoArgsConstructor
public class DeliveryPerson {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int deliveryPersonId;
    private String deliveryPersonEmail;
    private String deliveryPersonName;
    private String contactDetails;
    private String vehicleType;
    private String devAvailable;
    private String role;
    private String password;
    private boolean isActive;

    public DeliveryPerson(String deliveryPersonEmail, String deliveryPersonName, String password, String contactDetails, String vehicleType, String role) {
        this.deliveryPersonEmail = deliveryPersonEmail;
        this.deliveryPersonName = deliveryPersonName;
        this.password = password;
        this.contactDetails = contactDetails;
        this.vehicleType = vehicleType;
        this.role=role;
    }

    public void viewListOfAvailableDeliveries() {

    }

    public void updateOrderStatus() {

    }
}
