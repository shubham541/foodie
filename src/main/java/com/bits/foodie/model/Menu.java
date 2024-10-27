package com.bits.foodie.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "menu") // Table name as per the entity name
public class Menu {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int menuId;

    @OneToMany
    private List<Item> items; // Assuming each menu can have multiple items
}
