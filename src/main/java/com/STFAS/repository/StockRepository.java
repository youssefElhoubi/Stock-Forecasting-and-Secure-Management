package com.STFAS.repository;

import com.STFAS.entity.Stock;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockRepository extends MongoRepository<Stock,String> {

    List<Stock> findByWarehouseId(String warehouseId);
}
