package com.ecom.cartservice.repository.cart;

import com.ecom.cartservice.model.cart.CartItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CartItemRepository extends JpaRepository<CartItems, UUID> {
}
