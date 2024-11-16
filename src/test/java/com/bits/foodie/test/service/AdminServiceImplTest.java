package com.bits.foodie.test.service;

import com.bits.foodie.dto.AdminDTO;
import com.bits.foodie.dto.payload.LoginRequestDTO;
import com.bits.foodie.dto.response.PlatformActivityDTO;
import com.bits.foodie.dto.response.ResponseDTO;
import com.bits.foodie.model.Admin;
import com.bits.foodie.model.Order;
import com.bits.foodie.repository.*;
import com.bits.foodie.service.AdminService;
import com.bits.foodie.service.impl.AdminServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static com.bits.foodie.utility.FoodieConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminServiceImplTest {

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private DeliveryPersonRepository deliveryPersonRepository;

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AdminServiceImpl adminService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddAdmin() {
        AdminDTO adminDTO = new AdminDTO("admin@test.com", "Admin", "password", "ADMIN");
        Admin admin = new Admin("Admin", "admin@test.com", "ADMIN", "encodedPassword");

        when(passwordEncoder.encode(adminDTO.getPassword())).thenReturn("encodedPassword");
        when(adminRepository.save(any(Admin.class))).thenReturn(admin);

        ResponseDTO response = adminService.addAdmin(adminDTO);

        assertNotNull(response);
        assertEquals("Registration Successful", response.getMessage());
        verify(adminRepository, times(1)).save(any(Admin.class));
    }

    @Test
    void testLoginAdmin_Success() {
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO("admin@test.com", "password", "ADMIN");
        Admin admin = new Admin("Admin", "admin@test.com", "ADMIN", "encodedPassword");

        when(adminRepository.findByAdminEmail(loginRequestDTO.getAdminEmail())).thenReturn(admin);
        when(passwordEncoder.matches(loginRequestDTO.getPassword(), admin.getPassword())).thenReturn(true);
        when(adminRepository.findOneByAdminEmailAndPassword(admin.getAdminEmail(), admin.getPassword())).thenReturn(Optional.of(admin));

        ResponseDTO response = adminService.loginAdmin(loginRequestDTO);

        assertNotNull(response);
        assertEquals("Login Success", response.getMessage());
    }

    @Test
    void testLoginAdmin_InvalidPassword() {
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO("admin@test.com", "wrongPassword", "ADMIN");
        Admin admin = new Admin("Admin", "admin@test.com", "ADMIN", "encodedPassword");

        when(adminRepository.findByAdminEmail(loginRequestDTO.getAdminEmail())).thenReturn(admin);
        when(passwordEncoder.matches(loginRequestDTO.getPassword(), admin.getPassword())).thenReturn(false);

        ResponseDTO response = adminService.loginAdmin(loginRequestDTO);

        assertNotNull(response);
        assertEquals("Password Not Match", response.getMessage());
    }

    @Test
    void testLoginAdmin_EmailNotExists() {
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO("unknown@test.com", "password", "ADMIN");

        when(adminRepository.findByAdminEmail(loginRequestDTO.getAdminEmail())).thenReturn(null);

        ResponseDTO response = adminService.loginAdmin(loginRequestDTO);

        assertNotNull(response);
        assertEquals("Email not exists", response.getMessage());
    }

    @Test
    void testViewAllOrders() {
        List<Order> orders = List.of(new Order(), new Order());

        when(orderRepository.findAll()).thenReturn(orders);

        List<Order> result = adminService.viewAllOrders();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    void testCancelOrder() {
        Order order = new Order();
        order.setOrderId(1);

        when(orderRepository.findByOrderId(1)).thenReturn(order);

        Order result = adminService.cancelOrder(1);

        assertNotNull(result);
        assertTrue(result.isCancelled());
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    void testRescheduleOrder() {
        Order order = new Order();
        order.setOrderId(1);

        when(orderRepository.findByOrderId(1)).thenReturn(order);

        Order result = adminService.rescheduleOrder(1, "PREPARING");

        assertNotNull(result);
        assertEquals("PREPARING", result.getStatus());
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    void testGenerateReports() {
        Order order1 = new Order();
        order1.setRestaurantId(1);
        order1.setDeliveryTimeInMinutes(30);

        Order order2 = new Order();
        order2.setRestaurantId(1);
        order2.setDeliveryTimeInMinutes(40);

        Order order3 = new Order();
        order3.setRestaurantId(2);
        order3.setDeliveryTimeInMinutes(20);

        when(orderRepository.findAll()).thenReturn(List.of(order1, order2, order3));

        Map<String, String> reports = adminService.generateReports();

        assertNotNull(reports);
        assertEquals("Restaurant ID: 1", reports.get("Most Popular Restaurant"));
        assertEquals("Restaurant ID: 2", reports.get("Fastest Delivery Restaurant"));
    }

    @Test
    void testGetPlatformActivitySummary() {
        when(customerRepository.countByIsActive(true)).thenReturn(10);
        when(restaurantRepository.countByIsActive(true)).thenReturn(5);
        when(deliveryPersonRepository.countByIsActive(true)).thenReturn(8);
        when(orderRepository.countByStatus(PICKED_UP)).thenReturn(3);
        when(orderRepository.countByStatus(EN_ROUTE)).thenReturn(2);
        when(orderRepository.countByStatus(ORDER_PLACED)).thenReturn(4);
        when(orderRepository.countByStatus(PREPARING)).thenReturn(1);
        when(orderRepository.countByStatus(DELIVERED)).thenReturn(15);
        when(orderRepository.count()).thenReturn(25L);

        PlatformActivityDTO result = adminService.getPlatformActivitySummary();

        assertNotNull(result);
        assertEquals(23, result.getActiveUsersCount());
        assertEquals(5, result.getOngoingDeliveriesCount());
        assertEquals(25, result.getTotalOrdersCount());
        assertEquals(5, result.getPendingOrdersCount());
        assertEquals(15, result.getCompletedOrdersCount());
    }
}
