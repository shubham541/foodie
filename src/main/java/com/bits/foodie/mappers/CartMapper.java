package com.bits.foodie.mappers;

import com.bits.foodie.dto.CartDTO;
import com.bits.foodie.dto.CartItemDTO;
import com.bits.foodie.dto.ItemDTO;
import com.bits.foodie.model.Cart;
import com.bits.foodie.model.CartItem;
import com.bits.foodie.model.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CartMapper {

    @Autowired
    CartItemMapper cartItemMapper;


    public CartDTO mapCartToCartDTO(Cart cart) {
        CartDTO cartDTO = new CartDTO();
        cartDTO.setCartId(cart.getCartId());
        cartDTO.setRestId(cart.getRestId());
        cartDTO.setPrice(cart.getPrice());
        if (cart.getCartItems() != null) {
            List<CartItemDTO> cartItemDTOS = cart.getCartItems().stream()
                    .map(cartItemMapper::mapCardItemToCardItemDTO)
                    .toList();
            cartDTO.setCartItems(cartItemDTOS);
        }
        return cartDTO;

    }

    public Cart mapCartDTOtoCart(CartDTO cartDTO) {
        Cart cart = new Cart();
        cart.setCartId(cartDTO.getCartId());
        cart.setRestId(cartDTO.getRestId());
        cart.setPrice(cartDTO.getPrice());
        if (cartDTO.getCartItems() != null) {
            List<CartItem> cartItems = cartDTO.getCartItems().stream()
                    .map(cartItemDTO -> {
                        CartItem cartItem = cartItemMapper.mapCardItemDTOtoCardItem(cartItemDTO);
                        cartItem.setCart(cart);  // Set the back-reference to Menu
                        return cartItem;
                    })
                    .collect(Collectors.toList());
            cart.setCartItems(cartItems);
        }
        return cart;

    }
}

