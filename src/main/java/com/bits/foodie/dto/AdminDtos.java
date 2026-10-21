package com.bits.foodie.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public final class AdminDtos {

    private AdminDtos() {
    }

    public record Summary(long totalOrders, long pendingOrders, long readyForPickup, long completedOrders,
                          long cancelledOrders, long activeUsers, BigDecimal revenue) {
    }

    public record TopItem(String name, long quantity) {
    }

    /** averagePickupMinutes: time from placing an order to the customer collecting it. */
    public record Report(Double averagePickupMinutes, BigDecimal averageOrderValue, List<TopItem> topItems) {
    }

    public record ActiveRequest(@NotNull(message = "active is required") Boolean active) {
    }
}
