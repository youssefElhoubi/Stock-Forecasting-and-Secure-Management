package com.STFAS.repository;

import com.STFAS.entity.Forecast;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ForecastRepository extends MongoRepository<Forecast,String> {
    List<Forecast> findByProductId(String productId);
    List<Forecast> findByWarehouseId(String warehouseId);
}
