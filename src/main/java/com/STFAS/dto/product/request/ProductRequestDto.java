package com.STFAS.dto.product.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProductRequestDto {
    @NotBlank
    private String name;
    private String description;
    @NotBlank
    private String category;
    @Min(0)
    private Double sellingPrice;

    @Min(0)
    private Double purchasePrice;
    @Min(0)
    private Double margin;

    private Double weight;
    @NotBlank
    private String unit;
}
