package com.bits.foodie.mappers;

import com.bits.foodie.dto.DeliveryPersonDTO;
import com.bits.foodie.model.DeliveryPerson;
import org.springframework.stereotype.Component;

@Component
public class DeliveryPersonMapper {

    public static DeliveryPerson mapDeliveryPersonDTOtoDeliveryPerson(DeliveryPersonDTO deliveryPersonDTO) {
        DeliveryPerson deliveryPerson = new DeliveryPerson();
        deliveryPerson.setDeliveryPersonEmail(deliveryPersonDTO.getDeliveryPersonEmail());
        deliveryPerson.setDeliveryPersonName(deliveryPersonDTO.getDeliveryPersonName());
        deliveryPerson.setPassword(deliveryPersonDTO.getPassword());
        deliveryPerson.setContactDetails(deliveryPersonDTO.getContactDetails());
        deliveryPerson.setVehicleType(deliveryPersonDTO.getVehicleType());
        deliveryPerson.setDevAvailable(deliveryPersonDTO.getDevAvailable());
        deliveryPerson.setRole(deliveryPersonDTO.getRole());
        deliveryPerson.setActive(deliveryPersonDTO.isActive());
        return deliveryPerson;
    }

    // Convert DeliveryPerson Entity to DeliveryPersonDTO
    public static DeliveryPersonDTO mapDeliveryPersonToDeliveryPersonDTO(DeliveryPerson deliveryPerson) {
        DeliveryPersonDTO deliveryPersonDTO = new DeliveryPersonDTO();
        deliveryPersonDTO.setDeliveryPersonEmail(deliveryPerson.getDeliveryPersonEmail());
        deliveryPersonDTO.setDeliveryPersonName(deliveryPerson.getDeliveryPersonName());
        deliveryPersonDTO.setPassword(deliveryPerson.getPassword());
        deliveryPersonDTO.setContactDetails(deliveryPerson.getContactDetails());
        deliveryPersonDTO.setVehicleType(deliveryPerson.getVehicleType());
        deliveryPersonDTO.setDevAvailable(deliveryPerson.getDevAvailable());
        deliveryPersonDTO.setRole(deliveryPerson.getRole());
        deliveryPersonDTO.setActive(deliveryPerson.isActive());
        return deliveryPersonDTO;
    }

}
