package com.ecom.productservice.controller;

import com.ecom.productservice.dto.ProductRequestDto;
import com.ecom.productservice.dto.ProductResponseDTO;
import com.ecom.productservice.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController

public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/products")
    public ResponseEntity<Page<ProductResponseDTO>> getProducts(@RequestParam(defaultValue = "0") Integer page,
                                                                @RequestParam(defaultValue = "10") Integer size){
        Pageable pageable= PageRequest.of(page,size);
        Page<ProductResponseDTO> product=productService.getProducts(pageable);
        return ResponseEntity.ok().body(product);
    }


    @PostMapping("/products")
    public ResponseEntity<ProductResponseDTO> addProduct(@RequestBody @Valid ProductRequestDto productRequestDto){
        ProductResponseDTO product=productService.addProduct(productRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<ProductResponseDTO> updateProduct(@PathVariable UUID id,@RequestBody @Valid ProductRequestDto productRequestDto){
        ProductResponseDTO product=productService.updateProduct(id,productRequestDto);
        return ResponseEntity.ok(product);
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable UUID id){
       productService.deleteProduct(id);
       return ResponseEntity.ok("Product deleted successfully");
    }
}
