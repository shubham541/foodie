package com.bits.foodie.service;

import com.bits.foodie.dto.DeliveryPersonDTO;
import com.bits.foodie.dto.payload.LoginRequestDTO;
import com.bits.foodie.dto.response.ResponseDTO;

public interface DeliveryPersonService {
    ResponseDTO addDeliveryPerson(DeliveryPersonDTO deliveryPersonDTO);

    ResponseDTO loginDeliveryPerson(LoginRequestDTO loginRequestDTO);
}
