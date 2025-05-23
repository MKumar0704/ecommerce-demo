package com.ecom.cartservice.dto.cart;

import lombok.*;

import java.util.UUID;


@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddItemRequest {

    private UUID productId;

    private Integer quantity;
}
