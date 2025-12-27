package com.STFAS.repository;

import com.STFAS.entity.Product;
import com.STFAS.entity.SalesHistory;
import com.STFAS.entity.Warehouse;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SaleHistoryRepository extends MongoRepository<SalesHistory,String> {
    List<SalesHistory> findByProductAndWarehouse(Product product, Warehouse warehouse);
}
