package com.STFAS.controller;

import com.STFAS.dto.Warehouse.request.WarehouseRequestDto;
import com.STFAS.dto.Warehouse.request.WarehouseUpdateRequest;
import com.STFAS.dto.Warehouse.response.WarehouseResponseDto;
import com.STFAS.entity.Warehouse;
import com.STFAS.service.WarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/warehouse")
public class WarehouseController {
    private final WarehouseService warehouseService;

    @PostMapping
    public ResponseEntity<WarehouseResponseDto> create(@RequestBody WarehouseRequestDto warehouse) {
        return ResponseEntity.ok(warehouseService.createWarehouse(warehouse));
    }

    @GetMapping("all")
    public ResponseEntity<List<WarehouseResponseDto>> all(){
        return ResponseEntity.ok(warehouseService.getAllWarehouses());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<WarehouseResponseDto> update(@PathVariable String id, @RequestBody WarehouseUpdateRequest warehouse) {
        return ResponseEntity.ok(warehouseService.updateWarehouse(id, warehouse));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable String id) {
        warehouseService.deleteWarehouse(id);
        return ResponseEntity.ok("warehouse was deleted");
    }

}
