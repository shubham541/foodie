package com.bits.foodie.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int addressId;

    private String flatNo;
    private String lane;
    private String landmark;
    private String city;
    private String state;
    private String pinCode;

    //Extra attribute for joins
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer; //Foreign key column in Card
}
