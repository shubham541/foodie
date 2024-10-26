package com.bits.foodie.controller;

import com.bits.foodie.dto.*;
import com.bits.foodie.response.LoginResponse;
import com.bits.foodie.service.DeliveryPersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class DeliveryPersonController {

    @Autowired
    DeliveryPersonService deliveryPersonService;

    public void saveDeliveryPerson(@RequestBody RegisterDTO registerDTO) {
        DeliveryPersonDTO deliveryPersonDTO = new DeliveryPersonDTO(registerDTO.getDevEmail(), registerDTO.getDevName(),
                registerDTO.getPassword(), registerDTO.getContactDetails(), registerDTO.getVehicleType(), registerDTO.getRole());
        deliveryPersonService.addDeliveryPerson(deliveryPersonDTO);
    }

    public LoginResponse loginDeliveryPerson(@RequestBody LoginDTO loginDTO) {
        return deliveryPersonService.loginDeliveryPerson(loginDTO);
    }
}
