package com.STFAS.dto.salesHistory.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SalesHistoryRequestDto {
    @NotBlank private String productId;
    @NotBlank private String warehouseId;
    @Min(1) private int quantitySold;
}