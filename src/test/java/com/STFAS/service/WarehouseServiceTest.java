package com.STFAS.service;

import com.STFAS.dto.Warehouse.request.WarehouseRequestDto;
import com.STFAS.dto.Warehouse.request.WarehouseUpdateRequest;
import com.STFAS.dto.Warehouse.response.WarehouseResponseDto;
import com.STFAS.entity.Warehouse;
import com.STFAS.exception.BusinessRuleViolationException;
import com.STFAS.mapper.WarehouseMapper;
import com.STFAS.repository.WarehouseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("WarehouseService Unit Tests")
class WarehouseServiceTest {

    @Mock
    private WarehouseRepository warehouseRepository;

    @Mock
    private WarehouseMapper warehouseMapper;

    @InjectMocks
    private WarehouseService warehouseService;

    private Warehouse warehouse;
    private WarehouseRequestDto warehouseRequestDto;
    private WarehouseResponseDto warehouseResponseDto;
    private WarehouseUpdateRequest warehouseUpdateRequest;

    @BeforeEach
    void setUp() {
        warehouse = new Warehouse();
        warehouse.setId("warehouse-123");
        warehouse.setName("Main Warehouse");
        warehouse.setAddress("123 Main St");
        warehouse.setCity("New York");

        warehouseRequestDto = new WarehouseRequestDto();
        warehouseRequestDto.setName("Main Warehouse");
        warehouseRequestDto.setAddress("123 Main St");
        warehouseRequestDto.setCity("New York");

        warehouseResponseDto = new WarehouseResponseDto();
        warehouseResponseDto.setId("warehouse-123");
        warehouseResponseDto.setName("Main Warehouse");
        warehouseResponseDto.setAddress("123 Main St");
        warehouseResponseDto.setCity("New York");

        warehouseUpdateRequest = new WarehouseUpdateRequest();
        warehouseUpdateRequest.setName("Main Warehouse Updated");
        warehouseUpdateRequest.setAddress("456 Oak Ave");
        warehouseUpdateRequest.setCity("Los Angeles");
    }

    // ==================== createWarehouse Tests ====================
    @Test
    @DisplayName("createWarehouse - should successfully create a new warehouse")
    void testCreateWarehouseSuccess() {
        // Arrange
        when(warehouseRepository.findWarehouseByName(warehouseRequestDto.getName())).thenReturn(Optional.empty());
        when(warehouseMapper.toEntity(warehouseRequestDto)).thenReturn(warehouse);
        when(warehouseRepository.save(any(Warehouse.class))).thenReturn(warehouse);
        when(warehouseMapper.toDto(warehouse)).thenReturn(warehouseResponseDto);

        // Act
        WarehouseResponseDto result = warehouseService.createWarehouse(warehouseRequestDto);

        // Assert
        assertNotNull(result);
        assertEquals("Main Warehouse", result.getName());
        assertEquals("New York", result.getCity());
        verify(warehouseRepository, times(1)).findWarehouseByName(warehouseRequestDto.getName());
        verify(warehouseRepository, times(1)).save(any(Warehouse.class));
    }

    @Test
    @DisplayName("createWarehouse - should throw exception when warehouse with name already exists")
    void testCreateWarehouseDuplicateName() {
        // Arrange
        when(warehouseRepository.findWarehouseByName(warehouseRequestDto.getName())).thenReturn(Optional.of(warehouse));

        // Act & Assert
        BusinessRuleViolationException exception = assertThrows(BusinessRuleViolationException.class, () ->
            warehouseService.createWarehouse(warehouseRequestDto)
        );
        assertEquals("Warehouse already exists", exception.getMessage());
        verify(warehouseRepository, times(1)).findWarehouseByName(warehouseRequestDto.getName());
        verify(warehouseRepository, never()).save(any(Warehouse.class));
    }

    // ==================== updateWarehouse Tests ====================
    @Test
    @DisplayName("updateWarehouse - should successfully update warehouse details")
    void testUpdateWarehouseSuccess() {
        // Arrange
        Warehouse updatedWarehouse = new Warehouse();
        updatedWarehouse.setId("warehouse-123");
        updatedWarehouse.setName("Main Warehouse Updated");
        updatedWarehouse.setAddress("456 Oak Ave");
        updatedWarehouse.setCity("Los Angeles");

        WarehouseResponseDto updatedResponseDto = new WarehouseResponseDto();
        updatedResponseDto.setId("warehouse-123");
        updatedResponseDto.setName("Main Warehouse Updated");
        updatedResponseDto.setAddress("456 Oak Ave");
        updatedResponseDto.setCity("Los Angeles");

        when(warehouseRepository.findById("warehouse-123")).thenReturn(Optional.of(warehouse));
        when(warehouseRepository.save(any(Warehouse.class))).thenReturn(updatedWarehouse);
        when(warehouseMapper.toDto(updatedWarehouse)).thenReturn(updatedResponseDto);

        // Act
        WarehouseResponseDto result = warehouseService.updateWarehouse("warehouse-123", warehouseUpdateRequest);

        // Assert
        assertNotNull(result);
        assertEquals("Main Warehouse Updated", result.getName());
        assertEquals("456 Oak Ave", result.getAddress());
        assertEquals("Los Angeles", result.getCity());
        verify(warehouseRepository, times(1)).findById("warehouse-123");
        verify(warehouseRepository, times(1)).save(any(Warehouse.class));
    }

    @Test
    @DisplayName("updateWarehouse - should throw exception when warehouse not found")
    void testUpdateWarehouseNotFound() {
        // Arrange
        when(warehouseRepository.findById("warehouse-999")).thenReturn(Optional.empty());

        // Act & Assert
        BusinessRuleViolationException exception = assertThrows(BusinessRuleViolationException.class, () ->
            warehouseService.updateWarehouse("warehouse-999", warehouseUpdateRequest)
        );
        assertEquals("Warehouse not found", exception.getMessage());
        verify(warehouseRepository, never()).save(any(Warehouse.class));
    }

    // ==================== getWarehouseById Tests ====================
    @Test
    @DisplayName("getWarehouseById - should return warehouse when exists")
    void testGetWarehouseByIdSuccess() {
        // Arrange
        when(warehouseRepository.findById("warehouse-123")).thenReturn(Optional.of(warehouse));
        when(warehouseMapper.toDto(warehouse)).thenReturn(warehouseResponseDto);

        // Act
        WarehouseResponseDto result = warehouseService.getWarehouseById("warehouse-123");

        // Assert
        assertNotNull(result);
        assertEquals("Main Warehouse", result.getName());
        assertEquals("warehouse-123", result.getId());
        verify(warehouseRepository, times(1)).findById("warehouse-123");
    }

    @Test
    @DisplayName("getWarehouseById - should throw exception when warehouse not found")
    void testGetWarehouseByIdNotFound() {
        // Arrange
        when(warehouseRepository.findById("warehouse-999")).thenReturn(Optional.empty());

        // Act & Assert
        BusinessRuleViolationException exception = assertThrows(BusinessRuleViolationException.class, () ->
            warehouseService.getWarehouseById("warehouse-999")
        );
        assertEquals("Warehouse not found", exception.getMessage());
    }

    // ==================== getAllWarehouses Tests ====================
    @Test
    @DisplayName("getAllWarehouses - should return all warehouses")
    void testGetAllWarehousesSuccess() {
        // Arrange
        Warehouse warehouse2 = new Warehouse();
        warehouse2.setId("warehouse-456");
        warehouse2.setName("Secondary Warehouse");
        warehouse2.setCity("Chicago");

        WarehouseResponseDto warehouseResponseDto2 = new WarehouseResponseDto();
        warehouseResponseDto2.setId("warehouse-456");
        warehouseResponseDto2.setName("Secondary Warehouse");
        warehouseResponseDto2.setCity("Chicago");

        when(warehouseRepository.findAll()).thenReturn(List.of(warehouse, warehouse2));
        when(warehouseMapper.toDto(warehouse)).thenReturn(warehouseResponseDto);
        when(warehouseMapper.toDto(warehouse2)).thenReturn(warehouseResponseDto2);

        // Act
        List<WarehouseResponseDto> result = warehouseService.getAllWarehouses();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Main Warehouse", result.get(0).getName());
        assertEquals("Secondary Warehouse", result.get(1).getName());
        verify(warehouseRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("getAllWarehouses - should return empty list when no warehouses exist")
    void testGetAllWarehousesEmpty() {
        // Arrange
        when(warehouseRepository.findAll()).thenReturn(List.of());

        // Act
        List<WarehouseResponseDto> result = warehouseService.getAllWarehouses();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ==================== deleteWarehouse Tests ====================
    @Test
    @DisplayName("deleteWarehouse - should successfully delete warehouse")
    void testDeleteWarehouseSuccess() {
        // Arrange - No need to verify warehouse exists in this implementation
        // Act
        warehouseService.deleteWarehouse("warehouse-123");

        // Assert
        verify(warehouseRepository, times(1)).deleteById("warehouse-123");
    }

    @Test
    @DisplayName("deleteWarehouse - should call deleteById regardless of existence")
    void testDeleteWarehouseNonExistent() {
        // Act
        warehouseService.deleteWarehouse("warehouse-999");

        // Assert
        verify(warehouseRepository, times(1)).deleteById("warehouse-999");
    }
}
