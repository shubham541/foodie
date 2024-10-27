package com.bits.foodie.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.YearMonth;

@Getter
@Setter
public class CardDTO {
    private int cardId;
    private String cardNo;
    private YearMonth cardExpiry;
    private String cardName;
}
