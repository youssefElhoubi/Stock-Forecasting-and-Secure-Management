package com.STFAS.service.repository;

import com.STFAS.entity.Forecast;
import com.STFAS.entity.Product;
import com.STFAS.entity.Warehouse;

import java.util.List;

public interface ForecastingServiceInterface {

    void generateForecasts();
    List<Forecast> getAllForecasts();
    List<Forecast> getForecastsByWarehouse(String warehouseId);
    List<Forecast> getMyWarehouseForecasts(String userId);
}
