package com.bits.foodie.service.impl;

import com.bits.foodie.dto.AdminDTO;
import com.bits.foodie.dto.payload.LoginRequestDTO;
import com.bits.foodie.dto.response.PlatformActivityDTO;
import com.bits.foodie.model.Admin;
import com.bits.foodie.model.Order;
import com.bits.foodie.repository.*;
import com.bits.foodie.dto.response.ResponseDTO;
import com.bits.foodie.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.bits.foodie.utility.FoodieConstants.*;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private DeliveryPersonRepository deliveryPersonRepository;
    @Autowired
    private RestaurantRepository restaurantRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public ResponseDTO addAdmin(AdminDTO adminDTO) {
        Admin admin = new Admin(
                adminDTO.getAdminName(),
                adminDTO.getAdminEmail(),
                adminDTO.getRole(),
                this.passwordEncoder.encode(adminDTO.getPassword()));
        adminRepository.save(admin);
        return new ResponseDTO("Registration Successful", true);
    }

    @Override
    public ResponseDTO loginAdmin(LoginRequestDTO loginRequestDTO) {
        String msg = "";
        Admin admin = adminRepository.findByAdminEmail(loginRequestDTO.getAdminEmail());
        if (admin != null && loginRequestDTO.getRole().equalsIgnoreCase(admin.getRole())) {
            String password = loginRequestDTO.getPassword();
            String encodedPassword = admin.getPassword();
            boolean isPwdRight = passwordEncoder.matches(password, encodedPassword);
            if (isPwdRight) {
                Optional<Admin> admin1 = adminRepository.findOneByAdminEmailAndPassword(admin.getAdminEmail(), admin.getPassword());
                if (admin1.isPresent()) {
                    return new ResponseDTO("Login Success", true);
                } else {
                    return new ResponseDTO("Login failed", false);
                }
            } else {
                return new ResponseDTO("Password Not Match", false);
            }
        } else {
            return new ResponseDTO("Email not exists", false);
        }
    }

    @Override
    public List<Order> viewAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public Order cancelOrder(int orderId) {
        Order order = orderRepository.findByOrderId(orderId);
        if (order != null) {
            order.setCancelled(true);
            orderRepository.save(order);
        }
        return order;
    }

    @Override
    public Order rescheduleOrder(int orderId, String status) {
        Order order = orderRepository.findByOrderId(orderId);
        if (order != null) {
            order.setStatus(status);
            orderRepository.save(order);
        }
        return order;
    }

    @Override
    public Map<String, String> generateReports() {
        List<Order> orders = orderRepository.findAll();
        Map<String, String> performanceMetrics = new HashMap<>();

        // 1. Most Popular Restaurant (restaurant with max number of orders)
        Integer mostPopularRestaurantId = orders.stream()
                .collect(Collectors.groupingBy(Order::getRestaurantId, Collectors.counting())) // Group by restaurantId and count orders
                .entrySet().stream()
                .max(Map.Entry.comparingByValue()) // Get restaurant with max orders
                .map(Map.Entry::getKey)
                .orElse(null);

        performanceMetrics.put("Most Popular Restaurant", mostPopularRestaurantId != null ? "Restaurant ID: " + mostPopularRestaurantId : "No data");

        // 2. Average Delivery Time (restaurant with minimum average delivery time)
        Integer fastestDeliveryRestaurantId = orders.stream()
                .collect(Collectors.groupingBy(Order::getRestaurantId, Collectors.averagingDouble(Order::getDeliveryTimeInMinutes))) // Average delivery time per restaurant
                .entrySet().stream()
                .min(Map.Entry.comparingByValue()) // Get restaurant with minimum average delivery time
                .map(Map.Entry::getKey)
                .orElse(null);

        performanceMetrics.put("Fastest Delivery Restaurant", fastestDeliveryRestaurantId != null ? "Restaurant ID: " + fastestDeliveryRestaurantId : "No data");

        // 3. Order Trends (Max Item Ordered)
        Map<Integer, Long> itemFrequencyMap = orders.stream()
                .flatMap(order -> order.getCart().getCartItems().stream()) // Get all items in all orders
                .collect(Collectors.groupingBy(cartItem -> cartItem.getItem().getItemId(), Collectors.counting())); // Count occurrences of each item

        Integer mostOrderedItemId = itemFrequencyMap.entrySet().stream()
                .max(Map.Entry.comparingByValue()) // Get item with max occurrences
                .map(Map.Entry::getKey)
                .orElse(null);

        performanceMetrics.put("Most Ordered Item", mostOrderedItemId != null ? "Item ID: " + mostOrderedItemId : "No data");

        return performanceMetrics;

    }

    @Override
    public PlatformActivityDTO getPlatformActivitySummary() {
        PlatformActivityDTO dto = new PlatformActivityDTO();
        dto.setActiveUsersCount(customerRepository.countByIsActive(true) + restaurantRepository.countByIsActive(true) +
                deliveryPersonRepository.countByIsActive(true));
        dto.setOngoingDeliveriesCount(orderRepository.countByStatus(PICKED_UP) + orderRepository.countByStatus(EN_ROUTE));
        dto.setTotalOrdersCount((int) orderRepository.count());
        dto.setPendingOrdersCount(orderRepository.countByStatus(ORDER_PLACED) + orderRepository.countByStatus(PREPARING));
        dto.setCompletedOrdersCount(orderRepository.countByStatus(DELIVERED));
        return dto;
    }

}
