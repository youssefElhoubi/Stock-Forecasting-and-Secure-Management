package com.STFAS.controller;

import com.STFAS.dto.stock.request.StockRequestDto;
import com.STFAS.entity.Stock;
import com.STFAS.entity.User;
import com.STFAS.repository.UserRepository;
import com.STFAS.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<Stock>> getAllStocks() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(stockService.getAllStocks(user.getId()));
    }
    @GetMapping("/{id}")
    public ResponseEntity<Stock> getStockById(@PathVariable String id) {
        return ResponseEntity.ok(stockService.getStockById(id));
    }

    @PostMapping
    public ResponseEntity<Stock> createStock(@RequestBody StockRequestDto request) {
        return ResponseEntity.ok(stockService.createStock(request));
    }


    @PatchMapping("/{id}")
    public ResponseEntity<Stock> updateStock(
            @PathVariable String id,
            @RequestBody StockRequestDto request
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(stockService.updateStock(id, request.getQuantityAvailable(), user.getId()));
    }

    public ResponseEntity<List<Stock>> getStocksByWarehouse(@PathVariable String warehouseId) {
        return ResponseEntity.ok(stockService.getStocksByWarehouse(warehouseId));
    }


}
