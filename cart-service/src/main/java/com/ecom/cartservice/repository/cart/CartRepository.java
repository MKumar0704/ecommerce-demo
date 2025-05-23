package com.ecom.cartservice.repository.cart;

import com.ecom.cartservice.model.cart.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CartRepository extends JpaRepository<Cart, UUID> {

//    @Query(value = "SELECT c.id AS cart_id, c.user_id, c.guest_id, c.created_at, c.modified_at, ci.id AS cart_item_id, ci.product_id, ci.quantity " +
//            "FROM cart c LEFT JOIN cart_items ci ON c.id = ci.cart_id " +
//            "WHERE c.user_id = :id OR c.guest_id = :id", nativeQuery = true)
//    Optional<CartItemDto> findCartByUserIdOrGuestId(UUID id);


    boolean existsByGuestId(UUID guestId);

    Optional<Cart>
    findByUserIdOrGuestId(UUID userId,UUID guestId);

    Optional<Cart> findByGuestId(UUID guestId);

    Optional<Cart> findByUserId(UUID userId);
}
