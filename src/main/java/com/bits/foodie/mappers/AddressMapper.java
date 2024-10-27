package com.bits.foodie.mappers;

import com.bits.foodie.dto.AddressDTO;
import com.bits.foodie.model.Address;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
public class AddressMapper {

    public Address mapAddressDTOtoAddress(AddressDTO addressDTO) {

        Address address = new Address();
        address.setAddressId(addressDTO.getAddressId());
        address.setFlatNo(addressDTO.getFlatNo());
        address.setLane(addressDTO.getLane());
        address.setLandmark(addressDTO.getLandmark());
        address.setCity(addressDTO.getCity());
        address.setState(addressDTO.getState());
        address.setPinCode(addressDTO.getPinCode());

        return address;
    }

    public AddressDTO mapAddressToAddressDTO(Address address) {

        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setAddressId(address.getAddressId());
        addressDTO.setFlatNo(address.getFlatNo());
        addressDTO.setLane(address.getLane());
        addressDTO.setLandmark(address.getLandmark());
        addressDTO.setCity(address.getCity());
        addressDTO.setState(address.getState());
        addressDTO.setPinCode(address.getPinCode());

        return addressDTO;
    }

}
