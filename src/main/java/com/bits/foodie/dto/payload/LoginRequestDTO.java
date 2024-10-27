package com.bits.foodie.dto.payload;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDTO {
    private int loginId;
    private String role;
    private String customerEmail;
    private String adminEmail;
    private String RestaurantEmail;
    private String deliveryPersonEmail;
    private String password;

    public LoginRequestDTO() {
    }
}
