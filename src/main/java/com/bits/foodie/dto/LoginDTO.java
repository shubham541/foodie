package com.bits.foodie.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginDTO {
    private String role;
    private String customerEmail;
    private String adminEmail;
    private String RestaurantEmail;
    private String deliveryPersonEmail;
    private String password;

    public LoginDTO() {
    }
}
