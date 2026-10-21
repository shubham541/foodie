package com.bits.foodie.domain;

/** The restaurant owner is the boss of the whole system; everyone else is a customer. */
public enum Role {
    CUSTOMER, RESTAURANT;

    public String authority() {
        return "ROLE_" + name();
    }
}
