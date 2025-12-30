package com.STFAS.controller;

import com.STFAS.entity.Forecast;
import com.STFAS.entity.User;
import com.STFAS.repository.UserRepository;
import com.STFAS.service.ForecastingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/forecasts")
@RequiredArgsConstructor
public class ForecastingController {
    private final ForecastingService forecastingService;
    private final UserRepository userRepository;

    //post /api/forecasts/generate //ADMIN
    @PostMapping("/generate")
    public ResponseEntity<Void> generateForecasts() {
        forecastingService.generateForecasts();
        return ResponseEntity.ok().build();
    }

    //get /api/forecasts  //ADMIN
    @GetMapping
    public ResponseEntity<List<Forecast>> getAllForecasts() {
        return ResponseEntity.ok(forecastingService.getAllForecasts());
    }
    // get /api/forecasts/warehouse/{id} //ADMIN
    @GetMapping("/warehouse/{id}")
    public ResponseEntity<List<Forecast>> getForecastsByWarehouse(@PathVariable String id) {
        return ResponseEntity.ok(forecastingService.getForecastsByWarehouse(id));
    }
    //get  /api/forecasts/my-warehouse   //MANAGER
    @GetMapping("/my-warehouse")
    public ResponseEntity<List<Forecast>> getMyWarehouseForecasts() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(forecastingService.getMyWarehouseForecasts(user.getId()));
    }
}
