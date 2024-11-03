package com.bits.foodie.controller;

import com.bits.foodie.dto.*;
import com.bits.foodie.dto.payload.LoginRequestDTO;
import com.bits.foodie.dto.payload.RegisterRequestDTO;
import com.bits.foodie.dto.response.ResponseDTO;
import com.bits.foodie.model.Admin;
import com.bits.foodie.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/admin")
public class AdminController {

    @Autowired
    AdminService adminService;

    public ResponseDTO saveAdmin(@RequestBody RegisterRequestDTO registerRequestDTO) {
        AdminDTO adminDTO = new AdminDTO(registerRequestDTO.getAdminEmail(), registerRequestDTO.getAdminName(), registerRequestDTO.getPassword(), registerRequestDTO.getRole());
        return adminService.addAdmin(adminDTO);
    }

    public ResponseDTO loginAdmin(@RequestBody LoginRequestDTO loginRequestDTO) {
        return adminService.loginAdmin(loginRequestDTO);
    }

    @PatchMapping(path = "/{adminId}")
    public Admin updateRestaurant(@PathVariable("adminId") int adminId, @RequestBody AdminDTO adminDTO) {
        return adminService.updateAdmin(adminId,adminDTO);
    }
}
