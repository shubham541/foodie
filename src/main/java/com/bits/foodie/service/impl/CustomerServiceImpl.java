package com.bits.foodie.service.impl;

import com.bits.foodie.dto.CustomerDTO;
import com.bits.foodie.dto.LoginDTO;
import com.bits.foodie.model.Customer;
import com.bits.foodie.response.LoginResponse;
import com.bits.foodie.repository.CustomerRepository;
import com.bits.foodie.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public String addCustomer(CustomerDTO customerDTO) {

        Customer customer = new Customer(
                customerDTO.getRole(),
                customerDTO.getCustomerName(),
                customerDTO.getCustomerEmail(),
                this.passwordEncoder.encode(customerDTO.getPassword()));
        customerRepository.save(customer);
        return customer.getCustomerName();
    }

    @Override
    public LoginResponse loginCustomer(LoginDTO loginDTO) {
        String msg = "";
        Customer customer = customerRepository.findByCustomerEmail(loginDTO.getCustomerEmail());
        if (customer != null && loginDTO.getRole().equals(customer.getRole())) {
            String password = loginDTO.getPassword();
            String encodedPassword = customer.getPassword();
            boolean isPwdRight = passwordEncoder.matches(password, encodedPassword);
            if (isPwdRight) {
                Optional<Customer> customer1 = customerRepository.findOneByCustomerEmailAndPassword(customer.getCustomerEmail(), customer.getPassword());
                if (customer1.isPresent()) {
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
