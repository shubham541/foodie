package com.bits.foodie.service.impl;

import com.bits.foodie.dto.CustomerDTO;
import com.bits.foodie.dto.DeliveryPersonDTO;
import com.bits.foodie.dto.LoginDTO;
import com.bits.foodie.model.Customer;
import com.bits.foodie.model.DeliveryPerson;
import com.bits.foodie.repository.CustomerRepository;
import com.bits.foodie.repository.DeliveryPersonRepository;
import com.bits.foodie.response.LoginResponse;
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
    public String addDeliveryPerson(DeliveryPersonDTO deliveryPersonDTO) {
        DeliveryPerson deliveryPerson = new DeliveryPerson(
                deliveryPersonDTO.getDeliveryPersonEmail(),
                deliveryPersonDTO.getDeliveryPersonName(),
                this.passwordEncoder.encode(deliveryPersonDTO.getPassword()),
                deliveryPersonDTO.getContactDetails(),
                deliveryPersonDTO.getVehicleType(),
                deliveryPersonDTO.getRole());
        deliveryPersonRepository.save(deliveryPerson);
        return deliveryPerson.getDeliveryPersonName();
    }

    @Override
    public LoginResponse loginDeliveryPerson(LoginDTO loginDTO) {
        String msg = "";
        DeliveryPerson deliveryPerson = deliveryPersonRepository.findByDeliveryPersonEmail(loginDTO.getDeliveryPersonEmail());
        if (deliveryPerson != null && loginDTO.getRole().equalsIgnoreCase(deliveryPerson.getRole())) {
            String password = loginDTO.getPassword();
            String encodedPassword = deliveryPerson.getPassword();
            boolean isPwdRight = passwordEncoder.matches(password, encodedPassword);
            if (isPwdRight) {
                Optional<DeliveryPerson> deliveryPerson1 = deliveryPersonRepository.findOneByDeliveryPersonEmailAndPassword(deliveryPerson.getDeliveryPersonEmail(), deliveryPerson.getPassword());
                if (deliveryPerson1.isPresent()) {
                    return new LoginResponse("Login Success", true);
                } else {
                    return new LoginResponse("Login failed", false);
                }
            } else {
                return new LoginResponse("Password Not Match", false);
            }
        } else {
            return new LoginResponse("Email not exists", false);
        }
    }
}
