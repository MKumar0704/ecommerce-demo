package com.ecom.productservice.mapper;


import com.ecom.productservice.dto.ProductRequestDto;
import com.ecom.productservice.dto.ProductResponseDTO;
import com.ecom.productservice.model.Categories;
import com.ecom.productservice.model.Product;

public class ModelMapper {

    public static ProductResponseDTO mapToDto(Product product){
        return ProductResponseDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .categoryName(product.getCategory().getName())
                .build();
    }

    public static Product mapToProduct(ProductRequestDto productRequestDto,Categories category) {
        return Product.builder()
                .name(productRequestDto.getName())
                .description(productRequestDto.getDescription())
                .price(productRequestDto.getPrice())
                .stock(productRequestDto.getStock())
                .category(category)
                .build();
    }
}
