package com.bits.foodie.controller;

import com.bits.foodie.dto.*;
import com.bits.foodie.dto.payload.LoginRequestDTO;
import com.bits.foodie.dto.payload.RegisterRequestDTO;
import com.bits.foodie.dto.response.ResponseDTO;
import com.bits.foodie.model.DeliveryPerson;
import com.bits.foodie.model.Order;
import com.bits.foodie.service.DeliveryPersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/deliveryPerson")
public class DeliveryPersonController {

    @Autowired
    DeliveryPersonService deliveryPersonService;

    public ResponseDTO saveDeliveryPerson(@RequestBody RegisterRequestDTO registerRequestDTO) {
        DeliveryPersonDTO deliveryPersonDTO = new DeliveryPersonDTO(registerRequestDTO.getDeliveryPersonEmail(),
                registerRequestDTO.getDeliveryPersonName(), registerRequestDTO.getPassword(),
                registerRequestDTO.getContactDetails(), registerRequestDTO.getVehicleType(),
                registerRequestDTO.getRole());
        return deliveryPersonService.addDeliveryPerson(deliveryPersonDTO);
    }

    public ResponseDTO loginDeliveryPerson(@RequestBody LoginRequestDTO loginRequestDTO) {
        return deliveryPersonService.loginDeliveryPerson(loginRequestDTO);
    }

    @PatchMapping(path = "/{deliveryPersonId}")
    public DeliveryPerson updateDeliveryPerson(@PathVariable("deliveryPersonId") int deliveryPersonId, @RequestBody DeliveryPersonDTO deliveryPersonDTO) {
        return deliveryPersonService.updateDeliveryPerson(deliveryPersonId, deliveryPersonDTO);
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
}
