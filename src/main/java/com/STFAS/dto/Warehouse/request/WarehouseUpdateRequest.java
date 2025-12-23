package com.STFAS.dto.Warehouse.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class WarehouseUpdateRequest {
    private String name;
    private String city;
    private String address;
}
