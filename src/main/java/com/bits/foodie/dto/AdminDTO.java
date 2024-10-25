package com.bits.foodie.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminDTO {
    private String adminName;
    private String adminEmail;
    private String role;
    private String password;

    public AdminDTO(String adminEmail, String adminName, String password, String role) {
        this.password = password;
        this.adminName = adminName;
        this.adminEmail = adminEmail;
        this.role = role;
    }
}
