package com.bits.foodie.service.impl;

import com.bits.foodie.dto.AdminDTO;
import com.bits.foodie.dto.payload.LoginRequestDTO;
import com.bits.foodie.model.Admin;
import com.bits.foodie.model.Restaurant;
import com.bits.foodie.repository.AdminRepository;
import com.bits.foodie.repository.CustomerRepository;
import com.bits.foodie.repository.DeliveryPersonRepository;
import com.bits.foodie.repository.RestaurantRepository;
import com.bits.foodie.dto.response.ResponseDTO;
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

    @Override
    public ResponseDTO addAdmin(AdminDTO adminDTO) {
        Admin admin = new Admin(
                adminDTO.getAdminName(),
                adminDTO.getAdminEmail(),
                adminDTO.getRole(),
                this.passwordEncoder.encode(adminDTO.getPassword()));
        adminRepository.save(admin);
        return new ResponseDTO("Registration Successful", true);
    }

    @Override
    public ResponseDTO loginAdmin(LoginRequestDTO loginRequestDTO) {
        String msg = "";
        Admin admin = adminRepository.findByAdminEmail(loginRequestDTO.getAdminEmail());
        if (admin != null && loginRequestDTO.getRole().equalsIgnoreCase(admin.getRole())) {
            String password = loginRequestDTO.getPassword();
            String encodedPassword = admin.getPassword();
            boolean isPwdRight = passwordEncoder.matches(password, encodedPassword);
            if (isPwdRight) {
                Optional<Admin> admin1 = adminRepository.findOneByAdminEmailAndPassword(admin.getAdminEmail(), admin.getPassword());
                if (admin1.isPresent()) {
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
    public Admin updateAdmin(int adminId, AdminDTO adminDTO) {
        Admin admin = adminRepository.findByAdminId(adminId);
//        if (admin != null) {
//            if (restaurantDTO.getRestaurantName() != null) {
//                restaurant.setRestaurantName(restaurantDTO.getRestaurantName());
//            }
//            if (restaurantDTO.getRestaurantAdd() != null) {
//                restaurant.setRestaurantAdd(restaurantDTO.getRestaurantAdd());
//            }
//            restaurant.setHoursOfOperation(restaurantDTO.getHoursOfOperation());
//            restaurant.setOpeningHours(restaurantDTO.getOpeningHours());
//            restaurant.setDeliveryZones(restaurantDTO.getDeliveryZones());
//            restaurantRepository.save(restaurant);
//        }
        return admin;
    }
}
