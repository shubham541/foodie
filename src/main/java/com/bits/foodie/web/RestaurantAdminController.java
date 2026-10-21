package com.bits.foodie.web;

import com.bits.foodie.dto.AdminDtos.ActiveRequest;
import com.bits.foodie.dto.AdminDtos.Report;
import com.bits.foodie.dto.AdminDtos.Summary;
import com.bits.foodie.dto.AuthDtos.UserDTO;
import com.bits.foodie.dto.CatalogDtos.*;
import com.bits.foodie.dto.OrderDtos.OrderDTO;
import com.bits.foodie.security.AuthUser;
import com.bits.foodie.service.AdminService;
import com.bits.foodie.service.CatalogService;
import com.bits.foodie.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Everything the restaurant owner (the boss of the whole system) can do; limited by the security config to the RESTAURANT role. */
@RestController
@RequestMapping("/api/v1/restaurant-admin")
public class RestaurantAdminController {

    private final CatalogService catalog;
    private final OrderService orders;
    private final AdminService management;

    public RestaurantAdminController(CatalogService catalog, OrderService orders, AdminService management) {
        this.catalog = catalog;
        this.orders = orders;
        this.management = management;
    }

    @GetMapping("/restaurant")
    public RestaurantDTO restaurant(@AuthenticationPrincipal AuthUser user) {
        return catalog.ownRestaurant(user.id());
    }

    @PatchMapping("/settings")
    public RestaurantDTO settings(@AuthenticationPrincipal AuthUser user, @Valid @RequestBody SettingsRequest req) {
        return catalog.updateSettings(user.id(), req);
    }

    @GetMapping("/menu")
    public List<MenuItemDTO> menu(@AuthenticationPrincipal AuthUser user) {
        return catalog.ownMenu(user.id());
    }

    @PatchMapping("/menu/{itemId}")
    public MenuItemDTO updateItem(@AuthenticationPrincipal AuthUser user, @PathVariable Long itemId,
                                  @Valid @RequestBody MenuItemUpdate update) {
        return catalog.updateMenuItem(user.id(), itemId, update);
    }

    /** Open orders plus today's finished ones; {@code filter=ALL} returns the full history. */
    @GetMapping("/orders")
    public List<OrderDTO> orders(@AuthenticationPrincipal AuthUser user, @RequestParam(required = false) String filter) {
        return orders.forRestaurantOwner(user.id(), filter);
    }

    @GetMapping("/summary")
    public Summary summary() {
        return management.summary();
    }

    @GetMapping("/reports")
    public Report reports() {
        return management.report();
    }

    @GetMapping("/customers")
    public List<UserDTO> customers() {
        return management.customers();
    }

    @PatchMapping("/customers/{id}/active")
    public UserDTO setCustomerActive(@PathVariable Long id, @Valid @RequestBody ActiveRequest req) {
        return management.setCustomerActive(id, req.active());
    }
}
