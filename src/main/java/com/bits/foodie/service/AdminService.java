package com.bits.foodie.service;

import com.bits.foodie.dto.AdminDTO;
import com.bits.foodie.dto.payload.LoginRequestDTO;
import com.bits.foodie.dto.response.ResponseDTO;

public interface AdminService {
    ResponseDTO  addAdmin(AdminDTO adminDTO) ;

    ResponseDTO loginAdmin(LoginRequestDTO loginRequestDTO);
}
