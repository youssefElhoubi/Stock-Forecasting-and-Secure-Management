package com.STFAS.mapper;

import com.STFAS.dto.salesHistory.request.SalesHistoryRequestDto;
import com.STFAS.dto.salesHistory.response.SalesHistoryResponseDto;
import com.STFAS.entity.SalesHistory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SalesHistoryMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "warehouse", ignore = true)
    @Mapping(target = "dayOfWeek", ignore = true)
    @Mapping(target = "month", ignore = true)
    @Mapping(target = "year", ignore = true)
    @Mapping(target = "saleDate", ignore = true)
    SalesHistory toEntity(SalesHistoryRequestDto dto);

    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = "warehouse.name", target = "warehouseName")
    SalesHistoryResponseDto toDto(SalesHistory entity);
}