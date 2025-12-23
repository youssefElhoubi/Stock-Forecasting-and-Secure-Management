package com.STFAS.dto.Warehouse.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class WarehouseRequestDto {
    @NotBlank(message = "Name is required")
    private String name;
    @NotBlank
    private String city;
    @NotBlank
    private String address;
}
