package com.bits.foodie.mappers;

import com.bits.foodie.dto.CartDTO;
import com.bits.foodie.model.Cart;
import org.springframework.stereotype.Component;

@Component
public class CartMapper {

    public CartDTO mapCartToCartDTO(Cart cart) {
        CartDTO cartDTO = new CartDTO();
        cartDTO.setCartId(cart.getCartId());
        cartDTO.setRestId(cart.getRestId());
        cartDTO.setPrice(cart.getPrice());
        cartDTO.setItems(cart.getItems());
        return cartDTO;

    }

    public Cart mapCartDTOtoCart(CartDTO cartDTO) {
        Cart cart = new Cart();
        cart.setCartId(cartDTO.getCartId());
        cart.setRestId(cartDTO.getRestId());
        cart.setPrice(cartDTO.getPrice());
        cart.setItems(cartDTO.getItems());
        return cart;

    }
}

