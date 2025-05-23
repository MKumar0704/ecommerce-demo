package com.ecom.cartservice.dto.cart;

import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class CartItemDto {
    private UUID cartItemId;
    private UUID productId;
    private Integer quantity;
}