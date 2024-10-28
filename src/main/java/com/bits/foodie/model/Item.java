package com.bits.foodie.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int itemId;

    private String itemName;
    private double itemPrice;
    private String description;
    private boolean isAvailable;

    //Extra attribute for joins
    @ManyToOne
    @JoinColumn
    @JsonBackReference
    private Menu menu; //Foreign key column in item

}
