package com.bits.foodie.service;

import com.bits.foodie.dto.DeliveryPersonDTO;
import com.bits.foodie.dto.payload.LoginRequestDTO;
import com.bits.foodie.dto.response.ResponseDTO;
import com.bits.foodie.model.DeliveryPerson;
import com.bits.foodie.model.Order;

import java.util.List;

public interface DeliveryPersonService {
    ResponseDTO addDeliveryPerson(DeliveryPersonDTO deliveryPersonDTO);

    ResponseDTO loginDeliveryPerson(LoginRequestDTO loginRequestDTO);

    DeliveryPerson updateDeliveryPerson(int deliveryPersonId, DeliveryPersonDTO deliveryPersonDTO);

    List<Order> viewAvailableDeliveries();

    Order updateOrderStatus(int deliveryPersonId, int orderId, String status);

    DeliveryPerson manageDeliveryAvailability(int deliveryPersonId, boolean availability);

    void deactivateDeliveryPerson(int userId);
}
