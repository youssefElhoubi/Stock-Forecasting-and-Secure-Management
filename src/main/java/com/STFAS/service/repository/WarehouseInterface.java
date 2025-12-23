package com.STFAS.service.repository;

import com.STFAS.dto.Warehouse.request.WarehouseRequestDto;
import com.STFAS.dto.Warehouse.request.WarehouseUpdateRequest;
import com.STFAS.dto.Warehouse.response.WarehouseResponseDto;

import java.util.List;

public interface WarehouseInterface {
    WarehouseResponseDto createWarehouse(WarehouseRequestDto request);
    WarehouseResponseDto updateWarehouse(String id, WarehouseUpdateRequest request);
    WarehouseResponseDto getWarehouseById(String id);
    List<WarehouseResponseDto> getAllWarehouses();
    void deleteWarehouse(String id);
}
