package com.bits.foodie.dto;

import com.bits.foodie.domain.OrderStatus;
import com.bits.foodie.domain.PaymentMethod;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public final class OrderDtos {

    private OrderDtos() {
    }

    public record OrderItemRequest(
            @NotNull(message = "Item is required") Long menuItemId,
            @Min(value = 1, message = "Quantity must be at least 1") @Max(value = 20, message = "Quantity can be at most 20") int quantity) {
    }

    /** Prices are never accepted from the client; the server prices every order from the menu. */
    public record OrderRequest(
            @NotNull(message = "Restaurant is required") Long restaurantId,
            @NotEmpty(message = "Your cart is empty") @Size(max = 50, message = "Too many items in one order") List<@Valid OrderItemRequest> items,
            @NotBlank(message = "Contact number is required") @Pattern(regexp = AuthDtos.PHONE_REGEX, message = AuthDtos.PHONE_MESSAGE) String contactPhone,
            @NotNull(message = "Choose a payment method") PaymentMethod paymentMethod,
            @Size(max = 300, message = "Instructions are too long") String notes) {
    }

    public record OrderItemDTO(Long menuItemId, String name, BigDecimal unitPrice, int quantity, BigDecimal lineTotal) {
    }

    public record OrderDTO(Long id, String orderNumber, OrderStatus status, Long restaurantId, String restaurantName,
                           Long customerId, String customerName, String contactPhone,
                           List<OrderItemDTO> items, BigDecimal subtotal, BigDecimal taxAmount,
                           BigDecimal total, PaymentMethod paymentMethod, String notes, String cancelReason,
                           Instant createdAt, Instant updatedAt, Instant collectedAt) {
    }

    public record StatusUpdateRequest(
            @NotNull(message = "Status is required") OrderStatus status,
            @Size(max = 200, message = "Reason is too long") String reason) {
    }

    public record CancelRequest(@Size(max = 200, message = "Reason is too long") String reason) {
    }
}
