package com.STFAS.repository;

import com.STFAS.entity.Warehouse;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface WarehouseRepository extends MongoRepository<Warehouse,String> {
}
