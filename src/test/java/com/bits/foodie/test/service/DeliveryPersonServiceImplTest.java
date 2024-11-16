package com.bits.foodie.test.service;

import com.bits.foodie.dto.DeliveryPersonDTO;
import com.bits.foodie.dto.payload.LoginRequestDTO;
import com.bits.foodie.dto.response.ResponseDTO;
import com.bits.foodie.exception.ResourceNotFoundException;
import com.bits.foodie.model.DeliveryPerson;
import com.bits.foodie.model.Order;
import com.bits.foodie.repository.DeliveryPersonRepository;
import com.bits.foodie.repository.OrderRepository;
import com.bits.foodie.service.impl.DeliveryPersonServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static com.bits.foodie.utility.FoodieConstants.DELIVERY_PERSON;
import static com.bits.foodie.utility.FoodieConstants.ORDER_PLACED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DeliveryPersonServiceImplTest {

    @InjectMocks
    private DeliveryPersonServiceImpl deliveryPersonService;

    @Mock
    private DeliveryPersonRepository deliveryPersonRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddDeliveryPerson() {
        DeliveryPersonDTO deliveryPersonDTO = new DeliveryPersonDTO(
                "test@example.com",
                "Test DeliveryPerson",
                "password",
                "9876543210",
                "Bike",
                "ROLE_DELIVERY");

        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

        ResponseDTO response = deliveryPersonService.addDeliveryPerson(deliveryPersonDTO);

        assertNotNull(response);
        assertEquals("Registration Successful", response.getMessage());

        verify(deliveryPersonRepository, times(1)).save(any(DeliveryPerson.class));
    }

    @Test
    void testLoginDeliveryPerson_Success() {
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO("test@example.com", "password", DELIVERY_PERSON);
        DeliveryPerson deliveryPerson = new DeliveryPerson(
                "test@example.com",
                "Test DeliveryPerson",
                "encodedPassword",
                "9876543210",
                "Bike",
                DELIVERY_PERSON);

        when(deliveryPersonRepository.findByDeliveryPersonEmail("test@example.com")).thenReturn(deliveryPerson);
        when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(true);
        when(deliveryPersonRepository.findOneByDeliveryPersonEmailAndPassword("test@example.com", "encodedPassword"))
                .thenReturn(Optional.of(deliveryPerson));

        ResponseDTO response = deliveryPersonService.loginDeliveryPerson(loginRequestDTO);

        assertNotNull(response);
        assertEquals("Login Success", response.getMessage());
    }

    @Test
    void testUpdateDeliveryPerson() {
        DeliveryPersonDTO deliveryPersonDTO = new DeliveryPersonDTO(
                "test@example.com",
                "Updated DeliveryPerson",
                null,
                "1234567890",
                "Car",
                "DELIVERY");

        DeliveryPerson deliveryPerson = new DeliveryPerson(
                "test@example.com",
                "Old DeliveryPerson",
                "encodedPassword",
                "9876543210",
                "Bike",
                "DELIVERY");

        when(deliveryPersonRepository.findByDeliveryPersonId(1)).thenReturn(deliveryPerson);

        DeliveryPerson updatedDeliveryPerson = deliveryPersonService.updateDeliveryPerson(1, deliveryPersonDTO);

        assertNotNull(updatedDeliveryPerson);
        assertEquals("Updated DeliveryPerson", updatedDeliveryPerson.getDeliveryPersonName());
        assertEquals("1234567890", updatedDeliveryPerson.getContactDetails());
        assertEquals("Car", updatedDeliveryPerson.getVehicleType());
        verify(deliveryPersonRepository, times(1)).save(updatedDeliveryPerson);
    }

    @Test
    void testViewAvailableDeliveries() {
        Order order1 = new Order();
        order1.setStatus(ORDER_PLACED);
        Order order2 = new Order();
        order2.setStatus("Delivered");
        List<Order> orders = Arrays.asList(order1, order2);

        when(orderRepository.findAll()).thenReturn(orders);

        List<Order> availableOrders = deliveryPersonService.viewAvailableDeliveries();

        assertNotNull(availableOrders);
        assertEquals(1, availableOrders.size());
        assertEquals(ORDER_PLACED, availableOrders.get(0).getStatus());
    }

    @Test
    void testUpdateOrderStatus() {
        Order order = new Order();
        order.setOrderId(1);
        order.setStatus(ORDER_PLACED);

        when(orderRepository.findByOrderId(1)).thenReturn(order);

        Order updatedOrder = deliveryPersonService.updateOrderStatus(1, 1, "Delivered");

        assertNotNull(updatedOrder);
        assertEquals("Delivered", updatedOrder.getStatus());
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    void testManageDeliveryAvailability() {
        DeliveryPerson deliveryPerson = new DeliveryPerson();
        deliveryPerson.setDeliveryPersonId(1);
        deliveryPerson.setDevAvailable(false);

        when(deliveryPersonRepository.findByDeliveryPersonId(1)).thenReturn(deliveryPerson);

        DeliveryPerson updatedDeliveryPerson = deliveryPersonService.manageDeliveryAvailability(1, true);

        assertNotNull(updatedDeliveryPerson);
        assertTrue(updatedDeliveryPerson.isDevAvailable());
        verify(deliveryPersonRepository, times(1)).save(deliveryPerson);
    }

    @Test
    void testDeactivateDeliveryPerson() {
        DeliveryPerson deliveryPerson = new DeliveryPerson();
        deliveryPerson.setDeliveryPersonId(1);
        deliveryPerson.setActive(true);

        when(deliveryPersonRepository.findByDeliveryPersonId(1)).thenReturn(deliveryPerson);

        deliveryPersonService.deactivateDeliveryPerson(1);

        assertFalse(deliveryPerson.isActive());
        verify(deliveryPersonRepository, times(1)).save(deliveryPerson);
    }

    @Test
    void testFindById_Success() {
        DeliveryPerson deliveryPerson = new DeliveryPerson();
        deliveryPerson.setDeliveryPersonId(1);

        when(deliveryPersonRepository.findById(1)).thenReturn(Optional.of(deliveryPerson));

        DeliveryPerson foundDeliveryPerson = deliveryPersonService.findById(1);

        assertNotNull(foundDeliveryPerson);
        assertEquals(1, foundDeliveryPerson.getDeliveryPersonId());
    }

    @Test
    void testFindById_NotFound() {
        when(deliveryPersonRepository.findById(1)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            deliveryPersonService.findById(1);
        });

        assertEquals("DeliveryPerson not found with id 1", exception.getMessage());
    }
}
