package com.STFAS.service;

import com.STFAS.dto.stock.request.StockRequestDto;
import com.STFAS.dto.stock.response.StockResponseDto;
import com.STFAS.entity.Product;
import com.STFAS.entity.Stock;
import com.STFAS.entity.Warehouse;
import com.STFAS.exception.ResourceNotFoundException;
import com.STFAS.mapper.StockMapper;
import com.STFAS.repository.ProductRepository;
import com.STFAS.repository.StockRepository;
import com.STFAS.repository.WarehouseRepository;
import com.STFAS.service.repository.StockServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StockService implements StockServiceInterface {
    private final StockRepository stockRepository;
    private final ProductRepository productRepository;
    private final StockMapper stockMapper;
    private final WarehouseRepository warehouseRepository;

    @Override
    public StockResponseDto create(StockRequestDto request) {
        Stock stock = stockMapper.toEntity(request);

        Product product = productRepository.findById(request.getProductId()).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId()).orElseThrow(() -> new ResourceNotFoundException("Warehouse not found"));

        stock.setWarehouse(warehouse);
        stock.setProduct(product);

        stockRepository.save(stock);
        return null;
    }

    @Override
    public StockResponseDto updateStock(StockRequestDto request) {
        return null;
    }

    @Override
    public StockResponseDto getStockByProductAndWarehouse(String productId, String warehouseId) {
        return null;
    }

    @Override
    public List<StockResponseDto> getStocksByWarehouse(String warehouseId) {
        return List.of();
    }

    @Override
    public List<StockResponseDto> getAllStocks() {
        return List.of();
    }

    @Override
    public List<StockResponseDto> getLowStockAlerts(String warehouseId) {
        return List.of();
    }

    @Override
    public void adjustStockQuantity(String productId, String warehouseId, int quantityChange) {

    }
}
