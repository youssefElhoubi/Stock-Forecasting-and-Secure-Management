package com.STFAS.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "sales_history")
public class SalesHistory {
    @Id
    private String id;

    @DBRef
    private Product product;
    @DBRef
    private Warehouse warehouse;

    private LocalDateTime saleDate;
    private int quantitySold;

    // Calculated fields for easier AI processing
    private String dayOfWeek;
    private String month;
    private int year;
}