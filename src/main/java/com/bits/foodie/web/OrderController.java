package com.bits.foodie.web;

import com.bits.foodie.domain.Role;
import com.bits.foodie.dto.OrderDtos.*;
import com.bits.foodie.exception.ApiException;
import com.bits.foodie.security.AuthUser;
import com.bits.foodie.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orders;

    public OrderController(OrderService orders) {
        this.orders = orders;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderDTO place(@AuthenticationPrincipal AuthUser user, @Valid @RequestBody OrderRequest req) {
        if (user.role() != Role.CUSTOMER) {
            throw ApiException.forbidden("Only customers can place orders.");
        }
        return orders.place(user.id(), req);
    }

    @GetMapping("/mine")
    public List<OrderDTO> mine(@AuthenticationPrincipal AuthUser user) {
        if (user.role() != Role.CUSTOMER) {
            throw ApiException.forbidden("Only customers have an order history.");
        }
        return orders.mine(user.id());
    }

    @GetMapping("/{id}")
    public OrderDTO get(@AuthenticationPrincipal AuthUser user, @PathVariable Long id) {
        return orders.get(user, id);
    }

    @PatchMapping("/{id}/status")
    public OrderDTO updateStatus(@AuthenticationPrincipal AuthUser user, @PathVariable Long id,
                                 @Valid @RequestBody StatusUpdateRequest req) {
        return orders.changeStatus(user, id, req.status(), req.reason());
    }

    @PostMapping("/{id}/cancel")
    public OrderDTO cancel(@AuthenticationPrincipal AuthUser user, @PathVariable Long id,
                           @Valid @RequestBody(required = false) CancelRequest req) {
        return orders.cancel(user, id, req == null ? null : req.reason());
    }
}
