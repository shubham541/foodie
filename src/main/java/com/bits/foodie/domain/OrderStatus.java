package com.bits.foodie.domain;

/** Pickup-only lifecycle: the customer collects the order from the restaurant. */
public enum OrderStatus {
    PLACED, ACCEPTED, PREPARING, READY_FOR_PICKUP, COLLECTED, CANCELLED;

    public boolean isTerminal() {
        return this == COLLECTED || this == CANCELLED;
    }

    /** The next step of the happy path, or null when there is none. */
    public OrderStatus next() {
        return switch (this) {
            case PLACED -> ACCEPTED;
            case ACCEPTED -> PREPARING;
            case PREPARING -> READY_FOR_PICKUP;
            case READY_FOR_PICKUP -> COLLECTED;
            case COLLECTED, CANCELLED -> null;
        };
    }
}
