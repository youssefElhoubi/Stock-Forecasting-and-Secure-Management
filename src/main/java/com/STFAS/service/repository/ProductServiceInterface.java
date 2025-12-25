package com.STFAS.service.repository;

import com.STFAS.dto.product.request.ProductRequestDto;
import com.STFAS.dto.product.response.ProductResponseDto;

import java.util.List;

public interface ProductServiceInterface {

    /**
     * Creates a new product in the global catalog.
     * Access: ADMIN only.
     * Logic:
     * 1. Encrypts purchasePrice and margin before saving to DB.
     * 2. Sets public fields (name, description, sellingPrice).
     */
    ProductResponseDto createProduct(ProductRequestDto request);

    /**
     * Updates an existing product.
     * Access: ADMIN only.
     * Logic:
     * 1. Updates only non-null fields from the request.
     * 2. Re-encrypts prices/margins if they are included in the update.
     */
    ProductResponseDto updateProduct(String id, ProductRequestDto request);

    /**
     * Retrieves a product by its unique ID.
     * Access: Authenticated Users.
     * Security Logic:
     * - If user is ADMIN: Returns DTO with decrypted purchasePrice/margin.
     * - If user is MANAGER: Returns DTO with null/hidden sensitive fields.
     */
    ProductResponseDto getProductById(String id);

    /**
     * Retrieves all products in the catalog.
     * Access: Authenticated Users.
     * Security Logic:
     * - If user is ADMIN: Returns full data (decrypted).
     * - If user is MANAGER: Returns public data only.
     */
    List<ProductResponseDto> getAllProducts();

    /**
     * Permanently removes a product from the catalog.
     * Access: ADMIN only.
     * Note: Should check if stocks exist for this product first to prevent orphaned records.
     */
    void deleteProduct(String id);

    /**
     * Retrieves products by category (optional filtering).
     * Access: Authenticated Users.
     */
    List<ProductResponseDto> getProductsByCategory(String category);
}