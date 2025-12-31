package com.STFAS.config;

import com.STFAS.entity.*;
import com.STFAS.enums.Role;
import com.STFAS.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final WarehouseRepository warehouseRepository;
    private final ProductRepository productRepository;
    private final StockRepository stockRepository;
    private final SaleHistoryRepository saleHistoryRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // 1. Seed Admin
        if (userRepository.findByEmail("nihad@gmail.com").isEmpty()) {
            User admin = new User();
            admin.setName("Admin");
            admin.setEmail("nihad@gmail.com");
            admin.setPassword(passwordEncoder.encode("password"));
            admin.setRole(Role.ADMIN);
            userRepository.save(admin);
            System.out.println("Admin user seeded successfully.");
        }

        // 2. Seed Sample Data for AI if empty
        if (warehouseRepository.findAll().isEmpty()) {
            Warehouse warehouse = new Warehouse();
            warehouse.setName("Entrepôt Principal");
            warehouse.setCity("Casablanca");
            warehouse.setAddress("Zone Industrielle");
            warehouse = warehouseRepository.save(warehouse);

            Product product = new Product();
            product.setName("Ordinateur Portable Pro");
            product.setCategory("Électronique");
            product.setSellingPrice(1200.0);
            product = productRepository.save(product);

            Stock stock = new Stock();
            stock.setProduct(product);
            stock.setWarehouse(warehouse);
            stock.setQuantityAvailable(50);
            stock.setAlertThreshold(10);
            stockRepository.save(stock);

            // Seed 30 days of sales history
            Random random = new Random();
            for (int i = 1; i <= 30; i++) {
                SalesHistory history = new SalesHistory();
                history.setProduct(product);
                history.setWarehouse(warehouse);
                history.setSaleDate(LocalDateTime.now().minusDays(i));
                history.setQuantitySold(random.nextInt(5) + 1); // 1 to 5 sales per day
                saleHistoryRepository.save(history);
            }
            System.out.println("Sample data for AI seeded successfully.");
        }
    }
}
