package com.bits.foodie.service;

import com.bits.foodie.dto.AdminDTO;
import com.bits.foodie.dto.payload.LoginRequestDTO;
import com.bits.foodie.dto.response.PlatformActivityDTO;
import com.bits.foodie.dto.response.ResponseDTO;
import com.bits.foodie.model.Order;

import java.util.List;
import java.util.Map;

public interface AdminService {
    ResponseDTO  addAdmin(AdminDTO adminDTO) ;

    ResponseDTO loginAdmin(LoginRequestDTO loginRequestDTO);

    List<Order> viewAllOrders();

    Order cancelOrder(int orderId);

    Order rescheduleOrder(int orderId, String status);

    Map<String, String> generateReports();

    PlatformActivityDTO getPlatformActivitySummary();
}
