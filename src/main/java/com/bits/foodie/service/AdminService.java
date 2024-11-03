package com.bits.foodie.service;

import com.bits.foodie.dto.AdminDTO;
import com.bits.foodie.dto.payload.LoginRequestDTO;
import com.bits.foodie.dto.response.ResponseDTO;
import com.bits.foodie.model.Admin;

public interface AdminService {
    ResponseDTO  addAdmin(AdminDTO adminDTO) ;

    ResponseDTO loginAdmin(LoginRequestDTO loginRequestDTO);

    Admin updateAdmin(int adminId, AdminDTO adminDTO);
}
