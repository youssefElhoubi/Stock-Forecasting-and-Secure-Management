package com.STFAS.dto.stock.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StockRequestDto {
    @NotBlank private String productId;
    @NotBlank private String warehouseId;
    @Min(0) private int quantityAvailable;
    @Min(0) private int alertThreshold;
}
