package com.STFAS.service.repository;

import com.STFAS.dto.sales.request.SaleRequestDto;
import com.STFAS.entity.SalesHistory;

import java.util.List;

public interface SalesHistoryServiceInterface {
    List<SalesHistory> getHistory(String userId);
    void recordSale(SaleRequestDto request, String userId);
    List<SalesHistory> getHistoryByWarehouseId(String warehouseId);
}
