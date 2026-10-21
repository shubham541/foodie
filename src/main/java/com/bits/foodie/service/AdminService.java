package com.bits.foodie.service;

import com.bits.foodie.domain.OrderStatus;
import com.bits.foodie.domain.Role;
import com.bits.foodie.domain.User;
import com.bits.foodie.dto.AdminDtos.Report;
import com.bits.foodie.dto.AdminDtos.Summary;
import com.bits.foodie.dto.AdminDtos.TopItem;
import com.bits.foodie.dto.AuthDtos.UserDTO;
import com.bits.foodie.exception.ApiException;
import com.bits.foodie.repository.OrderRepository;
import com.bits.foodie.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

/** Business-wide numbers and customer management, available to the restaurant owner. */
@Service
public class AdminService {

    private final OrderRepository orders;
    private final UserRepository users;

    public AdminService(OrderRepository orders, UserRepository users) {
        this.orders = orders;
        this.users = users;
    }

    @Transactional(readOnly = true)
    public List<UserDTO> customers() {
        return users.findByRoleOrderByIdAsc(Role.CUSTOMER).stream().map(DtoMapper::user).toList();
    }

    @Transactional
    public UserDTO setCustomerActive(Long userId, boolean active) {
        User u = users.findById(userId).orElseThrow(() -> ApiException.notFound("Customer not found."));
        if (u.getRole() != Role.CUSTOMER) {
            throw ApiException.badRequest("Only customer accounts can be activated or deactivated.");
        }
        u.setActive(active);
        return DtoMapper.user(u);
    }

    @Transactional(readOnly = true)
    public Summary summary() {
        long pending = orders.countByStatusIn(List.of(OrderStatus.PLACED, OrderStatus.ACCEPTED, OrderStatus.PREPARING, OrderStatus.READY_FOR_PICKUP));
        return new Summary(orders.count(), pending, orders.countByStatus(OrderStatus.READY_FOR_PICKUP),
                orders.countByStatus(OrderStatus.COLLECTED), orders.countByStatus(OrderStatus.CANCELLED),
                users.countByRoleAndActiveTrue(Role.CUSTOMER), orders.collectedRevenue());
    }

    @Transactional(readOnly = true)
    public Report report() {
        Double avgMinutes = orders.pickupTimes().stream()
                .mapToDouble(row -> Duration.between((Instant) row[0], (Instant) row[1]).toSeconds() / 60.0)
                .average().stream().boxed().findFirst().orElse(null);

        Double avg = orders.averageCollectedValue();
        List<TopItem> top = orders.topItems(PageRequest.of(0, 5)).stream()
                .map(r -> new TopItem((String) r[0], ((Number) r[1]).longValue())).toList();

        return new Report(avgMinutes,
                avg == null ? BigDecimal.ZERO : BigDecimal.valueOf(avg).setScale(2, RoundingMode.HALF_UP),
                top);
    }
}
