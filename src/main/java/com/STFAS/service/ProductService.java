package com.STFAS.service;

import com.STFAS.dto.product.request.ProductRequestDto;
import com.STFAS.dto.product.response.ProductResponseDto;
import com.STFAS.entity.Product;
import com.STFAS.exception.BusinessRuleViolationException;
import com.STFAS.mapper.ProductMapper;
import com.STFAS.repository.ProductRepository;
import com.STFAS.service.repository.ProductServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService implements ProductServiceInterface {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public ProductResponseDto createProduct(ProductRequestDto request) {
        if (productRepository.findByName(request.getName()).isPresent()) {
            throw new BusinessRuleViolationException("Product already exists");
        }
        Product product = productMapper.toEntity(request);
        product = productRepository.save(product);

        return productMapper.toDto(product);
    }

    @Override
    public ProductResponseDto updateProduct(String id, ProductRequestDto request) {
        Product product =productRepository.findById(id).orElseThrow(() -> {
            throw new BusinessRuleViolationException("Product already exists");
        });
        Product updatedProduct = productMapper.toEntity(request);
        updatedProduct.setId(id);

        return productMapper.toDto(updatedProduct);
    }

    @Override
    public ProductResponseDto getProductById(String id) {
        Product product =productRepository.findById(id).orElseThrow(() -> {
            throw new BusinessRuleViolationException("Product already exists");
        });
        return sanitizeProductResponse(productMapper.toDto(product));
    }

    @Override
    public List<ProductResponseDto> getAllProducts() {
        List<Product> products =productRepository.findAll();
        return products.stream().map(productMapper::toDto).map(this::sanitizeProductResponse).toList();
    }

    @Override
    public void deleteProduct(String id) {
        Product product =productRepository.findById(id).orElseThrow(() -> {
            throw new BusinessRuleViolationException("Product already exists");
        });
        productRepository.delete(product);
    }

    @Override
    public List<ProductResponseDto> getProductsByCategory(String category) {
        List<Product> products =productRepository.findByCategory(category);
        return products.stream().map(productMapper::toDto).map(this::sanitizeProductResponse).toList();
    }

    private ProductResponseDto sanitizeProductResponse(ProductResponseDto dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_GESTIONNAIRE"))) {
            dto.setPurchasePrice(null);
            dto.setMargin(null);
        }
        return dto;
    }
}
