package com.bits.foodie.service;

import com.bits.foodie.dto.AdminDTO;
import com.bits.foodie.dto.LoginDTO;
import com.bits.foodie.response.LoginResponse;

public interface AdminService {
    public String addAdmin(AdminDTO adminDTO) ;

    LoginResponse loginAdmin(LoginDTO loginDTO);
}
