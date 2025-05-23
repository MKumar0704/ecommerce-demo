package com.ecom.productservice.dto;


import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class ProductResponseDTO {

    private UUID id;

    private String name;

    private String description;

    private String price;

    private String stock;

    private String categoryName;

}
