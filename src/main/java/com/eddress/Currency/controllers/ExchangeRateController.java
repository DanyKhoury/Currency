package com.eddress.Currency.controllers;

import com.eddress.Currency.IServices.IExchangeRateService;
import com.eddress.Currency.RequestDto.ConversionRequestDTO;
import com.eddress.Currency.RequestDto.CurrencyCreationRequest;
import com.eddress.Currency.entities.ExchangeRate;
import com.eddress.Currency.exception.CurrencyNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
public class ExchangeRateController {

    @Autowired
    private IExchangeRateService exchangeRateService;

    @PutMapping("/update-rates")
    public ResponseEntity<?> updateExchangeRates() {
        try {
            exchangeRateService.updateExchangeRates("USD");
            return ResponseEntity.ok("Exchange rates updated successfully ");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to update exchange rates: " + e.getMessage());
        }
    }

    @GetMapping("/exchange-rates")
    public ResponseEntity<List<ExchangeRate>> getAllExchangeRates() {
        return ResponseEntity.ok(exchangeRateService.getAllExchangeRates());
    }
    @GetMapping("/exchange-rates/{id}")
    public ResponseEntity<ExchangeRate> getExchangeRateById(@PathVariable Long id) {
        return exchangeRateService.getExchangeRateById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/exchange-rates/{id}")
    public ResponseEntity<?> deleteExchangeRateById(@PathVariable Long id) {
        exchangeRateService.deleteExchangeRateById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/create-currency")
    public ResponseEntity<?> createCurrency(@Valid @RequestBody CurrencyCreationRequest request) {
        try {
            ExchangeRate newRate = exchangeRateService.createCurrency(request.getTargetCurrency(), request.getRate());
            return ResponseEntity.ok(newRate);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/convert")
    public ResponseEntity<?> convertCurrency(@Valid @RequestBody ConversionRequestDTO requestDTO) {
        try {
            BigDecimal result = exchangeRateService.convertCurrency(requestDTO);
            return ResponseEntity.ok(result);
        } catch (CurrencyNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
