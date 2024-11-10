package com.bits.foodie.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CustomerDTO {

    private int customerId;

    @NotNull(message = "Customer name is required")
    private String customerName;

    @NotNull(message = "Customer email is required")
    @Email(message = "Customer email must be valid")
    private String customerEmail;

    @NotNull(message = "Password is required")
    private String password;

    private List<AddressDTO> addresses;

    private List<CardDTO> savedPaymentDetails;

    @NotNull(message = "Role is required")
    private String role;

    private CartDTO cart;

    private boolean isActive = true;

    private List<OrderDTO> history;

    public CustomerDTO(String customerEmail, String customerName, String password, String role) {
        this.customerEmail = customerEmail;
        this.customerName = customerName;
        this.password = password;
        this.role = role;
    }
}
