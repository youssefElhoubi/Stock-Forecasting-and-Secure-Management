package com.STFAS.service.repository;

import com.STFAS.dto.product.request.ProductRequestDto;
import com.STFAS.dto.product.response.ProductResponseDto;

import java.util.List;

public interface ProductServiceInterface {
    ProductResponseDto createProduct(ProductRequestDto request);

    ProductResponseDto updateProduct(String id, ProductRequestDto request);

    ProductResponseDto getProductById(String id);

    List<ProductResponseDto> getAllProducts();

    void deleteProduct(String id);

    List<ProductResponseDto> getProductsByCategory(String category);
}