package com.bits.foodie.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AdminDTO {

    private int adminId;

    @NotNull(message = "Admin name is required")
    private String adminName;

    @NotNull(message = "Admin email is required")
    @Email(message = "Admin email must be valid")
    private String adminEmail;

    @NotNull(message = "Role is required")
    private String role;

    @NotNull(message = "Password is required")
    @Size(min = 6, max = 20, message = "Password must be between 6 and 20 characters")
    private String password;

    public AdminDTO(String adminEmail, String adminName, String password, String role) {
        this.password = password;
        this.adminName = adminName;
        this.adminEmail = adminEmail;
        this.role = role;
    }
}
