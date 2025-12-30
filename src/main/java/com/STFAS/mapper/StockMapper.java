package com.STFAS.mapper;

import com.STFAS.dto.stock.request.StockRequestDto;
import com.STFAS.dto.stock.response.StockResponseDto;
import com.STFAS.entity.Stock;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ProductMapper.class})
public interface StockMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "warehouse", ignore = true)
    Stock toEntity(StockRequestDto dto);

    @Mapping(source = "warehouse.name", target = "warehouseName")
    StockResponseDto toDto(Stock entity);
}