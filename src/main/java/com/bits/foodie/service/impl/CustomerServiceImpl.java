package com.bits.foodie.service.impl;

import com.bits.foodie.dto.CartDTO;
import com.bits.foodie.dto.CustomerDTO;
import com.bits.foodie.dto.payload.LoginRequestDTO;
import com.bits.foodie.mappers.AddressMapper;
import com.bits.foodie.mappers.CardMapper;
import com.bits.foodie.mappers.CartMapper;
import com.bits.foodie.model.*;
import com.bits.foodie.dto.response.ResponseDTO;
import com.bits.foodie.repository.CustomerRepository;
import com.bits.foodie.repository.OrderRepository;
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
    OrderRepository orderRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AddressMapper addressMapper;
    @Autowired
    private CartMapper cartMapper;

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
            if (!customerDTO.getAddresses().isEmpty()) {
                List<Address> addresses = new ArrayList<>();
                customerDTO.getAddresses().forEach(add -> addresses.add(addressMapper.mapAddressDTOtoAddress(add)));
                customer.setAddresses(addresses);
            } else {
                customer.setAddresses(new ArrayList<>());
            }
            if (!customerDTO.getSavedPaymentDetails().isEmpty()) {
                List<Card> cards = new ArrayList<>();
                customerDTO.getSavedPaymentDetails().forEach(card -> cards.add(cardMapper.mapCardDTOtoCard(card)));
                customer.setSavedPaymentDetails(cards);
            } else {
                customer.setSavedPaymentDetails(new ArrayList<>());
            }
            customerRepository.save(customer);
        }
        return customer;
    }

    @Override
    public Customer fetchCustomerById(int customerId) {
        return customerRepository.findByCustomerId(customerId);
    }

    @Override
    public Customer updateCart(int customerId, CartDTO cartDTO) {
        cartDTO.updatePrice();
        Customer customer = customerRepository.findByCustomerId(customerId);
        Cart cart = cartMapper.mapCartDTOtoCart(cartDTO);
        cart.setCustomer(customer);
        customer.setCart(cart);
        customerRepository.save(customer);
        return customer;
    }

    public Order placeOrder(int customerId) {
        Customer customer = customerRepository.findByCustomerId(customerId);
        Cart cart = customer.getCart();
        if (cart.getRestId() > 0 && cart.getPrice() > 0) {
            Order order = new Order();
            order.setCart(cart);
            order.setCustomerId(customerId);
            order.setRestaurantId(cart.getRestId());
            order.setAmount(cart.getPrice());
            order.setStatus("Order Placed");
            orderRepository.save(order);

            List<Cart> history = customer.getHistory();
            history.add(cart);
            customer.setHistory(history);
            customer.setCart(null);
            return order;
        }
        return null;
    }
}
