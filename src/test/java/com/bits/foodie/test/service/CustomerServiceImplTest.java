package com.bits.foodie.test.service;

import com.bits.foodie.dto.CustomerDTO;
import com.bits.foodie.dto.payload.LoginRequestDTO;
import com.bits.foodie.dto.response.ResponseDTO;
import com.bits.foodie.mappers.AddressMapper;
import com.bits.foodie.mappers.CardMapper;
import com.bits.foodie.mappers.CartMapper;
import com.bits.foodie.model.*;
import com.bits.foodie.repository.CustomerRepository;
import com.bits.foodie.repository.OrderRepository;
import com.bits.foodie.service.impl.CustomerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static com.bits.foodie.utility.FoodieConstants.ORDER_PLACED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomerServiceImplTest {

    @InjectMocks
    private CustomerServiceImpl customerService;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AddressMapper addressMapper;

    @Mock
    private CartMapper cartMapper;

    @Mock
    private CardMapper cardMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddCustomer() {
        CustomerDTO customerDTO = new CustomerDTO("test@example.com", "Test User", "password", "CUSTOMER");
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

        ResponseDTO response = customerService.addCustomer(customerDTO);

        assertNotNull(response);
        assertEquals("Registration Successful", response.getMessage());

        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void testLoginCustomer_Success() {
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO("test@example.com", "password", "CUSTOMER");
        Customer customer = new Customer("CUSTOMER", "Test User", "test@example.com", "encodedPassword");

        when(customerRepository.findByCustomerEmail("test@example.com")).thenReturn(customer);
        when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(true);
        when(customerRepository.findOneByCustomerEmailAndPassword("test@example.com", "encodedPassword"))
                .thenReturn(Optional.of(customer));

        ResponseDTO response = customerService.loginCustomer(loginRequestDTO);

        assertNotNull(response);
        assertEquals("Login Success", response.getMessage());
    }

    @Test
    void testUpdateCustomer() {
        CustomerDTO customerDTO = new CustomerDTO("test@example.com", "Updated User", null, "CUSTOMER");
        customerDTO.setAddresses(Collections.emptyList());
        customerDTO.setSavedPaymentDetails(Collections.emptyList());
        Customer customer = new Customer("ROLE_CUSTOMER", "Old User", "test@example.com", "encodedPassword");

        when(customerRepository.findByCustomerEmail("test@example.com")).thenReturn(customer);

        Customer updatedCustomer = customerService.updateCustomer(customerDTO);

        assertNotNull(updatedCustomer);
        assertEquals("Updated User", updatedCustomer.getCustomerName());
        assertTrue(updatedCustomer.getAddresses().isEmpty());
        assertTrue(updatedCustomer.getSavedPaymentDetails().isEmpty());
    }

    @Test
    void testPlaceOrder_Success() {
        Customer customer = new Customer();
        customer.setCustomerId(1);
        Cart cart = new Cart();
        cart.setRestId(10);
        cart.setPrice(200.0);
        customer.setCart(cart);

        when(customerRepository.findByCustomerId(1)).thenReturn(customer);

        Order order = customerService.placeOrder(1);

        assertNotNull(order);
        assertEquals(200.0, order.getAmount());
        assertEquals(ORDER_PLACED, order.getStatus());
        verify(orderRepository, times(1)).save(order);
        verify(customerRepository, times(1)).save(customer);
    }

    @Test
    void testTrackOrders() {
        Customer customer = new Customer();
        customer.setCustomerId(1);
        Order order1 = new Order();
        order1.setOrderId(101);
        order1.setStatus("Delivered");
        Order order2 = new Order();
        order2.setOrderId(102);
        order2.setStatus("Preparing");
        customer.setHistory(Arrays.asList(order1, order2));

        when(customerRepository.findByCustomerId(1)).thenReturn(customer);

        Map<Integer, String> trackedOrders = customerService.trackOrders(1);

        assertNotNull(trackedOrders);
        assertEquals(2, trackedOrders.size());
        assertEquals("Delivered", trackedOrders.get(101));
        assertEquals("Preparing", trackedOrders.get(102));
    }

    @Test
    void testViewOrderHistory() {
        Customer customer = new Customer();
        customer.setCustomerId(1);
        Order order = new Order();
        order.setOrderId(101);
        customer.setHistory(Collections.singletonList(order));

        when(customerRepository.findByCustomerId(1)).thenReturn(customer);

        List<Order> orderHistory = customerService.viewOrderHistory(1);

        assertNotNull(orderHistory);
        assertEquals(1, orderHistory.size());
        assertEquals(101, orderHistory.get(0).getOrderId());
    }

    @Test
    void testDeactivateCustomer() {
        Customer customer = new Customer();
        customer.setCustomerId(1);
        customer.setActive(true);

        when(customerRepository.findByCustomerId(1)).thenReturn(customer);

        customerService.deactivateCustomer(1);

        assertFalse(customer.isActive());
        verify(customerRepository, times(1)).save(customer);
    }
}
