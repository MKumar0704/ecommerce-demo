package com.ecom.cartservice.service;

import com.ecom.cartservice.dto.cart.AddItemRequest;
import com.ecom.cartservice.dto.cart.CartResponseDto;
import com.ecom.cartservice.dto.cart.UpdateItemRequest;
import com.ecom.cartservice.mapper.ModelMapper;
import com.ecom.cartservice.model.cart.Cart;
import com.ecom.cartservice.model.cart.CartItems;
import com.ecom.cartservice.repository.cart.CartRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

@Service
public class CartService {

    private static final Logger log = LoggerFactory.getLogger(CartService.class);
    private final CartRepository cartRepository;

    public CartService(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

//    public void getCartById(UUID id) {
//        cartRepository.findByUserIdOrGuestId(id);
//    }


    public CartResponseDto addItem(UUID userId, UUID guestId, AddItemRequest addItemRequest){

        log.info("VALUES RECEIVED -------USERID:{}, GUESTID:{}, PRODUCTID:{}, QUANTITY:{}",userId,guestId,addItemRequest.getProductId(),addItemRequest.getQuantity());
        log.info("FETCHING FROM DATABASE");
        Optional<Cart> optionalCart = cartRepository.findByUserIdOrGuestId(userId, guestId);
        log.info("FETCHED VALUES AND CART IS ALREADY PRESENT OR NOT :::: {}",optionalCart.isPresent());
        Cart cart = optionalCart.orElseGet(() -> Cart.builder()
                .userId(userId)
                .guestId(guestId)
                .cartItems(new ArrayList<>())
                .build()
        );


        boolean itemUpdated = false;

        for (CartItems item : cart.getCartItems()) {
            if (item.getProductId().equals(addItemRequest.getProductId())) {
                item.setQuantity(item.getQuantity() + addItemRequest.getQuantity());
                itemUpdated = true;
                break;
            }
        }

        if (!itemUpdated) {
            CartItems newItem = new CartItems();
            newItem.setProductId(addItemRequest.getProductId());
            newItem.setQuantity(addItemRequest.getQuantity());
            newItem.setCart(cart); // Important: set bidirectional relationship
            cart.getCartItems().add(newItem);
            log.info("ADDING NEW PRODUCT: {} QUANTITY: {}", newItem.getProductId(), newItem.getQuantity());
        }


//        optionalCart.ifPresentOrElse(value -> {
//            value.getCartItems().stream().filter(cartItem ->
//                    cartItem.getProductId().equals(addItemRequest.getProductId())
//            ).findFirst().ifPresent(cartItem -> cartItem.setQuantity(cartItem.getQuantity() + addItemRequest.getQuantity()));
//        },()->{
//            CartItems newItem = new CartItems();
//            newItem.setProductId(addItemRequest.getProductId());
//            newItem.setQuantity(addItemRequest.getQuantity());
////            newItem.setCart(cart);
//            log.info("ADDING PRODUCT AND QUANTITY {} {}", newItem.getProductId(),newItem.getQuantity());
//            cart.getCartItems().add(newItem);
//        });




//        optionalCart.ifPresent(existingCart -> {
//            existingCart.getCartItems().stream()
//                    .filter(item -> item.getProductId().equals(addItemRequest.getProductId()))
//                    .findFirst()
//                    .ifPresent(item -> item.setQuantity(item.getQuantity() + addItemRequest.getQuantity()));
//        });
//
//
//        boolean alreadyExists = cart.getCartItems().stream()
//                .anyMatch(item -> item.getProductId().equals(addItemRequest.getProductId()));
//
//        if (!alreadyExists) {
//            CartItems newItem = new CartItems();
//            newItem.setProductId(addItemRequest.getProductId());
//            newItem.setQuantity(addItemRequest.getQuantity());
//            newItem.setCart(cart);
//            log.info("ADDING PRODUCT AND QUANTITY {} {}", newItem.getProductId(),newItem.getQuantity());
//            cart.getCartItems().add(newItem);
//        }
//        log.info("----------------------------------{}",cart.getCartItems().stream().map(CartItems::getProductId));
//        log.info("CART ITEMS ARE ]]]]]]]]]]]]]]]]]]{} ",cart.getCartItems().stream().toString());

        Cart saved= cartRepository.save(cart);
        return ModelMapper.toDto(saved);

    }

    public CartResponseDto findById(UUID userId, UUID guestId) {
        UUID id=(userId!=null) ? userId : guestId ;
        log.info("[[[[[[[[[[[[[[[--------------------{}",id);
        Cart cart= cartRepository.findByUserIdOrGuestId(userId,guestId).orElseThrow(()->new RuntimeException("Cart Nor Found with the ID"));
        return ModelMapper.toDto(cart);
    }

    public CartResponseDto updateItem(UUID userId, UUID guestId, UpdateItemRequest updateItemRequest) {
        UUID ownerId = userId !=null ? userId : guestId ;
        Cart cart = cartRepository.findByUserIdOrGuestId(userId,guestId)
                .orElseThrow(()-> new RuntimeException("cart not found with the Id"));

        cart.getCartItems().stream().filter(
                cartItem->
                    cartItem.getProductId().equals(updateItemRequest.getProductId())

        ).findFirst().ifPresent(item->item.setQuantity(updateItemRequest.getQuantity()));

        cartRepository.save(cart);

       return ModelMapper.toDto(cart);
    }


    public void removeItem(UUID userId, UUID guestId,UUID productId){
        Cart cart=cartRepository.findByUserIdOrGuestId(userId,guestId).orElseThrow(
                ()->new RuntimeException("Cart not found with the ID")
        );

        cart.getCartItems().removeIf(
                item->item.getProductId().equals(productId)
        );
        cartRepository.save(cart);

    }

    public void clearCart(UUID userId,UUID guestId){
        Cart cart= cartRepository.findByUserIdOrGuestId(userId,guestId).orElseThrow(
                ()->new RuntimeException("cart not found with the given ID")
        );
        cart.getCartItems().clear();
        cartRepository.save(cart);
    }

    public void mergeCart(UUID userId, UUID guestId) {
        Cart guestCart=cartRepository.findByGuestId(guestId).orElseThrow(
                ()-> new RuntimeException("Cart Not Found with the GuestId")
        );
        Optional<Cart> userCart=cartRepository.findByUserId(userId);

        if(userCart.isEmpty()){
            guestCart.setGuestId(null);
            guestCart.setUserId(userId);
            cartRepository.save(guestCart);
            return;
        }
        else {
            if (!guestCart.getCartItems().isEmpty()){
                for (CartItems guestItem: guestCart.getCartItems()){
                    Optional<CartItems> matchinguserItem= userCart.get().getCartItems().stream()
                            .filter(userItem-> userItem.getProductId().equals(guestItem.getProductId()))
                            .findFirst();
                    if(matchinguserItem.isPresent()){
                        matchinguserItem.get().setQuantity(matchinguserItem.get().getQuantity()+guestItem.getQuantity());
                    }else {
                        CartItems newItem= new CartItems();
                        newItem.setProductId(guestItem.getProductId());
                        newItem.setQuantity(guestItem.getQuantity());
                        newItem.setCart(userCart.get());
                        userCart.get().getCartItems().add(newItem);
                    }
                }
            }
            cartRepository.delete(guestCart);
        }
     cartRepository.save(userCart.get());

    }
}
