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

        return stockMapper.toDto(stockRepository.save(stock));
    }

    @Override
    public StockResponseDto updateStock(String id,StockRequestDto request) {
        Stock stock = stockRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Stock not found"));

        stock.setProduct(productRepository.findById(request.getProductId()).orElseThrow(() -> new ResourceNotFoundException("Product not found")));
        stock.setWarehouse(warehouseRepository.findById(request.getWarehouseId()).orElseThrow(() -> new ResourceNotFoundException("wareHouse not found")));
        stock.setQuantityAvailable(request.getQuantityAvailable());


        return stockMapper.toDto(stockRepository.save(stock));
    }

    @Override
    public StockResponseDto getStockByProductAndWarehouse(String productId, String warehouseId) {
        Stock stock = stockRepository.findByProduct_IdAndWarehouse_Id(productId, warehouseId)
                .orElseThrow(() -> new ResourceNotFoundException("Stock not found for this product in the specified warehouse"));

        return stockMapper.toDto(stock);
    }

    @Override
    public List<StockResponseDto> getStocksByWarehouse(String warehouseId) {
        List<Stock> stocks =stockRepository.findAll().stream().filter((s)->s.getWarehouse().getId().equals(warehouseId)).toList();
        return stocks.stream().map(stockMapper::toDto).toList();
    }

    @Override
    public List<StockResponseDto> getAllStocks() {
        List<Stock> stocks =stockRepository.findAll();
        return stocks.stream().map(stockMapper::toDto).toList();
    }

    @Override
    public List<StockResponseDto> getLowStockAlerts(String warehouseId) {
        return List.of();
    }

    @Override
    public void adjustStockQuantity(String productId, String warehouseId, int quantityChange) {

    }
}
