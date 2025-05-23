package com.ecom.cartservice.controller;

import com.ecom.cartservice.dto.cart.AddItemRequest;
import com.ecom.cartservice.dto.cart.CartItemDto;
import com.ecom.cartservice.dto.cart.CartResponseDto;
import com.ecom.cartservice.dto.cart.UpdateItemRequest;
import com.ecom.cartservice.repository.cart.CartRepository;
import com.ecom.cartservice.service.CartService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class CartController {


    private static final Logger log = LoggerFactory.getLogger(CartController.class);
    private final CartService cartService;

    public CartController(CartRepository cartRepository, CartService cartService) {
        this.cartService = cartService;
    }


    @GetMapping("/cart")
    public ResponseEntity<?> getCart(@RequestHeader(value = "X-User-Id", required = false) UUID userId, @RequestHeader(value = "X-Guest-Id", required = false) UUID guestId) {
        CartResponseDto cart = cartService.findById(userId, guestId);
        log.info("cart received from the service layer {{{{{{{{---------------{}", cart.getItems().stream().map(CartItemDto::getProductId));
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/cart")
    public ResponseEntity<CartResponseDto> addItem(@RequestHeader(value = "X-User-Id", required = false) UUID userId
            , @RequestHeader(value = "X-Guest-Id", required = false) UUID guestId
            , @RequestBody AddItemRequest addItemRequest) {
        CartResponseDto cart = cartService.addItem(userId, guestId, addItemRequest);
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/cart/item")
    public ResponseEntity<CartResponseDto> updateItem(@RequestHeader(value = "X-User-Id", required = false) UUID userId
            , @RequestHeader(value = "X-Guest-Id", required = false) UUID guestId
            , @RequestBody UpdateItemRequest updateItemRequest){
        CartResponseDto updatedCart = cartService.updateItem(userId,guestId,updateItemRequest);
        return ResponseEntity.ok(updatedCart);
    }


    @DeleteMapping("/item/{productId}")
    public ResponseEntity<?> removeItem(
            @RequestHeader(value = "X-User-Id", required = false) UUID userId
            , @RequestHeader(value = "X-Guest-Id", required = false) UUID guestId
            , @PathVariable UUID productId
    ){
        cartService.removeItem(userId,guestId,productId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/clear")
    public ResponseEntity<?> clearCart(
            @RequestHeader(value = "X-User-Id", required = false) UUID userId
            , @RequestHeader(value = "X-Guest-Id", required = false) UUID guestId
    ){
        cartService.clearCart(userId,guestId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/merge")
    public ResponseEntity<?> mergeCart(
            @RequestHeader(value = "X-User-Id") UUID userId
            , @RequestHeader(value = "X-Guest-Id") UUID guestId
    ){

        if(userId==null || guestId==null){
            throw new RuntimeException("Both userId and guest must be present to merge the cart");
        }

        cartService.mergeCart(userId,guestId);
        return ResponseEntity.ok().build();

    }


}
