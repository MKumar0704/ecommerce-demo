package com.ecom.cartservice.mapper;


import com.ecom.cartservice.dto.cart.CartItemDto;
import com.ecom.cartservice.dto.cart.CartResponseDto;
import com.ecom.cartservice.model.cart.Cart;

public class ModelMapper {

    public static CartResponseDto toDto(Cart cart){
        return CartResponseDto.builder()
                .cart_id(cart.getId())
                .items(
                        cart.getCartItems().stream().map(
                                items-> CartItemDto.builder()
                                        .cartItemId(items.getId())
                                        .productId(items.getProductId())
                                        .quantity(items.getQuantity())
                                        .build()

                        ).toList()
                ).totalPrice(0.0)
                .build();
    }

}
