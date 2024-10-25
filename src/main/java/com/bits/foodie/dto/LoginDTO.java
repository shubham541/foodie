package com.bits.foodie.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginDTO {
    private String role;
    private String customerEmail;
    private String password;

    public LoginDTO(String role, String customerEmail, String password) {
        this.role=role;
        this.customerEmail = customerEmail;
        this.password = password;
    }

    public LoginDTO() {
    }
}
