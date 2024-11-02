package com.bits.foodie.mappers;

import com.bits.foodie.dto.OrderDTO;
import com.bits.foodie.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {
    @Autowired
    private CartMapper cartMapper;

    public Order mapOrderDTOtoOrder(OrderDTO orderDTO) {
        Order order = new Order();
        order.setOrderId(orderDTO.getOrderId());
        order.setCart(cartMapper.mapCartDTOtoCart(orderDTO.getCart()));
        order.setCustomerId(orderDTO.getCustomerId());
        order.setRestaurantId(orderDTO.getRestaurantId());
        order.setAmount(orderDTO.getAmount());
        order.setCancelled(orderDTO.isCancelled());
        order.setStatus(orderDTO.getStatus());
        order.setDeliveryTimeInMinutes(orderDTO.getDeliveryTimeInMinutes());
        return order;
    }

    public OrderDTO mapOrderToOrderDTO(Order order) {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setOrderId(order.getOrderId());
        orderDTO.setCart(cartMapper.mapCartToCartDTO(order.getCart()));
        orderDTO.setCustomerId(order.getCustomerId());
        orderDTO.setRestaurantId(order.getRestaurantId());
        orderDTO.setAmount(order.getAmount());
        orderDTO.setCancelled(order.isCancelled());
        orderDTO.setStatus(order.getStatus());
        orderDTO.setDeliveryTimeInMinutes(order.getDeliveryTimeInMinutes());
        return orderDTO;
    }
}
