package com.ecom.productservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jdk.jfr.Category;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String price;

    @Column(nullable = false)
    private String stock;

    @ManyToOne
    @JoinColumn(name = "category_id",nullable = false)
    private Categories category;

}
