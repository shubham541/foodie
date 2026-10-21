package com.bits.foodie.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public final class CustomerDtos {

    private CustomerDtos() {
    }

    public record ProfileUpdate(
            @Size(max = 100, message = "Name is too long") String name,
            @Pattern(regexp = AuthDtos.PHONE_REGEX, message = AuthDtos.PHONE_MESSAGE) String phone) {
    }
}
