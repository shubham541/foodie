package com.bits.foodie.service;

import com.bits.foodie.domain.User;
import com.bits.foodie.dto.AuthDtos.UserDTO;
import com.bits.foodie.dto.CustomerDtos.ProfileUpdate;
import com.bits.foodie.exception.ApiException;
import com.bits.foodie.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerService {

    private final UserRepository users;

    public CustomerService(UserRepository users) {
        this.users = users;
    }

    @Transactional
    public UserDTO updateProfile(Long userId, ProfileUpdate update) {
        User u = users.findById(userId).orElseThrow(() -> ApiException.unauthorized("Please log in again."));
        if (update.name() != null && !update.name().isBlank()) u.setName(update.name().trim());
        if (update.phone() != null) u.setPhone(update.phone());
        return DtoMapper.user(u);
    }
}
