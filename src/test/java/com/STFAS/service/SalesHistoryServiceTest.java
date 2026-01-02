package com.STFAS.service;

import com.STFAS.dto.sales.request.SaleRequestDto;
import com.STFAS.entity.SalesHistory;
import com.STFAS.entity.Stock;
import com.STFAS.entity.User;
import com.STFAS.entity.Warehouse;
import com.STFAS.entity.Product;
import com.STFAS.enums.Role;
import com.STFAS.repository.SaleHistoryRepository;
import com.STFAS.repository.StockRepository;
import com.STFAS.repository.UserRepository;
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
@DisplayName("SalesHistoryService Unit Tests")
class SalesHistoryServiceTest {

    @Mock
    private SaleHistoryRepository saleHistoryRepository;

    @Mock
    private StockRepository stockRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private StockService stockService;

    @InjectMocks
    private SalesHistoryService salesHistoryService;

    private User adminUser;
    private User managerUser;
    private SalesHistory salesHistory;
    private Stock stock;
    private Product product;
    private Warehouse warehouse;
    private SaleRequestDto saleRequestDto;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId("product-123");
        product.setName("Laptop");

        warehouse = new Warehouse();
        warehouse.setId("warehouse-123");
        warehouse.setName("Main Warehouse");

        stock = new Stock();
        stock.setId("stock-123");
        stock.setProduct(product);
        stock.setWarehouse(warehouse);
        stock.setQuantityAvailable(100);

        adminUser = new User();
        adminUser.setId("admin-user");
        adminUser.setRole(Role.ADMIN);
        adminUser.setWarehouse(null);

        managerUser = new User();
        managerUser.setId("manager-user");
        managerUser.setRole(Role.GESTIONNAIRE);
        managerUser.setWarehouse(warehouse);

        salesHistory = new SalesHistory();
        salesHistory.setId("sale-123");
        salesHistory.setProduct(product);
        salesHistory.setWarehouse(warehouse);
        salesHistory.setQuantitySold(10);

        saleRequestDto = new SaleRequestDto();
        saleRequestDto.setProductId("product-123");
        saleRequestDto.setWarehouseId("warehouse-123");
        saleRequestDto.setQuantitySold(10);
    }

    // ==================== recordSale Tests ====================
    @Test
    @DisplayName("recordSale - should successfully record a sale")
    void testRecordSaleSuccess() {
        // Arrange
        when(stockRepository.findByWarehouseId("warehouse-123")).thenReturn(List.of(stock));
        doNothing().when(stockService).updateStock("stock-123", 90, "admin-user");

        // Act
        salesHistoryService.recordSale(saleRequestDto, "admin-user");

        // Assert
        verify(stockService, times(1)).updateStock("stock-123", 90, "admin-user");
    }

    @Test
    @DisplayName("recordSale - should throw exception when stock not found")
    void testRecordSaleStockNotFound() {
        // Arrange
        when(stockRepository.findByWarehouseId("warehouse-123")).thenReturn(List.of());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            salesHistoryService.recordSale(saleRequestDto, "admin-user")
        );
        assertEquals("Stock not found for this product in the warehouse", exception.getMessage());
        verify(stockService, never()).updateStock(anyString(), anyInt(), anyString());
    }

    @Test
    @DisplayName("recordSale - should throw exception when quantity sold exceeds available stock")
    void testRecordSaleInsufficientStock() {
        // Arrange
        saleRequestDto.setQuantitySold(150); // More than available (100)
        when(stockRepository.findByWarehouseId("warehouse-123")).thenReturn(List.of(stock));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            salesHistoryService.recordSale(saleRequestDto, "admin-user")
        );
        assertEquals("Insufficient stock", exception.getMessage());
        verify(stockService, never()).updateStock(anyString(), anyInt(), anyString());
    }

    @Test
    @DisplayName("recordSale - should allow sale with exact available quantity")
    void testRecordSaleExactQuantity() {
        // Arrange
        saleRequestDto.setQuantitySold(100); // Exact available quantity
        when(stockRepository.findByWarehouseId("warehouse-123")).thenReturn(List.of(stock));
        doNothing().when(stockService).updateStock("stock-123", 0, "admin-user");

        // Act
        salesHistoryService.recordSale(saleRequestDto, "admin-user");

        // Assert
        verify(stockService, times(1)).updateStock("stock-123", 0, "admin-user");
    }

    @Test
    @DisplayName("recordSale - should not process when quantity is zero")
    void testRecordSaleZeroQuantity() {
        // Arrange
        saleRequestDto.setQuantitySold(0);
        when(stockRepository.findByWarehouseId("warehouse-123")).thenReturn(List.of(stock));
        doNothing().when(stockService).updateStock("stock-123", 100, "admin-user");

        // Act
        salesHistoryService.recordSale(saleRequestDto, "admin-user");

        // Assert
        verify(stockService, times(1)).updateStock("stock-123", 100, "admin-user");
    }

    // ==================== getHistory Tests ====================
    @Test
    @DisplayName("getHistory - should return all sales for ADMIN")
    void testGetHistoryForAdminSuccess() {
        // Arrange
        when(userRepository.findById("admin-user")).thenReturn(Optional.of(adminUser));
        when(saleHistoryRepository.findAll()).thenReturn(List.of(salesHistory));

        // Act
        List<SalesHistory> result = salesHistoryService.getHistory("admin-user");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(saleHistoryRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("getHistory - should return warehouse-filtered sales for GESTIONNAIRE")
    void testGetHistoryForManagerSuccess() {
        // Arrange
        when(userRepository.findById("manager-user")).thenReturn(Optional.of(managerUser));
        when(saleHistoryRepository.findAll()).thenReturn(List.of(salesHistory));

        // Act
        List<SalesHistory> result = salesHistoryService.getHistory("manager-user");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("warehouse-123", result.get(0).getWarehouse().getId());
    }

    @Test
    @DisplayName("getHistory - should return empty list for GESTIONNAIRE without warehouse")
    void testGetHistoryManagerNoWarehouse() {
        // Arrange
        managerUser.setWarehouse(null);
        when(userRepository.findById("manager-user")).thenReturn(Optional.of(managerUser));

        // Act
        List<SalesHistory> result = salesHistoryService.getHistory("manager-user");

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("getHistory - should filter sales by manager's warehouse only")
    void testGetHistoryManagerMultipleWarehouses() {
        // Arrange
        Warehouse otherWarehouse = new Warehouse();
        otherWarehouse.setId("warehouse-456");

        SalesHistory otherSale = new SalesHistory();
        otherSale.setId("sale-456");
        otherSale.setProduct(product);
        otherSale.setWarehouse(otherWarehouse);
        otherSale.setQuantitySold(5);

        when(userRepository.findById("manager-user")).thenReturn(Optional.of(managerUser));
        when(saleHistoryRepository.findAll()).thenReturn(List.of(salesHistory, otherSale));

        // Act
        List<SalesHistory> result = salesHistoryService.getHistory("manager-user");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("warehouse-123", result.get(0).getWarehouse().getId());
    }

    @Test
    @DisplayName("getHistory - should throw exception when user not found")
    void testGetHistoryUserNotFound() {
        // Arrange
        when(userRepository.findById("user-999")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            salesHistoryService.getHistory("user-999")
        );
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    @DisplayName("getHistory - should return empty list when no sales for ADMIN")
    void testGetHistoryAdminEmpty() {
        // Arrange
        when(userRepository.findById("admin-user")).thenReturn(Optional.of(adminUser));
        when(saleHistoryRepository.findAll()).thenReturn(List.of());

        // Act
        List<SalesHistory> result = salesHistoryService.getHistory("admin-user");

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ==================== getHistoryByWarehouseId Tests ====================
    @Test
    @DisplayName("getHistoryByWarehouseId - should return sales for specific warehouse")
    void testGetHistoryByWarehouseIdSuccess() {
        // Arrange
        when(saleHistoryRepository.findAll()).thenReturn(List.of(salesHistory));

        // Act
        List<SalesHistory> result = salesHistoryService.getHistoryByWarehouseId("warehouse-123");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("warehouse-123", result.get(0).getWarehouse().getId());
    }

    @Test
    @DisplayName("getHistoryByWarehouseId - should filter multiple sales by warehouse")
    void testGetHistoryByWarehouseIdMultipleSales() {
        // Arrange
        SalesHistory sale2 = new SalesHistory();
        sale2.setId("sale-789");
        sale2.setProduct(product);
        sale2.setWarehouse(warehouse);
        sale2.setQuantitySold(5);

        when(saleHistoryRepository.findAll()).thenReturn(List.of(salesHistory, sale2));

        // Act
        List<SalesHistory> result = salesHistoryService.getHistoryByWarehouseId("warehouse-123");

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("getHistoryByWarehouseId - should return empty list when no sales in warehouse")
    void testGetHistoryByWarehouseIdEmpty() {
        // Arrange
        when(saleHistoryRepository.findAll()).thenReturn(List.of());

        // Act
        List<SalesHistory> result = salesHistoryService.getHistoryByWarehouseId("warehouse-999");

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("getHistoryByWarehouseId - should ignore sales from other warehouses")
    void testGetHistoryByWarehouseIdFiltering() {
        // Arrange
        Warehouse otherWarehouse = new Warehouse();
        otherWarehouse.setId("warehouse-456");

        SalesHistory otherSale = new SalesHistory();
        otherSale.setId("sale-456");
        otherSale.setProduct(product);
        otherSale.setWarehouse(otherWarehouse);
        otherSale.setQuantitySold(5);

        when(saleHistoryRepository.findAll()).thenReturn(List.of(salesHistory, otherSale));

        // Act
        List<SalesHistory> result = salesHistoryService.getHistoryByWarehouseId("warehouse-123");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("warehouse-123", result.get(0).getWarehouse().getId());
    }
}
