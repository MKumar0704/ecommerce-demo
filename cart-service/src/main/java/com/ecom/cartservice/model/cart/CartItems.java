package com.ecom.cartservice.model.cart;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


import java.util.UUID;

@Entity
@Table(name = "cart_items")
@Getter
@Setter
public class CartItems {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id",nullable = false)
    @JsonBackReference
    private Cart cart;

    @Column(name = "product_id",nullable = false)
    private UUID productId;

    @Column(nullable = false)
    private Integer quantity;

}
