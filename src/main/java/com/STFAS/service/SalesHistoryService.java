package com.STFAS.service;

import com.STFAS.dto.sales.request.SaleRequestDto;
import com.STFAS.dto.stock.request.StockRequestDto;
import com.STFAS.entity.SalesHistory;
import com.STFAS.entity.Stock;
import com.STFAS.entity.User;
import com.STFAS.enums.Role;
import com.STFAS.repository.SaleHistoryRepository;
import com.STFAS.repository.StockRepository;
import com.STFAS.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SalesHistoryService {

    private final SaleHistoryRepository saleHistoryRepository;
    private final StockRepository stockRepository;
    private final UserRepository userRepository;
    private final StockService stockService;

    public List<SalesHistory> getHistory(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRole() == Role.ADMIN) {
            return saleHistoryRepository.findAll();
        } else if (user.getRole() == Role.GESTIONNAIRE) {
            if (user.getWarehouse() == null) {
                return List.of();
            }
            // We need a method to find by warehouse in SaleHistoryRepository
            // For now, let's filter in memory or add the method.
            // Ideally add findByWarehouse(Warehouse w) to repo.
            return saleHistoryRepository.findAll().stream()
                    .filter(sh -> sh.getWarehouse().getId().equals(user.getWarehouse().getId()))
                    .toList();
        }
        return List.of();
    }

    @Transactional
    public void recordSale(SaleRequestDto request, String userId) {
        // Find stock to decrement
        // We need to find the stock entry for this product and warehouse
        List<Stock> stocks = stockRepository.findByWarehouseId(request.getWarehouseId());
        Stock stock = stocks.stream()
                .filter(s -> s.getProduct().getId().equals(request.getProductId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Stock not found for this product in the warehouse"));

        // Use StockService to update stock (it handles security and history recording)
        // But wait, StockService.updateStock takes newQuantity. We have quantitySold.
        int newQuantity = stock.getQuantityAvailable() - request.getQuantitySold();
        if (newQuantity < 0) {
            throw new RuntimeException("Insufficient stock");
        }
        StockRequestDto dto = new StockRequestDto().builder()
                .productId(stock.getProduct().getId())
                .quantityAvailable(newQuantity)
                .warehouseId(stock.getWarehouse().getId())
                .build();
        
        stockService.updateStock(stock.getId(), dto);
    }
}
