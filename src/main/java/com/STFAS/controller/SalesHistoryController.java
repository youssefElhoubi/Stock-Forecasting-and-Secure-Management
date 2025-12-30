package com.STFAS.controller;

import com.STFAS.dto.sales.request.SaleRequestDto;
import com.STFAS.entity.SalesHistory;
import com.STFAS.entity.User;
import com.STFAS.repository.UserRepository;
import com.STFAS.service.SalesHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sales-history")
@RequiredArgsConstructor
public class SalesHistoryController {

    private final SalesHistoryService salesHistoryService;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<SalesHistory>> getHistory() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(salesHistoryService.getHistory(user.getId()));
    }

    @PostMapping
    public ResponseEntity<Void> recordSale(@RequestBody SaleRequestDto request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        salesHistoryService.recordSale(request, user.getId());
        return ResponseEntity.ok().build();
    }
    @GetMapping("/warehouse/{id}")
    public ResponseEntity<List<SalesHistory>> getSalesByWarehouse(@PathVariable String id) {
        return ResponseEntity.ok(salesHistoryService.getHistoryByWarehouseId(id));
    }


    @GetMapping("/my-warehouse")
    public ResponseEntity<List<SalesHistory>> getMyWarehouseSales() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getWarehouse() == null) {
            return ResponseEntity.ok(List.of());
        }
        return ResponseEntity.ok(salesHistoryService.getHistoryByWarehouseId(user.getWarehouse().getId()));
    }
}
