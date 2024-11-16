package com.bits.foodie.test.controller;

import com.bits.foodie.controller.*;
import com.bits.foodie.dto.*;
import com.bits.foodie.dto.payload.LoginRequestDTO;
import com.bits.foodie.dto.payload.RegisterRequestDTO;
import com.bits.foodie.dto.response.PlatformActivityDTO;
import com.bits.foodie.dto.response.ResponseDTO;
import com.bits.foodie.exception.ResourceNotFoundException;
import com.bits.foodie.model.Order;
import com.bits.foodie.service.AdminService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminControllerTest {

    @InjectMocks
    private AdminController adminController;

    @Mock
    private AdminService adminService;

    @Mock
    private HomeController homeController;

    @Mock
    private CustomerController customerController;

    @Mock
    private RestaurantController restaurantController;

    @Mock
    private DeliveryPersonController deliveryPersonController;

    @Mock
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveAdmin_Success() {
        // Arrange
        RegisterRequestDTO registerRequestDTO = new RegisterRequestDTO();
        registerRequestDTO.setAdminEmail("admin@test.com");
        registerRequestDTO.setAdminName("Admin User");
        ResponseDTO mockResponse = new ResponseDTO("Admin saved successfully", true);
        when(adminService.addAdmin(any(AdminDTO.class))).thenReturn(mockResponse);

        // Act
        ResponseDTO response = adminController.saveAdmin(registerRequestDTO);

        // Assert
        assertNotNull(response);
        assertEquals("Admin saved successfully", response.getMessage());
        verify(adminService, times(1)).addAdmin(any(AdminDTO.class));
    }

    @Test
    void testLoginAdmin_Success() {
        // Arrange
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO("admin@test.com", "password");
        ResponseDTO mockResponse = new ResponseDTO("Login successful", true);
        when(adminService.loginAdmin(loginRequestDTO)).thenReturn(mockResponse);

        // Act
        ResponseDTO response = adminController.loginAdmin(loginRequestDTO);

        // Assert
        assertNotNull(response);
        assertEquals("Login successful", response.getMessage());
        verify(adminService, times(1)).loginAdmin(loginRequestDTO);
    }

    @Test
    void testDeactivateUser_Customer() {
        // Arrange
        int userId = 1;
        doNothing().when(customerController).deactivateCustomer(userId);

        // Act
        ResponseEntity<?> response = adminController.deactivateUser("customer", userId);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Customer deactivated", response.getBody());
        verify(customerController, times(1)).deactivateCustomer(userId);
    }

    @Test
    void testViewAllOrders_Success() {
        // Arrange
        List<Order> mockOrders = new ArrayList<>();
        Order order1 = new Order();
        order1.setOrderId(1);
        mockOrders.add(order1);
        when(adminService.viewAllOrders()).thenReturn(mockOrders);

        // Act
        List<Order> orders = adminController.viewAllOrders();

        // Assert
        assertNotNull(orders);
        assertEquals(1, orders.size());
        verify(adminService, times(1)).viewAllOrders();
    }

    @Test
    void testViewAllOrders_ThrowsException() {
        // Arrange
        when(adminService.viewAllOrders()).thenReturn(Collections.emptyList());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> adminController.viewAllOrders());
        assertEquals("No orders found.", exception.getMessage());
        verify(adminService, times(1)).viewAllOrders();
    }

    @Test
    void testCancelOrder_Success() {
        // Arrange
        int orderId = 1;
        Order mockOrder = new Order();
        mockOrder.setOrderId(orderId);
        when(adminService.cancelOrder(orderId)).thenReturn(mockOrder);

        // Act
        Order order = adminController.cancelOrder(orderId);

        // Assert
        assertNotNull(order);
        assertEquals(orderId, order.getOrderId());
        verify(adminService, times(1)).cancelOrder(orderId);
    }

    @Test
    void testRescheduleOrder_Success() {
        // Arrange
        int orderId = 1;
        String status = "Rescheduled";
        Order mockOrder = new Order();
        mockOrder.setOrderId(orderId);
        when(adminService.rescheduleOrder(orderId, status)).thenReturn(mockOrder);

        // Act
        Order order = adminController.rescheduleOrder(orderId, status);

        // Assert
        assertNotNull(order);
        assertEquals(orderId, order.getOrderId());
        verify(adminService, times(1)).rescheduleOrder(orderId, status);
    }

    @Test
    void testGenerateReports_Success() {
        // Arrange
        Map<String, String> mockReport = new HashMap<>();
        mockReport.put("Total Users", "100");
        when(adminService.generateReports()).thenReturn(mockReport);

        // Act
        Map<String, String> reports = adminController.generateReports();

        // Assert
        assertNotNull(reports);
        assertEquals("100", reports.get("Total Users"));
        verify(adminService, times(1)).generateReports();
    }

    @Test
    void testGetPlatformActivity_Success() {
        // Arrange
        PlatformActivityDTO mockActivity = new PlatformActivityDTO(50, 10, 20);
        when(adminService.getPlatformActivitySummary()).thenReturn(mockActivity);

        // Act
        PlatformActivityDTO activity = adminController.getPlatformActivity();

        // Assert
        assertNotNull(activity);
        assertEquals(50, activity.getActiveUsersCount());
        verify(adminService, times(1)).getPlatformActivitySummary();
    }
}
