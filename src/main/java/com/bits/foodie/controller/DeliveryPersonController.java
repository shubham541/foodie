package com.bits.foodie.controller;

import com.bits.foodie.dto.*;
import com.bits.foodie.dto.payload.LoginRequestDTO;
import com.bits.foodie.dto.payload.RegisterRequestDTO;
import com.bits.foodie.dto.response.ResponseDTO;
import com.bits.foodie.exception.ResourceNotFoundException;
import com.bits.foodie.model.DeliveryPerson;
import com.bits.foodie.model.Order;
import com.bits.foodie.service.DeliveryPersonService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/deliveryPerson")
public class DeliveryPersonController {

    @Autowired
    DeliveryPersonService deliveryPersonService;

    public ResponseDTO saveDeliveryPerson(@Valid @RequestBody RegisterRequestDTO registerRequestDTO) {
        try{
            DeliveryPersonDTO deliveryPersonDTO = new DeliveryPersonDTO(registerRequestDTO.getDeliveryPersonEmail(),
                    registerRequestDTO.getDeliveryPersonName(), registerRequestDTO.getPassword(),
                    registerRequestDTO.getContactDetails(), registerRequestDTO.getVehicleType(),
                    registerRequestDTO.getRole());
            return deliveryPersonService.addDeliveryPerson(deliveryPersonDTO);
        } catch (Exception e){
            throw new IllegalArgumentException("Failed to register delivery person: " + e.getMessage());
        }
    }

    public ResponseDTO loginDeliveryPerson(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        return deliveryPersonService.loginDeliveryPerson(loginRequestDTO);
    }

    @PatchMapping("/{id}")
    public DeliveryPerson updateDeliveryPerson(@PathVariable int id, @Valid @RequestBody DeliveryPersonDTO deliveryPersonDTO) {
        try {
            DeliveryPerson updatedPerson = deliveryPersonService.updateDeliveryPerson(id, deliveryPersonDTO);
            if (updatedPerson == null) {
                throw new ResourceNotFoundException("Delivery person not found for update with ID: " + id);
            }
            return updatedPerson;
        } catch (ResourceNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new IllegalArgumentException("Failed to update delivery person: " + ex.getMessage());
        }
    }

    @GetMapping(path = "/viewAvailableDeliveries")
    public List<Order> viewAvailableDeliveries() {
        return deliveryPersonService.viewAvailableDeliveries();
    }

    @PatchMapping(path = "/id/{deliveryPersonId}/order/{orderId}/updateOrderStatus")
    public Order updateOrderStatus(@PathVariable("deliveryPersonId") int deliveryPersonId,
                                   @PathVariable("orderId") int orderId, @RequestParam(required = true) String status) {
        return deliveryPersonService.updateOrderStatus(deliveryPersonId, orderId, status);
    }

    @PatchMapping(path = "/id/{deliveryPersonId}/manageDeliveryAvailability")
    public DeliveryPerson manageDeliveryAvailability(@PathVariable("deliveryPersonId") int deliveryPersonId,
                                                     @RequestParam(required = true) boolean availability) {
        return deliveryPersonService.manageDeliveryAvailability(deliveryPersonId, availability);
    }

    public void deactivateDeliveryPerson(int userId) {
        deliveryPersonService.deactivateDeliveryPerson(userId);
    }

    @GetMapping("/{id}")
    public DeliveryPerson getDeliveryPersonById(@PathVariable int id) {
        DeliveryPerson deliveryPerson = deliveryPersonService.findById(id);
        if (deliveryPerson == null) {
            throw new ResourceNotFoundException("Delivery person not found with ID: " + id);
        }
        return deliveryPerson;
    }
}
