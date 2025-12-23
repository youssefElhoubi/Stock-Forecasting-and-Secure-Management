package com.STFAS.mapper;

import com.STFAS.dto.product.request.ProductRequestDto;
import com.STFAS.dto.product.response.ProductResponseDto;
import com.STFAS.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    // Request -> Entity
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "encryptedPurchasePrice", source = "purchasePrice", qualifiedByName = "encryptPrice")
    @Mapping(target = "encryptedMargin", source = "margin", qualifiedByName = "encryptPrice")
    Product toEntity(ProductRequestDto dto);

    // Entity -> Response
    @Mapping(target = "purchasePrice", source = "encryptedPurchasePrice", qualifiedByName = "decryptPrice")
    @Mapping(target = "margin", source = "encryptedMargin", qualifiedByName = "decryptPrice")
    ProductResponseDto toDto(Product entity);

    // Placeholder methods for MapStruct to link to your Encryption Logic
    @Named("encryptPrice")
    default String encryptPrice(Double value) {
        if (value == null) return null;
        return "ENC_" + value; // Replace with actual AES Encryption logic
    }

    @Named("decryptPrice")
    default Double decryptPrice(String value) {
        if (value == null || !value.startsWith("ENC_")) return null;
        return Double.valueOf(value.replace("ENC_", "")); // Replace with actual AES Decryption
    }
}