package com.ecom.productservice.service;

import com.ecom.productservice.dto.ProductRequestDto;
import com.ecom.productservice.dto.ProductResponseDTO;
import com.ecom.productservice.exception.ResourceNotFoundException;
import com.ecom.productservice.mapper.ModelMapper;
import com.ecom.productservice.model.Categories;
import com.ecom.productservice.model.Product;
import com.ecom.productservice.repository.CategoryRepository;
import com.ecom.productservice.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;

import java.util.List;
import java.util.UUID;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public Page<ProductResponseDTO> getProducts(Pageable pageable) {
        return productRepository.findAll(pageable).map(ModelMapper::mapToDto);
    }

    public ProductResponseDTO addProduct(ProductRequestDto productRequestDto) {
        Categories category = categoryRepository.findByName(productRequestDto.getCategoryName())
                .orElseGet(() -> {
                    Categories newCategory = new Categories();
                    newCategory.setName(productRequestDto.getCategoryName());
                    return categoryRepository.save(newCategory);
                });
        Product product=productRepository.save(ModelMapper.mapToProduct(productRequestDto,category));
        return ModelMapper.mapToDto(product);
    }

    public ProductResponseDTO updateProduct(UUID id, ProductRequestDto productRequestDto) {
        Product product=productRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Product Not found"));
        return ModelMapper.mapToDto(productRepository.save(product));
    }

    public void deleteProduct(UUID id) {
        Product product=productRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Product Not found"));
         productRepository.delete(product);
    }
}
