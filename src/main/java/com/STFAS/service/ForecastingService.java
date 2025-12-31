package com.STFAS.service;

import com.STFAS.entity.*;
import com.STFAS.enums.Role;
import com.STFAS.repository.*;
import com.STFAS.service.repository.ForecastingServiceInterface;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ForecastingService implements ForecastingServiceInterface {

    private final ForecastRepository forecastRepository;
    private final StockRepository stockRepository;
    private final SaleHistoryRepository saleHistoryRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;
    private final UserRepository userRepository;

    private final OllamaChatModel chatModel;
    private final ObjectMapper objectMapper;

    // Injection de la configuration du Prompt
    private final PromptTemplate stockPromptTemplate;

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
        // 1. Récupération de l'historique (Optimisé : il vaudrait mieux une requête custom en DB)
        List<SalesHistory> history = saleHistoryRepository.findAll().stream()
                .filter(h -> h.getProduct().getId().equals(product.getId()) &&
                        h.getWarehouse().getId().equals(warehouse.getId()))
                .sorted((a, b) -> b.getSaleDate().compareTo(a.getSaleDate()))
                .limit(30)
                .toList();

        if (history.isEmpty()) return;

        // 2. Récupération du stock
        Optional<Stock> stockOpt = stockRepository.findByWarehouseId(warehouse.getId()).stream()
                .filter(s -> s.getProduct().getId().equals(product.getId()))
                .findFirst();

        if (stockOpt.isEmpty()) return;
        Stock stock = stockOpt.get();

        // 3. Préparation des données pour le PromptTemplate
        String historyData = history.stream()
                .map(h -> h.getSaleDate().toLocalDate() + ":" + h.getQuantitySold())
                .collect(Collectors.joining(", "));

        // Utilisation du template avec les variables définies dans AiPromptConfig
        Map<String, Object> promptVariables = Map.of(
                "productName", product.getName(),
                "warehouseName", warehouse.getName(),
                "currentStock", stock.getQuantityAvailable(),
                "alertThreshold", stock.getAlertThreshold(),
                "salesHistory", historyData
        );

        Prompt prompt = stockPromptTemplate.create(promptVariables);

        try {
            // 4. Appel Ollama
            String response = chatModel.call(prompt).getResult().getOutput().getText();

            // Nettoyage de la réponse (pour enlever les éventuels backticks ```json ... ```)
            String jsonContent = extractJson(response);
            JsonNode root = objectMapper.readTree(jsonContent);

            // 5. Sauvegarde
            Forecast forecast = new Forecast();
            forecast.setProduct(product);
            forecast.setWarehouse(warehouse);
            forecast.setForecastDate(LocalDate.now());

            forecast.setPredictedQuantity30Days(root.get("predictedSales").asInt());
            forecast.setConfidenceLevel(root.get("confidence").asDouble());
            forecast.setRecommendation(root.get("recommendation").asText());

            forecastRepository.save(forecast);
            log.info("Prévision générée pour : {} dans {}", product.getName(), warehouse.getName());

        } catch (Exception e) {
            log.error("Erreur IA pour {} : {}", product.getName(), e.getMessage());
        }
    }

    /**
     * Utilitaire pour extraire proprement le JSON de la réponse de l'IA
     */
    private String extractJson(String rawResponse) {
        int start = rawResponse.indexOf("{");
        int end = rawResponse.lastIndexOf("}");
        if (start != -1 && end != -1 && end > start) {
            return rawResponse.substring(start, end + 1);
        }
        return rawResponse;
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