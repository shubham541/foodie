package com.bits.foodie.service.impl;

import com.bits.foodie.dto.DeliveryPersonDTO;
import com.bits.foodie.dto.payload.LoginRequestDTO;
import com.bits.foodie.model.DeliveryPerson;
import com.bits.foodie.repository.CustomerRepository;
import com.bits.foodie.repository.DeliveryPersonRepository;
import com.bits.foodie.dto.response.ResponseDTO;
import com.bits.foodie.service.DeliveryPersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DeliveryPersonServiceImpl implements DeliveryPersonService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private DeliveryPersonRepository deliveryPersonRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public ResponseDTO addDeliveryPerson(DeliveryPersonDTO deliveryPersonDTO) {
        DeliveryPerson deliveryPerson = new DeliveryPerson(
                deliveryPersonDTO.getDeliveryPersonEmail(),
                deliveryPersonDTO.getDeliveryPersonName(),
                this.passwordEncoder.encode(deliveryPersonDTO.getPassword()),
                deliveryPersonDTO.getContactDetails(),
                deliveryPersonDTO.getVehicleType(),
                deliveryPersonDTO.getRole());
        deliveryPersonRepository.save(deliveryPerson);
        return new ResponseDTO("Registration Successful", true);
    }

    @Override
    public ResponseDTO loginDeliveryPerson(LoginRequestDTO loginRequestDTO) {
        String msg = "";
        DeliveryPerson deliveryPerson = deliveryPersonRepository.findByDeliveryPersonEmail(loginRequestDTO.getDeliveryPersonEmail());
        if (deliveryPerson != null && loginRequestDTO.getRole().equalsIgnoreCase(deliveryPerson.getRole())) {
            String password = loginRequestDTO.getPassword();
            String encodedPassword = deliveryPerson.getPassword();
            boolean isPwdRight = passwordEncoder.matches(password, encodedPassword);
            if (isPwdRight) {
                Optional<DeliveryPerson> deliveryPerson1 = deliveryPersonRepository.findOneByDeliveryPersonEmailAndPassword(deliveryPerson.getDeliveryPersonEmail(), deliveryPerson.getPassword());
                if (deliveryPerson1.isPresent()) {
                    return new ResponseDTO("Login Success", true);
                } else {
                    return new ResponseDTO("Login failed", false);
                }
            } else {
                return new ResponseDTO("Password Not Match", false);
            }
        } else {
            return new ResponseDTO("Email not exists", false);
        }
    }
}
