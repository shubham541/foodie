package com.bits.foodie.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "item")
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
    @JoinColumn(name = "menu_id") // Specify the foreign key column
    @JsonBackReference // Prevents infinite recursion during serialization
    private Menu menu; // Foreign key column in Item

}
