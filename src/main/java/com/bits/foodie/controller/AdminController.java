package com.bits.foodie.controller;

import com.bits.foodie.dto.*;
import com.bits.foodie.response.LoginResponse;
import com.bits.foodie.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class AdminController {

    @Autowired
    AdminService adminService;

    public void saveAdmin(@RequestBody RegisterDTO registerDTO) {
        AdminDTO adminDTO = new AdminDTO(registerDTO.getAdminEmail(), registerDTO.getAdminName(), registerDTO.getPassword(), registerDTO.getRole());
        adminService.addAdmin(adminDTO);
    }

    public LoginResponse loginAdmin(@RequestBody LoginDTO loginDTO) {
        return adminService.loginAdmin(loginDTO);
    }
}
