package com.ecom.cartservice.service;

import com.ecom.cartservice.model.order.Order;
import com.ecom.cartservice.model.order.OrderItems;
import com.ecom.cartservice.repository.order.OrderItemsRepository;
import com.ecom.cartservice.repository.order.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    private final OrderItemsRepository orderItemsRepository;

    public OrderService(OrderRepository orderRepository, OrderItemsRepository orderItemsRepository) {
        this.orderRepository = orderRepository;
        this.orderItemsRepository = orderItemsRepository;
    }

    @Transactional
    public Order createOrder(UUID userId, List<OrderItems> orderItems){
        Order order=Order.builder()
                .userId(userId)
                .status("PENDING")
                .build();

        BigDecimal totalPrice=BigDecimal.ZERO;

        for (OrderItems item : orderItems){
            item.setOrder(order);
            totalPrice=totalPrice.add(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }

        order.setItems(orderItems);
        order.setTotalAmount(totalPrice);
        return orderRepository.save(order);
    }

    public Optional<Order> getOrder(UUID orderId){
        return orderRepository.findById(orderId);
    }

    @Transactional
        public Optional<Order> updateOrderStatus(UUID orderId, String status) {
            Optional<Order> optionalOrder = orderRepository.findById(orderId);
            optionalOrder.ifPresent(order -> {
                order.setStatus(status);
                orderRepository.save(order);
            });
            return optionalOrder;
        }

    public List<Order> getOrdersByUser(UUID userId) {
        return orderRepository.findByUserId(userId);
    }
}



