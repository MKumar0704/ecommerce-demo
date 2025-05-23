package com.ecom.cartservice.repository.order;

import com.ecom.cartservice.model.order.OrderItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OrderItemsRepository extends JpaRepository<OrderItems, UUID> {
}
