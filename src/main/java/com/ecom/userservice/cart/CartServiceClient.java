package com.ecom.userservice.cart;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

@FeignClient(name = "cart-service", url="${cart.service.url}")
public interface CartServiceClient {

    @PostMapping("/merge")
    void mergeCart(@RequestHeader("X-User-Id")UUID userId,
                   @RequestHeader("X-Guest-Id")UUID guestId);


}
