package com.ecom.cartservice.controller;

import com.ecom.cartservice.dto.order.OrderCreateRequest;
import com.ecom.cartservice.model.order.Order;
import com.ecom.cartservice.service.OrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class OrderController {


    private final OrderService orderService;


    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }


    @PostMapping
    public Order createOrder(@RequestBody OrderCreateRequest orderCreateRequest){
        return orderService.createOrder(orderCreateRequest.getUserId(),orderCreateRequest.getItems());
    }

    @GetMapping
    public Order getOrder(@PathVariable UUID orderId){
        return orderService.getOrder(orderId)
                .orElseThrow(
                        ()->new RuntimeException("Order not found")
                );
    }

    @GetMapping("/user/{userId}")
    public List<Order> getOrdersByUser(@PathVariable UUID userId) {
        return orderService.getOrdersByUser(userId);
    }

    // Update order status
    @PutMapping("/{orderId}/status")
    public Order updateOrderStatus(@PathVariable UUID orderId, @RequestParam String status) {
        return orderService.updateOrderStatus(orderId, status)
                .orElseThrow(() -> new RuntimeException("Order Not Found With the ID"));
    }
}
