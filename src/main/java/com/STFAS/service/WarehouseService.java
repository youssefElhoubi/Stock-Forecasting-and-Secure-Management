package com.STFAS.service;

import com.STFAS.dto.Warehouse.request.WarehouseRequestDto;
import com.STFAS.dto.Warehouse.request.WarehouseUpdateRequest;
import com.STFAS.dto.Warehouse.response.WarehouseResponseDto;
import com.STFAS.repository.WarehouseRepository;
import com.STFAS.service.repository.Warehouse;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class WarehouseService implements Warehouse {
    private final WarehouseRepository warehouseRepository;

    @Override
    public WarehouseResponseDto createWarehouse(WarehouseRequestDto request) {
        return null;
    }

    @Override
    public WarehouseResponseDto updateWarehouse(String id, WarehouseUpdateRequest request) {
        return null;
    }

    @Override
    public WarehouseResponseDto getWarehouseById(String id) {
        return null;
    }

    @Override
    public List<WarehouseResponseDto> getAllWarehouses() {
        return List.of();
    }

    @Override
    public void deleteWarehouse(String id) {

    }
}
