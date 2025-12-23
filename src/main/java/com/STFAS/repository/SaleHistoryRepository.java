package com.STFAS.repository;

import com.STFAS.entity.SalesHistory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SaleHistoryRepository extends MongoRepository<SalesHistory,String> {
}
