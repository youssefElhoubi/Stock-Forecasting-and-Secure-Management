package com.STFAS.mapper;

import com.STFAS.dto.Warehouse.request.WarehouseRequestDto;
import com.STFAS.dto.Warehouse.request.WarehouseUpdateRequest;
import com.STFAS.dto.Warehouse.response.WarehouseResponseDto;
import com.STFAS.entity.Warehouse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WarehouseMapper {

    @Mapping(target = "id", ignore = true)
    Warehouse toEntity(WarehouseRequestDto dto);
    @Mapping(target = "id", ignore = true)
    Warehouse toEntity(WarehouseUpdateRequest dto);
    WarehouseResponseDto toDto(Warehouse entity);
}