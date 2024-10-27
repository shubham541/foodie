package com.bits.foodie.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.YearMonth;

@Getter
@Setter
@Entity
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int cardId;
    private String cardNo;

    @Column(name = "card_expiry")
    private YearMonth cardExpiry;
    private String cardName;

    //Extra attribute for joins
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer; //Foreign key column in Card
}
