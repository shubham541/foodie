package com.bits.foodie.service.impl;

import com.bits.foodie.dto.AdminDTO;
import com.bits.foodie.dto.LoginDTO;
import com.bits.foodie.model.Admin;
import com.bits.foodie.model.Customer;
import com.bits.foodie.repository.AdminRepository;
import com.bits.foodie.repository.CustomerRepository;
import com.bits.foodie.repository.DeliveryPersonRepository;
import com.bits.foodie.repository.RestaurantRepository;
import com.bits.foodie.response.LoginResponse;
import com.bits.foodie.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private DeliveryPersonRepository deliveryPersonRepository;
    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    //@Override
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

    @Override
    public String addAdmin(AdminDTO adminDTO) {
        Admin admin = new Admin(
                adminDTO.getAdminName(),
                adminDTO.getAdminEmail(),
                adminDTO.getRole(),
                this.passwordEncoder.encode(adminDTO.getPassword()));
        adminRepository.save(admin);
        return admin.getAdminName();
    }
}
