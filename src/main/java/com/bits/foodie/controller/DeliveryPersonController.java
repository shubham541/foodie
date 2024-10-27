package com.bits.foodie.controller;

import com.bits.foodie.dto.*;
import com.bits.foodie.dto.payload.LoginRequestDTO;
import com.bits.foodie.dto.payload.RegisterRequestDTO;
import com.bits.foodie.dto.response.ResponseDTO;
import com.bits.foodie.service.DeliveryPersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class DeliveryPersonController {

    @Autowired
    DeliveryPersonService deliveryPersonService;

    public ResponseDTO saveDeliveryPerson(@RequestBody RegisterRequestDTO registerRequestDTO) {
        DeliveryPersonDTO deliveryPersonDTO = new DeliveryPersonDTO(registerRequestDTO.getDevEmail(), registerRequestDTO.getDevName(),
                registerRequestDTO.getPassword(), registerRequestDTO.getContactDetails(), registerRequestDTO.getVehicleType(), registerRequestDTO.getRole());
        return deliveryPersonService.addDeliveryPerson(deliveryPersonDTO);
    }

    public ResponseDTO loginDeliveryPerson(@RequestBody LoginRequestDTO loginRequestDTO) {
        return deliveryPersonService.loginDeliveryPerson(loginRequestDTO);
    }
}
