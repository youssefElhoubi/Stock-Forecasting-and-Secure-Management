package com.STFAS.service;

import com.STFAS.entity.*;
import com.STFAS.enums.Role;
import com.STFAS.repository.*;
import com.STFAS.service.repository.ForecastingServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ForecastingService implements ForecastingServiceInterface {

    private final ForecastRepository forecastRepository;
    private final StockRepository stockRepository;
    private final SaleHistoryRepository saleHistoryRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;
    private final UserRepository userRepository;



    @Override
    @Transactional
    public void generateForecasts() {
        List<Warehouse> warehouses = warehouseRepository.findAll();
        List<Product> products = productRepository.findAll();

        for (Warehouse warehouse : warehouses) {
            for (Product product : products) {
                generateForecastForProductInWarehouse(product, warehouse);
            }
        }
    }

    private void generateForecastForProductInWarehouse(Product product, Warehouse warehouse) {
        List<SalesHistory> history = saleHistoryRepository.findAll().stream()
                .filter(h -> h.getProduct().getId().equals(product.getId()) &&
                        h.getWarehouse().getId().equals(warehouse.getId()))
                .toList();

        if (history.isEmpty()) return;

        double totalSales = history.stream().mapToInt(SalesHistory::getQuantitySold).sum();
         long uniqueDays = history.stream().map(h -> h.getSaleDate().toLocalDate()).distinct().count();
        if (uniqueDays == 0) uniqueDays = 1;

        double avgDailySales = totalSales / (double) uniqueDays;
        Optional<Stock> stockOpt = stockRepository.findByWarehouseId(warehouse.getId()).stream()
                .filter(s -> s.getProduct().getId().equals(product.getId()))
                .findFirst();

        if (stockOpt.isEmpty()) return;
        Stock stock = stockOpt.get();

        int currentStock = stock.getQuantityAvailable();
        int daysUntilStockout = (int) (currentStock / (avgDailySales == 0 ? 1 : avgDailySales));

        Forecast forecast = new Forecast();
        forecast.setProduct(product);
        forecast.setWarehouse(warehouse);
        forecast.setForecastDate(LocalDate.now());

        int predictedSales30Days = (int) (avgDailySales * 30);
        forecast.setPredictedQuantity30Days(predictedSales30Days);

        forecast.setConfidenceLevel(85.0);

        if (daysUntilStockout < 7) {
            int reorderQty = predictedSales30Days - currentStock;
            if (reorderQty < 0) reorderQty = 0;
            forecast.setRecommendation("CRITICAL: Stockout in " + daysUntilStockout + " days. Order " + reorderQty + " units.");
        } else if (daysUntilStockout < 30) {
            int reorderQty = predictedSales30Days - currentStock;
            if (reorderQty < 0) reorderQty = 0;
            forecast.setRecommendation("WARNING: Stockout in " + daysUntilStockout + " days. Consider ordering " + reorderQty + " units.");
        } else {
            forecast.setRecommendation("Stock sufficient for " + daysUntilStockout + " days.");
        }

        forecastRepository.save(forecast);
    }

    @Override
    public List<Forecast> getAllForecasts() {
        return forecastRepository.findAll();
    }

    @Override
    public List<Forecast> getForecastsByWarehouse(String warehouseId) {
        return forecastRepository.findByWarehouseId(warehouseId);
    }

    @Override
    public List<Forecast> getMyWarehouseForecasts(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRole() == Role.GESTIONNAIRE && user.getWarehouse() != null) {
            return forecastRepository.findByWarehouseId(user.getWarehouse().getId());
        }
        return new ArrayList<>();
    }
}
