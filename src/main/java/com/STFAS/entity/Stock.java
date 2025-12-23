package com.STFAS.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "stocks")
public class Stock {
    @Id
    private String id;

    @DBRef
    private Product product;

    @DBRef
    private Warehouse warehouse;

    private int quantityAvailable;
    private int alertThreshold;
}