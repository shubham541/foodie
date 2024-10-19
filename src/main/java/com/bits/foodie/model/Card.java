package com.bits.foodie.model;

import lombok.Getter;
import lombok.Setter;

import java.time.YearMonth;

@Getter
@Setter
public class Card {
    private String cardId;
    private String cardNo;
    private YearMonth cardExpiry;
    private String cardName;
}
