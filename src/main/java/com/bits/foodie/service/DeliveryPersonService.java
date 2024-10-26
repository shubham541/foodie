package com.bits.foodie.service;

import com.bits.foodie.dto.DeliveryPersonDTO;
import com.bits.foodie.dto.LoginDTO;
import com.bits.foodie.response.LoginResponse;

public interface DeliveryPersonService {
    String addDeliveryPerson(DeliveryPersonDTO deliveryPersonDTO);

    LoginResponse loginDeliveryPerson(LoginDTO loginDTO);
}
