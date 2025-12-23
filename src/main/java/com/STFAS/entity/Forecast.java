package com.STFAS.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "forecasts")
public class Forecast {
    @Id
    private String id;

    @DBRef
    private Product product;
    @DBRef
    private Warehouse warehouse;

    private LocalDate forecastDate;
    private int predictedQuantity30Days;
    private double confidenceLevel; // e.g., 85.5%
    private String recommendation;
}