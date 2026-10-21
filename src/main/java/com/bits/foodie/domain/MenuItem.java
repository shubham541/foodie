package com.bits.foodie.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "menu_items",
        uniqueConstraints = @UniqueConstraint(name = "uk_menu_restaurant_name", columnNames = {"restaurant_id", "name"}))
public class MenuItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(length = 300)
    private String description;

    @Column(nullable = false, length = 60)
    private String category;

    /** Price excluding GST. */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    /** true = vegetarian, false = contains egg/non-veg, null = not specified. */
    private Boolean veg;

    @Column(length = 250)
    private String imageUrl;

    @Column(nullable = false)
    private boolean available = true;

    @Column(nullable = false)
    private int sortOrder;

    /** Curation tags set from the seed file (the owner does not edit them). */
    @Column(nullable = false)
    private boolean spicy;

    @Column(nullable = false)
    private boolean bestseller;

    @Column(nullable = false)
    private boolean healthy;
}
