package com.STFAS.repository;

import com.STFAS.entity.SalesHistory;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SaleHistoryRepository extends MongoRepository<SalesHistory,String> {
}
