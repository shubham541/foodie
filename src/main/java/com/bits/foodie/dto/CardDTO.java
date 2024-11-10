package com.bits.foodie.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.time.YearMonth;

@Getter
@Setter
public class CardDTO {

    private int cardId;

    @Pattern(regexp = "^[0-9]{16}$", message = "Card number must be 16 digits")
    private String cardNo;

    @NotNull(message = "Card expiry date is required")
    private YearMonth cardExpiry;

    @NotNull(message = "Card name is required")
    private String cardName;
}
