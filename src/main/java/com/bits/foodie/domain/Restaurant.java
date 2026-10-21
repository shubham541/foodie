package com.bits.foodie.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "restaurants")
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(length = 100)
    private String cuisine;

    @Column(length = 250)
    private String address;

    @Column(length = 250)
    private String logoUrl;

    @Column(length = 250)
    private String heroUrl;

    /** Switch used by the owner to pause incoming orders. */
    @Column(nullable = false)
    private boolean acceptingOrders = true;

    /** Soft-delete flag; inactive restaurants are hidden from customers. */
    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal taxRatePercent = new BigDecimal("5.00");

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", unique = true)
    private User owner;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();
}
