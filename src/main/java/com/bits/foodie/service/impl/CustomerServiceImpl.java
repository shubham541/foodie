package com.bits.foodie.service.impl;

import com.bits.foodie.dto.AddressDTO;
import com.bits.foodie.dto.CardDTO;
import com.bits.foodie.dto.CustomerDTO;
import com.bits.foodie.dto.payload.LoginRequestDTO;
import com.bits.foodie.mappers.AddressMapper;
import com.bits.foodie.mappers.CardMapper;
import com.bits.foodie.model.Address;
import com.bits.foodie.model.Card;
import com.bits.foodie.model.Customer;
import com.bits.foodie.dto.response.ResponseDTO;
import com.bits.foodie.repository.CustomerRepository;
import com.bits.foodie.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AddressMapper addressMapper;

    @Autowired
    private CardMapper cardMapper;

    @Override
    public ResponseDTO addCustomer(CustomerDTO customerDTO) {

        Customer customer = new Customer(
                customerDTO.getRole(),
                customerDTO.getCustomerName(),
                customerDTO.getCustomerEmail(),
                this.passwordEncoder.encode(customerDTO.getPassword()));
        customerRepository.save(customer);
        return new ResponseDTO("Registration Successful", true);
    }

    @Override
    public ResponseDTO loginCustomer(LoginRequestDTO loginRequestDTO) {
        Customer customer = customerRepository.findByCustomerEmail(loginRequestDTO.getCustomerEmail());
        if (customer != null && loginRequestDTO.getRole().equalsIgnoreCase(customer.getRole())) {
            String password = loginRequestDTO.getPassword();
            String encodedPassword = customer.getPassword();
            boolean isPwdRight = passwordEncoder.matches(password, encodedPassword);
            if (isPwdRight) {
                Optional<Customer> customer1 = customerRepository.findOneByCustomerEmailAndPassword(customer.getCustomerEmail(), customer.getPassword());
                if (customer1.isPresent()) {
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
    public Customer updateCustomer(CustomerDTO customerDTO) {
        Customer customer = customerRepository.findByCustomerEmail(customerDTO.getCustomerEmail());
        if (customer != null) {
            if (customerDTO.getCustomerName() != null) {
                customer.setCustomerName(customerDTO.getCustomerName());
            }
            if (customerDTO.getAddresses() != null) {
                List<Address> addresses = new ArrayList<>();
                for (AddressDTO add : customerDTO.getAddresses()) {
                    addresses.add(addressMapper.mapAddressDTOtoAddress(add));
                }
                customer.setAddresses(addresses);
            }
            if (customerDTO.getSavedPaymentDetails() != null) {
                List<Card> cards = new ArrayList<>();
                for (CardDTO card : customerDTO.getSavedPaymentDetails()) {
                    cards.add(cardMapper.mapCardDTOtoCard(card));
                }
                customer.setSavedPaymentDetails(cards);
            }
            customerRepository.save(customer);
        }
        return customer;
    }

    @Override
    public Customer fetchCustomerById(int customerId) {
        return customerRepository.findByCustomerId(customerId);
    }
}
