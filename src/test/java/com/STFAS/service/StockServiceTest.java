package com.STFAS.service;

import com.STFAS.dto.stock.request.StockRequestDto;
import com.STFAS.entity.Product;
import com.STFAS.entity.SalesHistory;
import com.STFAS.entity.Stock;
import com.STFAS.entity.User;
import com.STFAS.entity.Warehouse;
import com.STFAS.enums.Role;
import com.STFAS.repository.ProductRepository;
import com.STFAS.repository.SaleHistoryRepository;
import com.STFAS.repository.StockRepository;
import com.STFAS.repository.UserRepository;
import com.STFAS.repository.WarehouseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("StockService Unit Tests")
class StockServiceTest {

    @Mock
    private StockRepository stockRepository;

    @Mock
    private SaleHistoryRepository saleHistoryRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private WarehouseRepository warehouseRepository;

    @InjectMocks
    private StockService stockService;

    private Stock stock;
    private User adminUser;
    private User managerUser;
    private Product product;
    private Warehouse warehouse;
    private StockRequestDto stockRequestDto;

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
        stock.setAlertThreshold(20);

        adminUser = new User();
        adminUser.setId("admin-user");
        adminUser.setRole(Role.ADMIN);
        adminUser.setWarehouse(null);

        managerUser = new User();
        managerUser.setId("manager-user");
        managerUser.setRole(Role.GESTIONNAIRE);
        managerUser.setWarehouse(warehouse);

        stockRequestDto = new StockRequestDto();
        stockRequestDto.setProductId("product-123");
        stockRequestDto.setWarehouseId("warehouse-123");
        stockRequestDto.setQuantityAvailable(100);
        stockRequestDto.setAlertThreshold(20);
    }

    // ==================== updateStock Tests ====================
    @Test
    @DisplayName("updateStock - should successfully update stock quantity by ADMIN")
    void testUpdateStockByAdminSuccess() {
        // Arrange
        when(stockRepository.findById("stock-123")).thenReturn(Optional.of(stock));
        when(userRepository.findById("admin-user")).thenReturn(Optional.of(adminUser));
        when(stockRepository.save(any(Stock.class))).thenReturn(stock);

        // Act
        Stock result = stockService.updateStock("stock-123", 80, "admin-user");

        // Assert
        assertNotNull(result);
        assertEquals(80, result.getQuantityAvailable());
        verify(stockRepository, times(1)).save(any(Stock.class));
        verify(saleHistoryRepository, times(1)).save(any(SalesHistory.class));
    }

    @Test
    @DisplayName("updateStock - should successfully update stock quantity by GESTIONNAIRE for own warehouse")
    void testUpdateStockByManagerOwnWarehouseSuccess() {
        // Arrange
        when(stockRepository.findById("stock-123")).thenReturn(Optional.of(stock));
        when(userRepository.findById("manager-user")).thenReturn(Optional.of(managerUser));
        when(stockRepository.save(any(Stock.class))).thenReturn(stock);

        // Act
        Stock result = stockService.updateStock("stock-123", 80, "manager-user");

        // Assert
        assertNotNull(result);
        assertEquals(80, result.getQuantityAvailable());
        verify(stockRepository, times(1)).save(any(Stock.class));
    }

    @Test
    @DisplayName("updateStock - should deny access for GESTIONNAIRE managing different warehouse")
    void testUpdateStockByManagerDifferentWarehouseFailure() {
        // Arrange
        Warehouse differentWarehouse = new Warehouse();
        differentWarehouse.setId("warehouse-456");
        managerUser.setWarehouse(differentWarehouse);

        when(stockRepository.findById("stock-123")).thenReturn(Optional.of(stock));
        when(userRepository.findById("manager-user")).thenReturn(Optional.of(managerUser));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            stockService.updateStock("stock-123", 80, "manager-user")
        );
        assertEquals("Access Denied: You can only manage your own warehouse.", exception.getMessage());
        verify(stockRepository, never()).save(any(Stock.class));
    }

    @Test
    @DisplayName("updateStock - should throw exception when stock not found")
    void testUpdateStockNotFound() {
        // Arrange
        when(stockRepository.findById("stock-999")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            stockService.updateStock("stock-999", 80, "admin-user")
        );
        assertEquals("Stock not found", exception.getMessage());
    }

    @Test
    @DisplayName("updateStock - should throw exception when user not found")
    void testUpdateStockUserNotFound() {
        // Arrange
        when(stockRepository.findById("stock-123")).thenReturn(Optional.of(stock));
        when(userRepository.findById("user-999")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            stockService.updateStock("stock-123", 80, "user-999")
        );
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    @DisplayName("updateStock - should record sales history when quantity decreases")
    void testUpdateStockRecordSalesHistory() {
        // Arrange
        when(stockRepository.findById("stock-123")).thenReturn(Optional.of(stock));
        when(userRepository.findById("admin-user")).thenReturn(Optional.of(adminUser));
        when(stockRepository.save(any(Stock.class))).thenReturn(stock);

        // Act
        stockService.updateStock("stock-123", 80, "admin-user");

        // Assert
        verify(saleHistoryRepository, times(1)).save(any(SalesHistory.class));
    }

    @Test
    @DisplayName("updateStock - should not record sales history when quantity increases")
    void testUpdateStockNoSalesHistoryOnIncrease() {
        // Arrange
        when(stockRepository.findById("stock-123")).thenReturn(Optional.of(stock));
        when(userRepository.findById("admin-user")).thenReturn(Optional.of(adminUser));
        when(stockRepository.save(any(Stock.class))).thenReturn(stock);

        // Act
        stockService.updateStock("stock-123", 150, "admin-user");

        // Assert
        verify(saleHistoryRepository, never()).save(any(SalesHistory.class));
    }

    // ==================== getStocksByWarehouse Tests ====================
    @Test
    @DisplayName("getStocksByWarehouse - should return all stocks for warehouse")
    void testGetStocksByWarehouseSuccess() {
        // Arrange
        Stock stock2 = new Stock();
        stock2.setId("stock-456");
        stock2.setProduct(product);
        stock2.setWarehouse(warehouse);

        when(stockRepository.findByWarehouseId("warehouse-123")).thenReturn(List.of(stock, stock2));

        // Act
        List<Stock> result = stockService.getStocksByWarehouse("warehouse-123");

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(stockRepository, times(1)).findByWarehouseId("warehouse-123");
    }

    @Test
    @DisplayName("getStocksByWarehouse - should return empty list when warehouse has no stocks")
    void testGetStocksByWarehouseEmpty() {
        // Arrange
        when(stockRepository.findByWarehouseId("warehouse-123")).thenReturn(List.of());

        // Act
        List<Stock> result = stockService.getStocksByWarehouse("warehouse-123");

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ==================== getStockById Tests ====================
    @Test
    @DisplayName("getStockById - should return stock when exists")
    void testGetStockByIdSuccess() {
        // Arrange
        when(stockRepository.findById("stock-123")).thenReturn(Optional.of(stock));

        // Act
        Stock result = stockService.getStockById("stock-123");

        // Assert
        assertNotNull(result);
        assertEquals("stock-123", result.getId());
        assertEquals(100, result.getQuantityAvailable());
    }

    @Test
    @DisplayName("getStockById - should throw exception when stock not found")
    void testGetStockByIdNotFound() {
        // Arrange
        when(stockRepository.findById("stock-999")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            stockService.getStockById("stock-999")
        );
        assertEquals("Stock not found", exception.getMessage());
    }

    // ==================== getAllStocks Tests ====================
    @Test
    @DisplayName("getAllStocks - should return all stocks for ADMIN")
    void testGetAllStocksForAdminSuccess() {
        // Arrange
        Stock stock2 = new Stock();
        stock2.setId("stock-456");
        when(userRepository.findById("admin-user")).thenReturn(Optional.of(adminUser));
        when(stockRepository.findAll()).thenReturn(List.of(stock, stock2));

        // Act
        List<Stock> result = stockService.getAllStocks("admin-user");

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(stockRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("getAllStocks - should return warehouse-specific stocks for GESTIONNAIRE")
    void testGetAllStocksForManagerSuccess() {
        // Arrange
        when(userRepository.findById("manager-user")).thenReturn(Optional.of(managerUser));
        when(stockRepository.findByWarehouseId("warehouse-123")).thenReturn(List.of(stock));

        // Act
        List<Stock> result = stockService.getAllStocks("manager-user");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(stockRepository, times(1)).findByWarehouseId("warehouse-123");
    }

    @Test
    @DisplayName("getAllStocks - should return empty list for GESTIONNAIRE without warehouse")
    void testGetAllStocksManagerNoWarehouse() {
        // Arrange
        managerUser.setWarehouse(null);
        when(userRepository.findById("manager-user")).thenReturn(Optional.of(managerUser));

        // Act
        List<Stock> result = stockService.getAllStocks("manager-user");

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("getAllStocks - should throw exception when user not found")
    void testGetAllStocksUserNotFound() {
        // Arrange
        when(userRepository.findById("user-999")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            stockService.getAllStocks("user-999")
        );
        assertEquals("User not found", exception.getMessage());
    }

    // ==================== createStock Tests ====================
    @Test
    @DisplayName("createStock - should successfully create new stock")
    void testCreateStockSuccess() {
        // Arrange
        when(stockRepository.findByWarehouseId("warehouse-123")).thenReturn(List.of());
        when(productRepository.findById("product-123")).thenReturn(Optional.of(product));
        when(warehouseRepository.findById("warehouse-123")).thenReturn(Optional.of(warehouse));
        when(stockRepository.save(any(Stock.class))).thenReturn(stock);

        // Act
        Stock result = stockService.createStock(stockRequestDto);

        // Assert
        assertNotNull(result);
        assertEquals("product-123", result.getProduct().getId());
        assertEquals("warehouse-123", result.getWarehouse().getId());
        assertEquals(100, result.getQuantityAvailable());
        verify(stockRepository, times(1)).save(any(Stock.class));
    }

    @Test
    @DisplayName("createStock - should throw exception when stock already exists for product in warehouse")
    void testCreateStockAlreadyExists() {
        // Arrange
        when(stockRepository.findByWarehouseId("warehouse-123")).thenReturn(List.of(stock));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            stockService.createStock(stockRequestDto)
        );
        assertEquals("Stock already exists for this product in this warehouse", exception.getMessage());
        verify(stockRepository, never()).save(any(Stock.class));
    }

    @Test
    @DisplayName("createStock - should throw exception when product not found")
    void testCreateStockProductNotFound() {
        // Arrange
        when(stockRepository.findByWarehouseId("warehouse-123")).thenReturn(List.of());
        when(productRepository.findById("product-999")).thenReturn(Optional.empty());
        stockRequestDto.setProductId("product-999");

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            stockService.createStock(stockRequestDto)
        );
        assertEquals("Product not found", exception.getMessage());
    }

    @Test
    @DisplayName("createStock - should throw exception when warehouse not found")
    void testCreateStockWarehouseNotFound() {
        // Arrange
        when(stockRepository.findByWarehouseId("warehouse-999")).thenReturn(List.of());
        when(productRepository.findById("product-123")).thenReturn(Optional.of(product));
        when(warehouseRepository.findById("warehouse-999")).thenReturn(Optional.empty());
        stockRequestDto.setWarehouseId("warehouse-999");

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            stockService.createStock(stockRequestDto)
        );
        assertEquals("Warehouse not found", exception.getMessage());
    }
}
