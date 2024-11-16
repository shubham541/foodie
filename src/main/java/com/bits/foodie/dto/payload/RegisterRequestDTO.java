package com.bits.foodie.dto.payload;

import com.bits.foodie.dto.CartDTO;
import com.bits.foodie.model.Address;
import com.bits.foodie.model.Card;
import com.bits.foodie.model.Menu;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
public class RegisterRequestDTO {

    private int registerId;

    // Common attributes
    @NotNull(message = "Name is required")
    private String name;

    @Email(message = "Email must be valid")
    private String email;

    @NotNull(message = "Role is required")
    private String role;

    private boolean isActive;

    // Customer Attributes
    private int customerId;

    @NotNull(message = "Customer name is required")
    private String customerName;

    @Email(message = "Email must be valid")
    private String customerEmail;

    @NotNull(message = "Password is required")
    @Size(min = 6, max = 20, message = "Password must be between 6 and 20 characters")
    private String password;

    private List<Address> addresses;
    private List<Card> savedPaymentDetails;
    private CartDTO cart;
    private List<CartDTO> history;

    // Delivery Person Attributes
    @NotNull(message = "Delivery person ID is required")
    private String deliveryPersonId;

    @Email(message = "Email must be valid")
    private String deliveryPersonEmail;

    @NotNull(message = "Delivery person name is required")
    private String deliveryPersonName;

    private String contactDetails;
    private String vehicleType;
    private String devAvailable;

    // Restaurant Attributes
    @NotNull(message = "Restaurant ID is required")
    private String restaurantId;

    @NotNull(message = "Restaurant name is required")
    private String restaurantName;

    @Email(message = "Email must be valid")
    private String restaurantEmail;

    private String restaurantAdd;
    private Menu menu;

    private String cuisineType;

    private boolean isVegOnly;
    private double hoursOfOperation;
    private LocalTime openingHours;
    private LocalTime closingHours;

    private List<String> deliveryZones;

    private int orderCount;
    private double timeConsumedPerOrderInMinutes;

    // Admin attributes
    @NotNull(message = "Admin ID is required")
    private String adminId;

    @NotNull(message = "Admin name is required")
    private String adminName;

    @Email(message = "Email must be valid")
    private String adminEmail;
}
