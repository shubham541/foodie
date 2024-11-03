package com.bits.foodie.service.impl;

import com.bits.foodie.dto.DeliveryPersonDTO;
import com.bits.foodie.dto.payload.LoginRequestDTO;
import com.bits.foodie.model.DeliveryPerson;
import com.bits.foodie.model.Order;
import com.bits.foodie.model.Restaurant;
import com.bits.foodie.repository.CustomerRepository;
import com.bits.foodie.repository.DeliveryPersonRepository;
import com.bits.foodie.dto.response.ResponseDTO;
import com.bits.foodie.repository.OrderRepository;
import com.bits.foodie.service.DeliveryPersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.bits.foodie.utility.FoodieConstants.ORDER_PLACED;

@Service
public class DeliveryPersonServiceImpl implements DeliveryPersonService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private OrderRepository orderRepository;

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

    @Override
    public DeliveryPerson updateDeliveryPerson(int deliveryPersonId, DeliveryPersonDTO deliveryPersonDTO) {
        DeliveryPerson deliveryPerson = deliveryPersonRepository.findByDeliveryPersonId(deliveryPersonId);
        if (deliveryPerson != null) {
            if (deliveryPersonDTO.getDeliveryPersonName() != null) {
                deliveryPerson.setDeliveryPersonName(deliveryPersonDTO.getDeliveryPersonName());
            }
            if (deliveryPersonDTO.getContactDetails() != null) {
                deliveryPerson.setContactDetails(deliveryPersonDTO.getContactDetails());
            }
            if (deliveryPersonDTO.getVehicleType() != null) {
                deliveryPerson.setVehicleType(deliveryPersonDTO.getVehicleType());
            }
            deliveryPersonRepository.save(deliveryPerson);
        }
        return deliveryPerson;
    }

    @Override
    public List<Order> viewAvailableDeliveries() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream().filter(order -> order.getStatus().equals(ORDER_PLACED)).toList();
    }

    @Override
    public Order updateOrderStatus(int deliveryPersonId, int orderId, String status) {
        Order order = orderRepository.findByOrderId(orderId);
        if (order != null) {
            order.setStatus(status);
            orderRepository.save(order);
        }
        return order;

    }

    @Override
    public DeliveryPerson manageDeliveryAvailability(int deliveryPersonId, boolean availability) {
        DeliveryPerson deliveryPerson = deliveryPersonRepository.findByDeliveryPersonId(deliveryPersonId);
        deliveryPerson.setDevAvailable(availability);
        deliveryPersonRepository.save(deliveryPerson);
        return deliveryPerson;
    }
}
