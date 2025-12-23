package com.STFAS.repository;

import com.STFAS.entity.Forecast;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ForecastRepository extends MongoRepository<Forecast,String> {
}
