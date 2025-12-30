package com.STFAS.service;

import com.STFAS.dto.sales.request.SaleRequestDto;
import com.STFAS.entity.SalesHistory;
import com.STFAS.entity.Stock;
import com.STFAS.entity.User;
import com.STFAS.enums.Role;
import com.STFAS.repository.SaleHistoryRepository;
import com.STFAS.repository.StockRepository;
import com.STFAS.repository.UserRepository;
import com.STFAS.service.repository.SalesHistoryServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SalesHistoryService implements SalesHistoryServiceInterface {

    private final SaleHistoryRepository saleHistoryRepository;
    private final StockRepository stockRepository;
    private final UserRepository userRepository;
    private final StockService stockService;

    @Override
    public List<SalesHistory> getHistory(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRole() == Role.ADMIN) {
            return saleHistoryRepository.findAll();
        } else if (user.getRole() == Role.GESTIONNAIRE) {
            if (user.getWarehouse() == null) {
                return List.of();
            }
            return saleHistoryRepository.findAll().stream()
                    .filter(sh -> sh.getWarehouse().getId().equals(user.getWarehouse().getId()))
                    .toList();
        }
        return List.of();
    }

    @Override
    @Transactional
    public void recordSale(SaleRequestDto request, String userId) {
         List<Stock> stocks = stockRepository.findByWarehouseId(request.getWarehouseId());
        Stock stock = stocks.stream()
                .filter(s -> s.getProduct().getId().equals(request.getProductId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Stock not found for this product in the warehouse"));

        int newQuantity = stock.getQuantityAvailable() - request.getQuantitySold();
        if (newQuantity < 0) {
            throw new RuntimeException("Insufficient stock");
        }
        
        stockService.updateStock(stock.getId(), newQuantity, userId);
    }

    @Override
    public List<SalesHistory> getHistoryByWarehouseId(String warehouseId) {
        return saleHistoryRepository.findAll().stream()
                .filter(sh -> sh.getWarehouse().getId().equals(warehouseId))
                .toList();
    }
}
