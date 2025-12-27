package com.STFAS.controller;

import com.STFAS.entity.Forecast;
import com.STFAS.service.ForecastService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/forecast")
@RequiredArgsConstructor
public class ForecastController {

    private final ForecastService forecastService;

    @PostMapping("/{stockId}")
    public ResponseEntity<Forecast> generateForecast(@PathVariable String stockId) {
        return ResponseEntity.ok(forecastService.generateForecast(stockId));
    }
}
