package com.bits.foodie.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AdminDTO {
    private int adminId;
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
