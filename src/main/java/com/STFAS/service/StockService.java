package com.STFAS.service;

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
public class StockService {

    private final StockRepository stockRepository;
    private final SaleHistoryRepository saleHistoryRepository;
    private final UserRepository userRepository;

    @Transactional
    public Stock updateStock(String stockId, int newQuantity, String userId) {
        Stock stock = stockRepository.findById(stockId)
                .orElseThrow(() -> new RuntimeException("Stock not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Security Check for GESTIONNAIRE
        if (user.getRole() == Role.GESTIONNAIRE) {
            if (user.getWarehouse() == null || !user.getWarehouse().getId().equals(stock.getWarehouse().getId())) {
                throw new RuntimeException("Access Denied: You can only manage your own warehouse.");
            }
        }

        int oldQuantity = stock.getQuantityAvailable();
        if (newQuantity < oldQuantity) {
            // Record Sale
            int quantitySold = oldQuantity - newQuantity;
            SalesHistory history = new SalesHistory();
            history.setProduct(stock.getProduct());
            history.setWarehouse(stock.getWarehouse());
            history.setSaleDate(LocalDateTime.now());
            history.setQuantitySold(quantitySold);
            
            // Set calculated fields for AI
            history.setDayOfWeek(history.getSaleDate().getDayOfWeek().toString());
            history.setMonth(history.getSaleDate().getMonth().toString());
            history.setYear(history.getSaleDate().getYear());

            saleHistoryRepository.save(history);
        }

        stock.setQuantityAvailable(newQuantity);
        return stockRepository.save(stock);
    }

    public List<Stock> getStocksByWarehouse(String warehouseId) {
        return stockRepository.findByWarehouseId(warehouseId);
    }
    
    public Stock getStockById(String id) {
        return stockRepository.findById(id).orElseThrow(() -> new RuntimeException("Stock not found"));
    }
}
