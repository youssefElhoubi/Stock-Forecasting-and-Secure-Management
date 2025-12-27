package com.STFAS.service;

import com.STFAS.entity.Forecast;
import com.STFAS.entity.SalesHistory;
import com.STFAS.entity.Stock;
import com.STFAS.repository.ForecastRepository;
import com.STFAS.repository.SaleHistoryRepository;
import com.STFAS.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ForecastService {

    private final StockRepository stockRepository;
    private final SaleHistoryRepository saleHistoryRepository;
    private final ForecastRepository forecastRepository;

    public Forecast generateForecast(String stockId) {
        Stock stock = stockRepository.findById(stockId)
                .orElseThrow(() -> new RuntimeException("Stock not found"));

        List<SalesHistory> history = saleHistoryRepository.findByProductAndWarehouse(stock.getProduct(), stock.getWarehouse());

        double totalSold = history.stream().mapToInt(SalesHistory::getQuantitySold).sum();
        double avgDailySales = history.isEmpty() ? 0 : totalSold / Math.max(1, history.size()); // Simplified: assuming 1 record per sale event, not per day. Ideally should divide by date range.
        
        // Better avg calculation:
        // If we have history, find min date and max date (or now).
        // days = days between min and now.
        // avg = total / days.
        
        if (!history.isEmpty()) {
             java.time.LocalDateTime minDate = history.stream()
                    .map(SalesHistory::getSaleDate)
                    .min(java.time.LocalDateTime::compareTo)
                    .orElse(java.time.LocalDateTime.now());
             
             long days = java.time.Duration.between(minDate, java.time.LocalDateTime.now()).toDays();
             days = Math.max(1, days);
             avgDailySales = totalSold / (double) days;
        }

        int predictedConsumption30Days = (int) (avgDailySales * 30);
        int daysUntilStockout = avgDailySales > 0 ? (int) (stock.getQuantityAvailable() / avgDailySales) : 999;

        Forecast forecast = new Forecast();
        forecast.setProduct(stock.getProduct());
        forecast.setWarehouse(stock.getWarehouse());
        forecast.setForecastDate(LocalDate.now());
        forecast.setPredictedQuantity30Days(predictedConsumption30Days);
        forecast.setConfidenceLevel(85.0); // Static for now, would be calculated by AI model

        if (daysUntilStockout < 7) {
            forecast.setRecommendation("URGENT: Order " + (predictedConsumption30Days - stock.getQuantityAvailable()) + " units.");
        } else if (daysUntilStockout < 14) {
             forecast.setRecommendation("WARNING: Stock low. Consider ordering " + (predictedConsumption30Days / 2) + " units.");
        } else {
            forecast.setRecommendation("Stock levels are healthy.");
        }

        return forecastRepository.save(forecast);
    }
}
