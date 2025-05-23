package com.ecom.cartservice.dto.cart;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Builder
@Data
@Getter
@Setter
public class CartResponseDto {

    private UUID cart_id;
    private List<CartItemDto> items;
    private Double totalPrice;

}
