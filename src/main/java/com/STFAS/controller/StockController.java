package com.STFAS.controller;

import com.STFAS.dto.stock.request.StockRequestDto;
import com.STFAS.dto.stock.response.StockResponseDto;
import com.STFAS.service.repository.StockServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
public class StockController {

    private final StockServiceInterface stockService;

    @PostMapping
    public ResponseEntity<StockResponseDto> createStock(
            @RequestBody StockRequestDto request
    ) {
        return ResponseEntity.ok(stockService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StockResponseDto> updateStock(
            @PathVariable String id,
            @RequestBody StockRequestDto request
    ) {
        return ResponseEntity.ok(stockService.updateStock(id, request));
    }

    @GetMapping("/product/{productId}/warehouse/{warehouseId}")
    public ResponseEntity<StockResponseDto> getStockByProductAndWarehouse(
            @PathVariable String productId,
            @PathVariable String warehouseId
    ) {
        return ResponseEntity.ok(
                stockService.getStockByProductAndWarehouse(productId, warehouseId)
        );
    }

    @GetMapping("/warehouse/{warehouseId}")
    public ResponseEntity<List<StockResponseDto>> getStocksByWarehouse(
            @PathVariable String warehouseId
    ) {
        return ResponseEntity.ok(stockService.getStocksByWarehouse(warehouseId));
    }

    @GetMapping
    public ResponseEntity<List<StockResponseDto>> getAllStocks() {
        return ResponseEntity.ok(stockService.getAllStocks());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStock(@PathVariable String id) {
        // Optional: add delete method in service later
        return ResponseEntity.noContent().build();
    }
}
