package com.bits.foodie.dto;

import com.bits.foodie.domain.Role;
import jakarta.validation.constraints.*;

public final class AuthDtos {

    private AuthDtos() {
    }

    public static final String PASSWORD_REGEX = "^(?=.*[A-Za-z])(?=.*\\d).{8,72}$";
    public static final String PASSWORD_MESSAGE = "Password must be 8-72 characters with at least one letter and one number";
    public static final String PHONE_REGEX = "^[6-9]\\d{9}$";
    public static final String PHONE_MESSAGE = "Enter a valid 10-digit mobile number";

    /** Self-registration always creates a customer account. */
    public record RegisterRequest(
            @NotBlank(message = "Name is required") @Size(max = 100, message = "Name is too long") String name,
            @NotBlank(message = "Email is required") @Email(message = "Enter a valid email address")
            @Size(max = 160, message = "Email is too long") String email,
            @NotBlank(message = "Mobile number is required") @Pattern(regexp = PHONE_REGEX, message = PHONE_MESSAGE) String phone,
            @NotBlank(message = "Password is required") @Pattern(regexp = PASSWORD_REGEX, message = PASSWORD_MESSAGE) String password) {
    }

    public record LoginRequest(
            @NotBlank(message = "Email is required") @Email(message = "Enter a valid email address") String email,
            @NotBlank(message = "Password is required") String password) {
    }

    public record UserDTO(Long id, String name, String email, String phone, Role role, boolean active) {
    }

    public record AuthResponse(String token, String tokenType, long expiresInSeconds, UserDTO user) {
    }
}
