package com.STFAS.service.repository;

import com.STFAS.dto.stock.request.StockRequestDto;
import com.STFAS.entity.Stock;

import java.util.List;

public interface StockServiceInterface {

    Stock updateStock(String stockId, int newQuantity, String userId);

    List<Stock> getStocksByWarehouse(String warehouseId);

    Stock getStockById(String id);

    List<Stock> getAllStocks(String userId);

    Stock createStock(StockRequestDto request);
}

