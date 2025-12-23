package com.STFAS.mapper;

import com.STFAS.dto.stock.request.StockRequestDto;
import com.STFAS.dto.stock.response.StockResponseDto;
import com.STFAS.entity.Stock;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ProductMapper.class})
public interface StockMapper {

    // For Request, we usually need to fetch the Product/Warehouse from DB manually
    // inside the Service, so we often don't map Request->Entity purely in MapStruct
    // when using @DBRef unless we write custom logic.
    // Keeping it simple here:
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "product", ignore = true) // Handled in Service
    @Mapping(target = "warehouse", ignore = true) // Handled in Service
    Stock toEntity(StockRequestDto dto);

    @Mapping(source = "warehouse.name", target = "warehouseName")
    StockResponseDto toDto(Stock entity);
}