package com.bits.foodie.mappers;

import com.bits.foodie.dto.CartItemDTO;
import com.bits.foodie.model.CartItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CartItemMapper {

    @Autowired
    ItemMapper itemMapper;

    public CartItemDTO mapCardItemToCardItemDTO(CartItem cartItem) {
        CartItemDTO cartItemDTO = new CartItemDTO();
        cartItemDTO.setItem(itemMapper.mapItemToItemDTO(cartItem.getItem()));
        cartItemDTO.setQuantity(cartItem.getQuantity());
        return cartItemDTO;

    }

    public CartItem mapCardItemDTOtoCardItem(CartItemDTO cartItemDTO) {
        CartItem cartItem = new CartItem();
        cartItem.setItem(itemMapper.mapItemDTOtoItem(cartItemDTO.getItem()));
        cartItem.setQuantity(cartItemDTO.getQuantity());
        return cartItem;

    }
}
