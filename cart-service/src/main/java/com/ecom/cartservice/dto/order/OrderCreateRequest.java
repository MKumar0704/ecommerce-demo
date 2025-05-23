package com.ecom.cartservice.dto.order;

import com.ecom.cartservice.model.order.OrderItems;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Data
@Getter
@Setter
public class OrderCreateRequest {

    private UUID userId;

    private List<OrderItems> items;
}
