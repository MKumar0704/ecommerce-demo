package com.ecom.cartservice.dto.cart;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Data
@Getter
@Setter
public class UpdateItemRequest {

    private UUID productId;

    private int quantity;

}
