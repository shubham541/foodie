package com.bits.foodie.dto.payload;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDTO {

    private int loginId;

    @NotNull(message = "Role is required")
    private String role;

    @Email(message = "Invalid email format")
    private String customerEmail;

    @Email(message = "Invalid email format")
    private String adminEmail;

    @Email(message = "Invalid email format")
    private String restaurantEmail;

    @Email(message = "Invalid email format")
    private String deliveryPersonEmail;

    @NotNull(message = "Password is required")
    @Size(min = 6, max = 20, message = "Password must be between 6 and 20 characters")
    private String password;

    public LoginRequestDTO() {
    }

    public LoginRequestDTO(String mail, String password) {
    }
}
