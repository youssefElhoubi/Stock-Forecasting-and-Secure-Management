package com.STFAS.service.repository;

import com.STFAS.dto.stock.request.StockRequestDto;
import com.STFAS.dto.stock.response.StockResponseDto;

import java.util.List;

public interface StockServiceInterface {

    StockResponseDto updateStock(StockRequestDto request);

    StockResponseDto create(StockRequestDto request);

    StockResponseDto getStockByProductAndWarehouse(String productId, String warehouseId);

    List<StockResponseDto> getStocksByWarehouse(String warehouseId);

    List<StockResponseDto> getAllStocks();

    List<StockResponseDto> getLowStockAlerts(String warehouseId);

    void adjustStockQuantity(String productId, String warehouseId, int quantityChange);
}
