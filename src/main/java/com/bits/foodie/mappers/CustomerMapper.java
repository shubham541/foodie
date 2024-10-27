package com.bits.foodie.mappers;

import com.bits.foodie.dto.AddressDTO;
import com.bits.foodie.dto.CardDTO;
import com.bits.foodie.dto.CartDTO;
import com.bits.foodie.dto.CustomerDTO;
import com.bits.foodie.model.Address;
import com.bits.foodie.model.Card;
import com.bits.foodie.model.Cart;
import com.bits.foodie.model.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CustomerMapper {
    @Autowired
    AddressMapper addressMapper;

    @Autowired
    CardMapper cardMapper;

    @Autowired
    CartMapper cartMapper;

    public CustomerDTO mapCustomerToCustomerDTO(Customer customer) {
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setCustomerName(customer.getCustomerName());
        customerDTO.setCustomerEmail(customer.getCustomerEmail());
        customerDTO.setPassword(customer.getPassword());
        customerDTO.setRole(customer.getRole());
        customerDTO.setCart(cartMapper.mapCartToCartDTO(customer.getCart()));
        customerDTO.setActive(customer.isActive());

        if (customer.getAddresses() != null) {
            List<AddressDTO> addressDTOS = new ArrayList<>();
            for (Address add : customer.getAddresses()) {
                addressDTOS.add(addressMapper.mapAddressToAddressDTO(add));
            }
            customerDTO.setAddresses(addressDTOS);
        }
        if (customer.getSavedPaymentDetails() != null) {
            List<CardDTO> cardDTOS = new ArrayList<>();
            for (Card card : customer.getSavedPaymentDetails()) {
                cardDTOS.add(cardMapper.mapCardToCardDTO(card));
            }
            customerDTO.setSavedPaymentDetails(cardDTOS);
        }
        if (customer.getHistory() != null) {
            List<CartDTO> cartDTOs = new ArrayList<>();
            for (Cart cart : customer.getHistory()) {
                cartDTOs.add(cartMapper.mapCartToCartDTO(cart));
            }
            customerDTO.setHistory(cartDTOs);
        }

        return customerDTO;

    }

    public Customer mapCustomerDTOToCustomer(CustomerDTO customerDTO) {
        Customer customer = new Customer();
        customer.setCustomerName(customerDTO.getCustomerName());
        customer.setCustomerEmail(customerDTO.getCustomerEmail());
        customer.setPassword(customerDTO.getPassword());
        customer.setRole(customerDTO.getRole());
        customer.setCart(cartMapper.mapCartDTOtoCart(customerDTO.getCart()));
        customer.setActive(customerDTO.isActive());

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
        if (customerDTO.getHistory() != null) {
            List<Cart> carts = new ArrayList<>();
            for (CartDTO cart : customerDTO.getHistory()) {
                carts.add(cartMapper.mapCartDTOtoCart(cart));
            }
            customer.setHistory(carts);
        }

        return customer;

    }
}
