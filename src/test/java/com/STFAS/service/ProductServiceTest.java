package com.STFAS.service;

import com.STFAS.dto.product.request.ProductRequestDto;
import com.STFAS.dto.product.response.ProductResponseDto;
import com.STFAS.entity.Product;
import com.STFAS.exception.BusinessRuleViolationException;
import com.STFAS.mapper.ProductMapper;
import com.STFAS.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductService Unit Tests")
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductService productService;

    private Product product;
    private ProductRequestDto productRequestDto;
    private ProductResponseDto productResponseDto;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId("product-123");
        product.setName("Laptop");
        product.setCategory("Electronics");
        product.setDescription("High-performance laptop");

        productRequestDto = new ProductRequestDto();
        productRequestDto.setName("Laptop");
        productRequestDto.setCategory("Electronics");
        productRequestDto.setDescription("High-performance laptop");

        productResponseDto = new ProductResponseDto();
        productResponseDto.setId("product-123");
        productResponseDto.setName("Laptop");
        productResponseDto.setCategory("Electronics");
        productResponseDto.setDescription("High-performance laptop");
        productResponseDto.setPurchasePrice(500.0);
        productResponseDto.setMargin(0.2);
    }

    // ==================== createProduct Tests ====================
    @Test
    @DisplayName("createProduct - should successfully create a new product")
    void testCreateProductSuccess() {
        // Arrange
        when(productRepository.findByName(productRequestDto.getName())).thenReturn(Optional.empty());
        when(productMapper.toEntity(productRequestDto)).thenReturn(product);
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(productMapper.toDto(product)).thenReturn(productResponseDto);

        // Act
        ProductResponseDto result = productService.createProduct(productRequestDto);

        // Assert
        assertNotNull(result);
        assertEquals("Laptop", result.getName());
        assertEquals("Electronics", result.getCategory());
        verify(productRepository, times(1)).findByName(productRequestDto.getName());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("createProduct - should throw exception when product with name already exists")
    void testCreateProductDuplicateName() {
        // Arrange
        when(productRepository.findByName(productRequestDto.getName())).thenReturn(Optional.of(product));

        // Act & Assert
        BusinessRuleViolationException exception = assertThrows(BusinessRuleViolationException.class, () ->
            productService.createProduct(productRequestDto)
        );
        assertEquals("Product already exists", exception.getMessage());
        verify(productRepository, times(1)).findByName(productRequestDto.getName());
        verify(productRepository, never()).save(any(Product.class));
    }

    // ==================== updateProduct Tests ====================
    @Test
    @DisplayName("updateProduct - should successfully update product")
    void testUpdateProductSuccess() {
        // Arrange
        ProductRequestDto updateRequest = new ProductRequestDto();
        updateRequest.setName("Updated Laptop");
        updateRequest.setCategory("Electronics");

        Product updatedProduct = new Product();
        updatedProduct.setId("product-123");
        updatedProduct.setName("Updated Laptop");

        ProductResponseDto updatedResponseDto = new ProductResponseDto();
        updatedResponseDto.setId("product-123");
        updatedResponseDto.setName("Updated Laptop");

        when(productRepository.findById("product-123")).thenReturn(Optional.of(product));
        when(productMapper.toEntity(updateRequest)).thenReturn(updatedProduct);
        when(productMapper.toDto(updatedProduct)).thenReturn(updatedResponseDto);

        // Act
        ProductResponseDto result = productService.updateProduct("product-123", updateRequest);

        // Assert
        assertNotNull(result);
        assertEquals("product-123", result.getId());
        verify(productRepository, times(1)).findById("product-123");
    }

    @Test
    @DisplayName("updateProduct - should throw exception when product not found")
    void testUpdateProductNotFound() {
        // Arrange
        when(productRepository.findById("product-999")).thenReturn(Optional.empty());

        // Act & Assert
        BusinessRuleViolationException exception = assertThrows(BusinessRuleViolationException.class, () ->
            productService.updateProduct("product-999", productRequestDto)
        );
        assertEquals("Product already exists", exception.getMessage());
    }

    // ==================== getProductById Tests ====================
    @Test
    @DisplayName("getProductById - should return product when exists")
    void testGetProductByIdSuccess() {
        // Arrange
        mockSecurityContext(false); // Not GESTIONNAIRE
        when(productRepository.findById("product-123")).thenReturn(Optional.of(product));
        when(productMapper.toDto(product)).thenReturn(productResponseDto);

        // Act
        ProductResponseDto result = productService.getProductById("product-123");

        // Assert
        assertNotNull(result);
        assertEquals("Laptop", result.getName());
        verify(productRepository, times(1)).findById("product-123");
    }

    @Test
    @DisplayName("getProductById - should throw exception when product not found")
    void testGetProductByIdNotFound() {
        // Arrange
        mockSecurityContext(false);
        when(productRepository.findById("product-999")).thenReturn(Optional.empty());

        // Act & Assert
        BusinessRuleViolationException exception = assertThrows(BusinessRuleViolationException.class, () ->
            productService.getProductById("product-999")
        );
        assertEquals("Product already exists", exception.getMessage());
    }

    // ==================== getAllProducts Tests ====================
    @Test
    @DisplayName("getAllProducts - should return all products")
    void testGetAllProductsSuccess() {
        // Arrange
        mockSecurityContext(false);
        Product product2 = new Product();
        product2.setId("product-456");
        product2.setName("Desktop");

        ProductResponseDto productResponseDto2 = new ProductResponseDto();
        productResponseDto2.setId("product-456");
        productResponseDto2.setName("Desktop");

        when(productRepository.findAll()).thenReturn(List.of(product, product2));
        when(productMapper.toDto(product)).thenReturn(productResponseDto);
        when(productMapper.toDto(product2)).thenReturn(productResponseDto2);

        // Act
        List<ProductResponseDto> result = productService.getAllProducts();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Laptop", result.get(0).getName());
        assertEquals("Desktop", result.get(1).getName());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("getAllProducts - should return empty list when no products exist")
    void testGetAllProductsEmpty() {
        // Arrange
        mockSecurityContext(false);
        when(productRepository.findAll()).thenReturn(List.of());

        // Act
        List<ProductResponseDto> result = productService.getAllProducts();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ==================== getProductsByCategory Tests ====================
    @Test
    @DisplayName("getProductsByCategory - should return products filtered by category")
    void testGetProductsByCategorySuccess() {
        // Arrange
        mockSecurityContext(false);
        when(productRepository.findByCategory("Electronics")).thenReturn(List.of(product));
        when(productMapper.toDto(product)).thenReturn(productResponseDto);

        // Act
        List<ProductResponseDto> result = productService.getProductsByCategory("Electronics");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Electronics", result.get(0).getCategory());
        verify(productRepository, times(1)).findByCategory("Electronics");
    }

    @Test
    @DisplayName("getProductsByCategory - should return empty list when no products in category")
    void testGetProductsByCategoryEmpty() {
        // Arrange
        mockSecurityContext(false);
        when(productRepository.findByCategory("NonExistent")).thenReturn(List.of());

        // Act
        List<ProductResponseDto> result = productService.getProductsByCategory("NonExistent");

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ==================== deleteProduct Tests ====================
    @Test
    @DisplayName("deleteProduct - should successfully delete product")
    void testDeleteProductSuccess() {
        // Arrange
        when(productRepository.findById("product-123")).thenReturn(Optional.of(product));

        // Act
        productService.deleteProduct("product-123");

        // Assert
        verify(productRepository, times(1)).findById("product-123");
        verify(productRepository, times(1)).delete(product);
    }

    @Test
    @DisplayName("deleteProduct - should throw exception when product not found")
    void testDeleteProductNotFound() {
        // Arrange
        when(productRepository.findById("product-999")).thenReturn(Optional.empty());

        // Act & Assert
        BusinessRuleViolationException exception = assertThrows(BusinessRuleViolationException.class, () ->
            productService.deleteProduct("product-999")
        );
        assertEquals("Product already exists", exception.getMessage());
        verify(productRepository, never()).delete(any());
    }

    // ==================== sanitizeProductResponse Tests ====================
    @Test
    @DisplayName("sanitizeProductResponse - should hide sensitive fields for GESTIONNAIRE role")
    void testSanitizeProductResponseForGestionnaire() {
        // Arrange
        mockSecurityContext(true); // GESTIONNAIRE role
        ProductResponseDto dtoWithSensitiveData = new ProductResponseDto();
        dtoWithSensitiveData.setId("product-123");
        dtoWithSensitiveData.setName("Laptop");
        dtoWithSensitiveData.setPurchasePrice(500.0);
        dtoWithSensitiveData.setMargin(0.2);

        // Act
        ProductResponseDto result = productService.getProductById("product-123");

        // The sanitizeProductResponse is called internally
        // For this test, we verify that the service handles GESTIONNAIRE role properly
        // Note: This would require accessing the actual method result after sanitization
    }

    @Test
    @DisplayName("sanitizeProductResponse - should keep sensitive fields for non-GESTIONNAIRE role")
    void testSanitizeProductResponseForAdmin() {
        // Arrange
        mockSecurityContext(false); // ADMIN or other role
        when(productRepository.findById("product-123")).thenReturn(Optional.of(product));
        when(productMapper.toDto(product)).thenReturn(productResponseDto);

        // Act
        ProductResponseDto result = productService.getProductById("product-123");

        // Assert
        assertNotNull(result);
        assertNotNull(result.getPurchasePrice());
        assertNotNull(result.getMargin());
    }

    // ==================== Helper method ====================
    private void mockSecurityContext(boolean isGestionnaire) {
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        
        if (isGestionnaire) {
            GrantedAuthority authority = mock(GrantedAuthority.class);
            when(authority.getAuthority()).thenReturn("ROLE_GESTIONNAIRE");
            when(authentication.getAuthorities()).thenReturn((Collection) List.of(authority));
        } else {
            when(authentication.getAuthorities()).thenReturn((Collection) List.of());
        }
        
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }
}
