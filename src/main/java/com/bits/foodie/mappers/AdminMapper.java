package com.bits.foodie.mappers;

import com.bits.foodie.dto.AdminDTO;
import com.bits.foodie.model.Admin;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
public class AdminMapper {
    public Admin AdminDTOtoAdmin(AdminDTO adminDTO) {

        Admin admin=new Admin();
        admin.setPassword(adminDTO.getPassword());
        admin.setAdminEmail(adminDTO.getAdminEmail());
        admin.setAdminName(adminDTO.getAdminName());
        admin.setRole(adminDTO.getRole());

        return admin;
    }

    public AdminDTO AdminToAdminDTO(Admin admin) {

        AdminDTO adminDTO=new AdminDTO();
        adminDTO.setPassword(admin.getPassword());
        adminDTO.setAdminEmail(admin.getAdminEmail());
        adminDTO.setAdminName(admin.getAdminName());
        adminDTO.setRole(admin.getRole());

        return adminDTO;
    }
}
