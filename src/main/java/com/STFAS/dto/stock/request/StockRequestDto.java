package com.STFAS.dto.stock.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.aspectj.lang.annotation.Aspect;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StockRequestDto {
    @NotBlank private String productId;
    @NotBlank private String warehouseId;
    @Min(0) private int quantityAvailable;
    @Min(0) private int alertThreshold;
}
