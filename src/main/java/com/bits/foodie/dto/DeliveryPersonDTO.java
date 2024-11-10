package com.bits.foodie.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DeliveryPersonDTO {

    private int deliveryPersonId;

    @NotNull(message = "Delivery person email is required")
    @Email(message = "Delivery person email must be valid")
    private String deliveryPersonEmail;

    @NotNull(message = "Delivery person name is required")
    private String deliveryPersonName;

    @NotNull(message = "Password is required")
    private String password;

    @NotNull(message = "Contact details are required")
    private String contactDetails;

    @NotNull(message = "Vehicle type is required")
    private String vehicleType;

    private boolean devAvailable;

    @NotNull(message = "Role is required")
    private String role;

    private boolean isActive = true;

    public DeliveryPersonDTO(String deliveryPersonEmail, String deliveryPersonName, String password,
                             String contactDetails, String vehicleType, String role) {
        this.deliveryPersonEmail = deliveryPersonEmail;
        this.deliveryPersonName = deliveryPersonName;
        this.password = password;
        this.contactDetails = contactDetails;
        this.vehicleType = vehicleType;
        this.role = role;
    }
}
