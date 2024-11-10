package com.bits.foodie.controller;

import com.bits.foodie.dto.*;
import com.bits.foodie.dto.payload.LoginRequestDTO;
import com.bits.foodie.dto.payload.RegisterRequestDTO;
import com.bits.foodie.dto.response.PlatformActivityDTO;
import com.bits.foodie.dto.response.ResponseDTO;
import com.bits.foodie.model.Order;
import com.bits.foodie.service.AdminService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static com.bits.foodie.utility.FoodieConstants.*;

@RestController
@RequestMapping("api/v1/admin")
public class AdminController {

    @Autowired
    AdminService adminService;

    @Autowired
    HomeController homeController;

    @Autowired
    CustomerController customerController;

    @Autowired
    RestaurantController restaurantController;

    @Autowired
    DeliveryPersonController deliveryPersonController;
    @Autowired
    private ObjectMapper objectMapper;

    @PostMapping(path = "/save")
    public ResponseDTO saveAdmin(@RequestBody RegisterRequestDTO registerRequestDTO) {
        AdminDTO adminDTO = new AdminDTO(registerRequestDTO.getAdminEmail(), registerRequestDTO.getAdminName(),
                registerRequestDTO.getPassword(), registerRequestDTO.getRole());
        return adminService.addAdmin(adminDTO);
    }

    @PostMapping(path = "/login")
    public ResponseDTO loginAdmin(@RequestBody LoginRequestDTO loginRequestDTO) {
        return adminService.loginAdmin(loginRequestDTO);
    }

    @PostMapping(path = "/createUser")
    public ResponseEntity<?> createUser(@RequestBody RegisterRequestDTO registerRequestDTO) {
        return homeController.saveUser(registerRequestDTO);
    }

    @PatchMapping(path = "/updateUser/{userRole}/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable String userRole, @PathVariable int userId,
                                        @RequestBody(required = false) Map<String, Object> dtoData) {
        try {
            switch (userRole.toLowerCase()) {
                case "customer" -> {
                    CustomerDTO customerDto = objectMapper.convertValue(dtoData, CustomerDTO.class);
                    customerController.updateCustomer(customerDto);
                    return ResponseEntity.ok("CustomerDTO processed");
                }
                case "restaurant" -> {
                    RestaurantDTO restaurantDto = objectMapper.convertValue(dtoData, RestaurantDTO.class);
                    restaurantController.updateRestaurant(userId, restaurantDto);
                    return ResponseEntity.ok("RestaurantDTO processed");
                }
                case "delivery_person" -> {
                    DeliveryPersonDTO deliveryPersonDto = objectMapper.convertValue(dtoData, DeliveryPersonDTO.class);
                    deliveryPersonController.updateDeliveryPerson(userId, deliveryPersonDto);
                    return ResponseEntity.ok("DeliveryPersonDTO processed");
                }
                default -> {
                    return ResponseEntity.badRequest().body("Invalid userRole");
                }
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Failed to process request body: " + e.getMessage());
        }
    }


    @PatchMapping(path = "/deactivateUser/{userRole}/{userId}")
    public ResponseEntity<?> deactivateUser(@PathVariable String userRole, @PathVariable int userId) {

        switch (userRole) {
            case CUSTOMER -> {
                customerController.deactivateCustomer(userId);
                return ResponseEntity.ok("Customer deactivated");
            }
            case RESTAURANT -> {
                restaurantController.deactivateRestaurant(userId);
                return ResponseEntity.ok("Restaurant deactivated");
            }
            case DELIVERY_PERSON -> {
                deliveryPersonController.deactivateDeliveryPerson(userId);
                return ResponseEntity.ok("DeliveryPerson deactivated");
            }
            case null, default -> {
                return ResponseEntity.badRequest().body("Invalid type or bad request");
            }
        }
    }

    @GetMapping(path = "/viewAllOrders")
    public List<Order> viewAllOrders() {
        return adminService.viewAllOrders();
    }

    @PatchMapping(path = "/cancelOrder/{orderId}")
    public Order cancelOrder(@PathVariable int orderId) {
        return adminService.cancelOrder(orderId);
    }

    @PatchMapping(path = "/rescheduleOrder/{orderId}")
    public Order rescheduleOrder(@PathVariable int orderId, @RequestParam(required = true) String status) {
        return adminService.rescheduleOrder(orderId, status);
    }

    @PreAuthorize("hasRole('Admin')")
    @GetMapping(path = "/generateReports")
    public Map<String, String> generateReports() {
        return adminService.generateReports();
    }

    @PreAuthorize("hasRole('Admin')")
    @GetMapping("/platformActivity")
    public PlatformActivityDTO getPlatformActivity() {
        return adminService.getPlatformActivitySummary();
    }
}
