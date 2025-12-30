package com.STFAS.dto.stock.response;

import com.STFAS.dto.product.response.ProductResponseDto;
import lombok.Data;

@Data
public class StockResponseDto {
    private String id;
    private ProductResponseDto product;
    private String warehouseName;
    private int quantityAvailable;
    private int alertThreshold;
}