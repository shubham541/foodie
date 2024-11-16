package com.bits.foodie;

import com.bits.foodie.controller.DeliveryPersonController;
import com.bits.foodie.dto.DeliveryPersonDTO;
import com.bits.foodie.dto.payload.LoginRequestDTO;
import com.bits.foodie.dto.payload.RegisterRequestDTO;
import com.bits.foodie.dto.response.ResponseDTO;
import com.bits.foodie.exception.ResourceNotFoundException;
import com.bits.foodie.model.DeliveryPerson;
import com.bits.foodie.model.Order;
import com.bits.foodie.service.DeliveryPersonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DeliveryPersonControllerTest {

    @InjectMocks
    private DeliveryPersonController deliveryPersonController;

    @Mock
    private DeliveryPersonService deliveryPersonService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveDeliveryPerson_Success() {
        // Arrange
        RegisterRequestDTO registerRequestDTO = new RegisterRequestDTO();
        registerRequestDTO.setDeliveryPersonEmail("delivery@test.com");
        registerRequestDTO.setDeliveryPersonName("Delivery Person");
        ResponseDTO mockResponse = new ResponseDTO("Delivery person saved successfully", true);
        when(deliveryPersonService.addDeliveryPerson(any(DeliveryPersonDTO.class))).thenReturn(mockResponse);

        // Act
        ResponseDTO response = deliveryPersonController.saveDeliveryPerson(registerRequestDTO);

        // Assert
        assertNotNull(response);
        assertEquals("Delivery person saved successfully", response.getMessage());
        verify(deliveryPersonService, times(1)).addDeliveryPerson(any(DeliveryPersonDTO.class));
    }

    @Test
    void testLoginDeliveryPerson_Success() {
        // Arrange
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO("delivery@test.com", "password");
        ResponseDTO mockResponse = new ResponseDTO("Login successful", true);
        when(deliveryPersonService.loginDeliveryPerson(loginRequestDTO)).thenReturn(mockResponse);

        // Act
        ResponseDTO response = deliveryPersonController.loginDeliveryPerson(loginRequestDTO);

        // Assert
        assertNotNull(response);
        assertEquals("Login successful", response.getMessage());
        verify(deliveryPersonService, times(1)).loginDeliveryPerson(loginRequestDTO);
    }

    @Test
    void testUpdateDeliveryPerson_Success() {
        // Arrange
        int id = 1;
        DeliveryPersonDTO deliveryPersonDTO = new DeliveryPersonDTO();
        DeliveryPerson mockPerson = new DeliveryPerson();
        mockPerson.setDeliveryPersonId(id);
        when(deliveryPersonService.updateDeliveryPerson(id, deliveryPersonDTO)).thenReturn(mockPerson);

        // Act
        DeliveryPerson updatedPerson = deliveryPersonController.updateDeliveryPerson(id, deliveryPersonDTO);

        // Assert
        assertNotNull(updatedPerson);
        assertEquals(id, updatedPerson.getDeliveryPersonId());
        verify(deliveryPersonService, times(1)).updateDeliveryPerson(id, deliveryPersonDTO);
    }

    @Test
    void testUpdateDeliveryPerson_ThrowsException() {
        // Arrange
        int id = 1;
        DeliveryPersonDTO deliveryPersonDTO = new DeliveryPersonDTO();
        when(deliveryPersonService.updateDeliveryPerson(id, deliveryPersonDTO)).thenReturn(null);

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> deliveryPersonController.updateDeliveryPerson(id, deliveryPersonDTO));
        assertEquals("Delivery person not found for update with ID: " + id, exception.getMessage());
        verify(deliveryPersonService, times(1)).updateDeliveryPerson(id, deliveryPersonDTO);
    }

    @Test
    void testViewAvailableDeliveries_Success() {
        // Arrange
        List<Order> mockOrders = new ArrayList<>();
        Order order = new Order();
        mockOrders.add(order);
        when(deliveryPersonService.viewAvailableDeliveries()).thenReturn(mockOrders);

        // Act
        List<Order> orders = deliveryPersonController.viewAvailableDeliveries();

        // Assert
        assertNotNull(orders);
        assertEquals(1, orders.size());
        verify(deliveryPersonService, times(1)).viewAvailableDeliveries();
    }

    @Test
    void testUpdateOrderStatus_Success() {
        // Arrange
        int deliveryPersonId = 1;
        int orderId = 100;
        String status = "Completed";
        Order mockOrder = new Order();
        mockOrder.setOrderId(orderId);
        when(deliveryPersonService.updateOrderStatus(deliveryPersonId, orderId, status)).thenReturn(mockOrder);

        // Act
        Order updatedOrder = deliveryPersonController.updateOrderStatus(deliveryPersonId, orderId, status);

        // Assert
        assertNotNull(updatedOrder);
        assertEquals(orderId, updatedOrder.getOrderId());
        verify(deliveryPersonService, times(1)).updateOrderStatus(deliveryPersonId, orderId, status);
    }

    @Test
    void testManageDeliveryAvailability_Success() {
        // Arrange
        int deliveryPersonId = 1;
        boolean availability = true;
        DeliveryPerson mockPerson = new DeliveryPerson();
        mockPerson.setDeliveryPersonId(deliveryPersonId);
        when(deliveryPersonService.manageDeliveryAvailability(deliveryPersonId, availability)).thenReturn(mockPerson);

        // Act
        DeliveryPerson updatedPerson = deliveryPersonController.manageDeliveryAvailability(deliveryPersonId, availability);

        // Assert
        assertNotNull(updatedPerson);
        assertEquals(deliveryPersonId, updatedPerson.getDeliveryPersonId());
        verify(deliveryPersonService, times(1)).manageDeliveryAvailability(deliveryPersonId, availability);
    }

    @Test
    void testDeactivateDeliveryPerson_Success() {
        // Arrange
        int userId = 1;
        doNothing().when(deliveryPersonService).deactivateDeliveryPerson(userId);

        // Act
        deliveryPersonController.deactivateDeliveryPerson(userId);

        // Assert
        verify(deliveryPersonService, times(1)).deactivateDeliveryPerson(userId);
    }

    @Test
    void testGetDeliveryPersonById_Success() {
        // Arrange
        int id = 1;
        DeliveryPerson mockPerson = new DeliveryPerson();
        mockPerson.setDeliveryPersonId(id);
        when(deliveryPersonService.findById(id)).thenReturn(mockPerson);

        // Act
        DeliveryPerson person = deliveryPersonController.getDeliveryPersonById(id);

        // Assert
        assertNotNull(person);
        assertEquals(id, person.getDeliveryPersonId());
        verify(deliveryPersonService, times(1)).findById(id);
    }

    @Test
    void testGetDeliveryPersonById_ThrowsException() {
        // Arrange
        int id = 1;
        when(deliveryPersonService.findById(id)).thenReturn(null);

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> deliveryPersonController.getDeliveryPersonById(id));
        assertEquals("Delivery person not found with ID: " + id, exception.getMessage());
        verify(deliveryPersonService, times(1)).findById(id);
    }
}
