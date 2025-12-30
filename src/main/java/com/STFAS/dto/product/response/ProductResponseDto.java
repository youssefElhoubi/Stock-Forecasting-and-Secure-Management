package com.STFAS.dto.product.response;

import lombok.Data;

@Data
public class ProductResponseDto {
    private String id;
    private String name;
    private String description;
    private String category;
    private Double sellingPrice;
    private Double weight;
    private String unit;

    private Double purchasePrice;
    private Double margin;
}
