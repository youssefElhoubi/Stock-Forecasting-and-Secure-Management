package com.STFAS.repository;

import com.STFAS.entity.Stock;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StockRepository extends MongoRepository<Stock,String> {
    Optional<Stock> findByProduct_IdAndWarehouse_Id(String productId, String warehouseId);
}
