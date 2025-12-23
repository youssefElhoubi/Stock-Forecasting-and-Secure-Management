package com.STFAS.mapper;

import com.STFAS.dto.salesHistory.request.SalesHistoryRequestDto;
import com.STFAS.dto.salesHistory.response.SalesHistoryResponseDto;
import com.STFAS.entity.SalesHistory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SalesHistoryMapper {

    // Request -> Entity
    // NOTE: The Service layer must fetch the actual Product and Warehouse objects
    // using the IDs from the DTO and set them manually.
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "warehouse", ignore = true)
    @Mapping(target = "dayOfWeek", ignore = true) // Calculated in Service
    @Mapping(target = "month", ignore = true)     // Calculated in Service
    @Mapping(target = "year", ignore = true)      // Calculated in Service
    @Mapping(target = "saleDate", ignore = true)  // Set in Service (usually LocalDateTime.now())
    SalesHistory toEntity(SalesHistoryRequestDto dto);

    // Entity -> Response
    // We extract names to make the API response readable
    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = "warehouse.name", target = "warehouseName")
    SalesHistoryResponseDto toDto(SalesHistory entity);
}