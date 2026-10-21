package com.bits.foodie.service;

import com.bits.foodie.config.AppProperties;
import com.bits.foodie.domain.*;
import com.bits.foodie.dto.OrderDtos.OrderDTO;
import com.bits.foodie.dto.OrderDtos.OrderItemRequest;
import com.bits.foodie.dto.OrderDtos.OrderRequest;
import com.bits.foodie.exception.ApiException;
import com.bits.foodie.repository.*;
import com.bits.foodie.security.AuthUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.*;

/** Placing orders and moving them through the lifecycle, with every rule enforced server-side. */
@Service
public class OrderService {

    private final OrderRepository orders;
    private final UserRepository users;
    private final RestaurantRepository restaurants;
    private final MenuItemRepository menuItems;
    private final AppProperties props;

    public OrderService(OrderRepository orders, UserRepository users, RestaurantRepository restaurants,
                        MenuItemRepository menuItems, AppProperties props) {
        this.orders = orders;
        this.users = users;
        this.restaurants = restaurants;
        this.menuItems = menuItems;
        this.props = props;
    }

    // ------------------------------------------------------------------ placing

    @Transactional
    public OrderDTO place(Long customerId, OrderRequest req) {
        User customer = users.findById(customerId).orElseThrow(() -> ApiException.unauthorized("Please log in again."));
        Restaurant restaurant = restaurants.findByIdAndActiveTrue(req.restaurantId())
                .orElseThrow(() -> ApiException.notFound("Restaurant not found."));
        if (!restaurant.isAcceptingOrders()) {
            throw ApiException.conflict(restaurant.getName() + " is not accepting orders right now.");
        }

        // Merge duplicate lines, then price everything from the database (never from the client).
        Map<Long, Integer> quantities = new LinkedHashMap<>();
        for (OrderItemRequest line : req.items()) {
            quantities.merge(line.menuItemId(), line.quantity(), Integer::sum);
        }
        int max = props.getOrder().getMaxQuantityPerItem();
        quantities.forEach((id, qty) -> {
            if (qty > max) throw ApiException.badRequest("You can order at most " + max + " of one item.");
        });
        Map<Long, MenuItem> found = new HashMap<>();
        menuItems.findByRestaurantIdAndIdIn(restaurant.getId(), quantities.keySet()).forEach(m -> found.put(m.getId(), m));

        Order order = new Order();
        BigDecimal subtotal = BigDecimal.ZERO;
        for (Map.Entry<Long, Integer> e : quantities.entrySet()) {
            MenuItem item = found.get(e.getKey());
            if (item == null) throw ApiException.conflict("An item in your cart is no longer on the menu. Please review your cart.");
            if (!item.isAvailable()) throw ApiException.conflict(item.getName() + " is currently unavailable. Please remove it from your cart.");
            OrderItem line = new OrderItem();
            line.setMenuItemId(item.getId());
            line.setName(item.getName());
            line.setUnitPrice(item.getPrice());
            line.setQuantity(e.getValue());
            line.setLineTotal(item.getPrice().multiply(BigDecimal.valueOf(e.getValue())).setScale(2, RoundingMode.HALF_UP));
            order.addItem(line);
            subtotal = subtotal.add(line.getLineTotal());
        }
        BigDecimal tax = subtotal.multiply(restaurant.getTaxRatePercent()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        order.setCustomer(customer);
        order.setRestaurant(restaurant);
        order.setSubtotal(subtotal.setScale(2, RoundingMode.HALF_UP));
        order.setTaxAmount(tax);
        order.setTotal(order.getSubtotal().add(tax));
        order.setPaymentMethod(req.paymentMethod());
        order.setContactPhone(req.contactPhone());
        order.setNotes(req.notes() == null || req.notes().isBlank() ? null : req.notes().trim());
        order.setStatus(OrderStatus.PLACED);
        return DtoMapper.order(orders.save(order));
    }

    // ------------------------------------------------------------------ reading

    @Transactional(readOnly = true)
    public List<OrderDTO> mine(Long customerId) {
        return orders.findByCustomerIdOrderByCreatedAtDesc(customerId).stream().map(DtoMapper::order).toList();
    }

    @Transactional(readOnly = true)
    public OrderDTO get(AuthUser actor, Long orderId) {
        Order o = find(orderId);
        if (!canView(actor, o)) throw ApiException.notFound("Order not found.");
        return DtoMapper.order(o);
    }

    @Transactional(readOnly = true)
    public List<OrderDTO> forRestaurantOwner(Long ownerId, String filter) {
        Restaurant r = restaurants.findByOwnerId(ownerId).orElseThrow(() -> ApiException.notFound("No restaurant is linked to this account."));
        Instant startOfToday = java.time.LocalDate.now(java.time.ZoneOffset.UTC).atStartOfDay().toInstant(java.time.ZoneOffset.UTC);
        return orders.findByRestaurantIdOrderByCreatedAtDesc(r.getId()).stream()
                // finished orders only matter on today's board; active ones always show
                .filter(o -> !o.getStatus().isTerminal() || o.getUpdatedAt().isAfter(startOfToday) || "ALL".equalsIgnoreCase(filter))
                .map(DtoMapper::order).toList();
    }

    // ------------------------------------------------------------------ transitions

    @Transactional
    public OrderDTO changeStatus(AuthUser actor, Long orderId, OrderStatus to, String reason) {
        Order o = find(orderId);
        if (!canView(actor, o)) throw ApiException.notFound("Order not found.");
        OrderStatus from = o.getStatus();
        if (from.isTerminal()) {
            throw ApiException.conflict("This order is already " + from.name().toLowerCase().replace('_', ' ') + ".");
        }
        if (to == from) {
            return DtoMapper.order(o);
        }
        if (!isAllowed(actor, o, from, to)) {
            throw ApiException.conflict("You cannot move this order from " + label(from) + " to " + label(to) + ".");
        }
        o.setStatus(to);
        o.setUpdatedAt(Instant.now());
        if (to == OrderStatus.CANCELLED) {
            o.setCancelReason(reason != null && !reason.isBlank() ? reason.trim() : defaultCancelReason(actor.role()));
        }
        if (to == OrderStatus.COLLECTED) {
            o.setCollectedAt(Instant.now());
        }
        return DtoMapper.order(o);
    }

    @Transactional
    public OrderDTO cancel(AuthUser actor, Long orderId, String reason) {
        return changeStatus(actor, orderId, OrderStatus.CANCELLED, reason);
    }

    private boolean isAllowed(AuthUser actor, Order o, OrderStatus from, OrderStatus to) {
        boolean cancel = to == OrderStatus.CANCELLED;
        boolean forward = to == from.next();
        return switch (actor.role()) {
            case CUSTOMER -> cancel && from == OrderStatus.PLACED;
            // the owner is the boss: move any open order forward or cancel it at any stage
            case RESTAURANT -> cancel || forward;
        };
    }

    private static String defaultCancelReason(Role role) {
        return switch (role) {
            case CUSTOMER -> "Cancelled by customer";
            case RESTAURANT -> "Cancelled by restaurant";
        };
    }

    private static String label(OrderStatus s) {
        return s.name().toLowerCase().replace('_', ' ');
    }

    /** Who may see (and therefore act on) an order. */
    static boolean canView(AuthUser actor, Order o) {
        return switch (actor.role()) {
            case CUSTOMER -> o.getCustomer().getId().equals(actor.id());
            case RESTAURANT -> o.getRestaurant().getOwner().getId().equals(actor.id());
        };
    }

    private Order find(Long id) {
        return orders.findById(id).orElseThrow(() -> ApiException.notFound("Order not found."));
    }
}
