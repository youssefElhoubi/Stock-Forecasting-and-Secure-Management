package com.STFAS.service;

import com.STFAS.dto.stock.request.StockRequestDto;
import com.STFAS.entity.*;
import com.STFAS.enums.Role;
import com.STFAS.repository.*;
import com.STFAS.service.repository.StockServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StockService implements StockServiceInterface {

    private final StockRepository stockRepository;
    private final SaleHistoryRepository saleHistoryRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;

    @Override
    @Transactional
    public Stock updateStock(String stockId, int newQuantity, String userId) {
        Stock stock = stockRepository.findById(stockId)
                .orElseThrow(() -> new RuntimeException("Stock not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRole() == Role.GESTIONNAIRE) {
            if (user.getWarehouse() == null || !user.getWarehouse().getId().equals(stock.getWarehouse().getId())) {
                throw new RuntimeException("Access Denied: You can only manage your own warehouse.");
            }
        }

        int oldQuantity = stock.getQuantityAvailable();
        if (newQuantity < oldQuantity) {
            int quantitySold = oldQuantity - newQuantity;
            SalesHistory history = new SalesHistory();
            history.setProduct(stock.getProduct());
            history.setWarehouse(stock.getWarehouse());
            history.setSaleDate(LocalDateTime.now());
            history.setQuantitySold(quantitySold);
            
            history.setDayOfWeek(history.getSaleDate().getDayOfWeek().toString());
            history.setMonth(history.getSaleDate().getMonth().toString());
            history.setYear(history.getSaleDate().getYear());

            saleHistoryRepository.save(history);
        }

        stock.setQuantityAvailable(newQuantity);
        return stockRepository.save(stock);
    }

    @Override
    public List<Stock> getStocksByWarehouse(String warehouseId) {
        return stockRepository.findByWarehouseId(warehouseId);
    }

    @Override
    public Stock getStockById(String id) {
        return stockRepository.findById(id).orElseThrow(() -> new RuntimeException("Stock not found"));
    }

    @Override
    public List<Stock> getAllStocks(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRole() == Role.ADMIN) {
            return stockRepository.findAll();
        } else if (user.getRole() == Role.GESTIONNAIRE) {
            if (user.getWarehouse() == null) {
                return List.of();
            }
            return stockRepository.findByWarehouseId(user.getWarehouse().getId());
        }
        return List.of();
    }

    @Override
    @Transactional
    public Stock createStock(StockRequestDto request) {
        boolean exists = stockRepository.findByWarehouseId(request.getWarehouseId()).stream()
                .anyMatch(s -> s.getProduct().getId().equals(request.getProductId()));

        if (exists) {
            throw new RuntimeException("Stock already exists for this product in this warehouse");
        }

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));
        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> new RuntimeException("Warehouse not found"));

        Stock stock = new Stock();
        stock.setProduct(product);
        stock.setWarehouse(warehouse);
        stock.setQuantityAvailable(request.getQuantityAvailable());
        stock.setAlertThreshold(request.getAlertThreshold());

        return stockRepository.save(stock);
    }
}
