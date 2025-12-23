package com.STFAS.dto;

import lombok.Data;

@Data
public class ForecastResponseDto {
    private String productName;
    private String warehouseName;
    private int predictedQuantity;
    private double confidence;
    private String recommendation;
    private String forecastDate;
}
