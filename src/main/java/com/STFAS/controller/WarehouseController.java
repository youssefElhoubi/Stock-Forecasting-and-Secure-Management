package com.STFAS.controller;

import com.STFAS.dto.Warehouse.request.WarehouseRequestDto;
import com.STFAS.dto.Warehouse.response.WarehouseResponseDto;
import com.STFAS.entity.Warehouse;
import com.STFAS.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/warehouse")
public class WarehouseController{
    private final WarehouseService warehouseService;

    public ResponseEntity<WarehouseResponseDto> Create(@RequestBody WarehouseRequestDto warehouse){
        return ResponseEntity.ok(warehouseService.createWarehouse(warehouse));
    }

}
