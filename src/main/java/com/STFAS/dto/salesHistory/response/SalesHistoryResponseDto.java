package com.STFAS.dto.salesHistory.response;

import lombok.Data;

@Data
public class SalesHistoryResponseDto {
    private String id;
    private String productName;
    private String warehouseName;
    private int quantitySold;
    private String saleDate; // ISO String
}
