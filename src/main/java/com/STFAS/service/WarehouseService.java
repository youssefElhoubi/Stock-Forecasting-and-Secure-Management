package com.STFAS.service;

import com.STFAS.dto.Warehouse.request.WarehouseRequestDto;
import com.STFAS.dto.Warehouse.request.WarehouseUpdateRequest;
import com.STFAS.dto.Warehouse.response.WarehouseResponseDto;
import com.STFAS.exception.BusinessRuleViolationException;
import com.STFAS.mapper.WarehouseMapper;
import com.STFAS.repository.WarehouseRepository;
import com.STFAS.service.repository.WarehouseInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.STFAS.entity.Warehouse;

import java.util.List;

@RequiredArgsConstructor
@Service
public class WarehouseService implements WarehouseInterface {
    private final WarehouseRepository warehouseRepository;
    private final WarehouseMapper warehouseMapper;

    @Override
    public WarehouseResponseDto createWarehouse(WarehouseRequestDto request) {
        if (warehouseRepository.findWarehouseByName(request.getName()).isPresent()){
            throw new BusinessRuleViolationException("Warehouse already exists");
        }
        Warehouse warehouse = warehouseMapper.toEntity(request);
        return warehouseMapper.toDto(warehouseRepository.save(warehouse));
    }

    @Override
    public WarehouseResponseDto updateWarehouse(String id, WarehouseUpdateRequest request) {
        Warehouse warehouse = warehouseRepository.findById(id).orElseThrow(()-> new BusinessRuleViolationException("Warehouse not found"));
        warehouse.setName(request.getName());
        warehouse.setAddress(request.getAddress());
        warehouse.setCity(request.getCity());
        warehouseRepository.save(warehouse);
        return warehouseMapper.toDto(warehouse);
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
