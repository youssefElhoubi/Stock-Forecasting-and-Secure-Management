package com.STFAS.mapper;

import com.STFAS.dto.ForecastResponseDto;
import com.STFAS.entity.Forecast;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ForecastMapper {

    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = "warehouse.name", target = "warehouseName")
    @Mapping(source = "predictedQuantity30Days", target = "predictedQuantity")
    @Mapping(source = "confidenceLevel", target = "confidence")
    @Mapping(target = "forecastDate", dateFormat = "yyyy-MM-dd")
    ForecastResponseDto toDto(Forecast entity);
}