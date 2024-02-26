package com.eddress.Currency.services;

import com.eddress.Currency.IServices.IExchangeRateService;
import com.eddress.Currency.RequestDto.ConversionRequestDTO;
import com.eddress.Currency.entities.ExchangeRate;
import com.eddress.Currency.exception.CurrencyNotFoundException;
import com.eddress.Currency.repositories.IExchangeRateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.RoundingMode;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ExchangeRateService implements IExchangeRateService {

    @Autowired
    private IExchangeRateRepository exchangeRateRepository;
    @Transactional
    public void updateExchangeRates(String baseCurrency) throws IOException, InterruptedException {

        String requestUri = "https://exchange-rate-api1.p.rapidapi.com/latest?base=" + baseCurrency;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(requestUri))
                .header("X-RapidAPI-Key", "a9247fa569msh46ec62c4a63130ap1d4a09jsnf0b15c2b20d4")
                .header("X-RapidAPI-Host", "exchange-rate-api1.p.rapidapi.com")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());



        ObjectMapper mapper = new ObjectMapper();
        var root = mapper.readTree(response.body());
        var ratesNode = root.path("rates");

        ratesNode.fields().forEachRemaining(entry -> {
            String targetCurrency = entry.getKey();
            BigDecimal rate = entry.getValue().decimalValue();

            // Check if a record exists, update it, else create a new one
            ExchangeRate exchangeRate = exchangeRateRepository.findByBaseCurrencyAndTargetCurrency(baseCurrency, targetCurrency)
                    .orElse(new ExchangeRate(baseCurrency, targetCurrency, rate, LocalDateTime.now()));

            exchangeRate.setRate(rate);
            exchangeRate.setUpdatedAt(LocalDateTime.now());
            exchangeRateRepository.save(exchangeRate);
        });
    }

    public List<ExchangeRate> getAllExchangeRates() {
        return exchangeRateRepository.findAll();
    }

    public Optional<ExchangeRate> getExchangeRateById(Long id) {
        return exchangeRateRepository.findById(id);
    }

    public void deleteExchangeRateById(Long id) {
        exchangeRateRepository.deleteById(id);
    }

    public ExchangeRate createCurrency(String targetCurrency, BigDecimal rate) throws Exception {
        String baseCurrency = "USD";

        Optional<ExchangeRate> existingRate = exchangeRateRepository.findByBaseCurrencyAndTargetCurrency(baseCurrency, targetCurrency);
        if (existingRate.isPresent()) {
            throw new Exception("Currency cannot be created as it already exists.");
        }

        ExchangeRate newRate = new ExchangeRate();
        newRate.setBaseCurrency(baseCurrency);
        newRate.setTargetCurrency(targetCurrency);
        newRate.setRate(rate);
        newRate.setUpdatedAt(LocalDateTime.now()); // Setting the creation time
        return exchangeRateRepository.save(newRate);
    }

    public BigDecimal convertCurrency(ConversionRequestDTO requestDTO) {
        if (requestDTO.getBaseCurrencyCode().equalsIgnoreCase(requestDTO.getTargetCurrencyCode())) {
            return requestDTO.getAmount();
        }

        // Convert from base currency to USD
        BigDecimal amountInUSD = convertToUSD(requestDTO.getBaseCurrencyCode(), requestDTO.getAmount());

        // Convert from USD to target currency
        BigDecimal finalAmount = convertFromUSD(requestDTO.getTargetCurrencyCode(), amountInUSD);

        return finalAmount.setScale(10, RoundingMode.HALF_UP);
    }

    private BigDecimal convertToUSD(String baseCurrencyCode, BigDecimal amount) {
        if ("USD".equalsIgnoreCase(baseCurrencyCode)) {
            return amount; // No conversion needed if base is USD
        }
        Optional<ExchangeRate> rateOptional = exchangeRateRepository.findByBaseCurrencyAndTargetCurrency("USD", baseCurrencyCode);
        BigDecimal rate = rateOptional.map(ExchangeRate::getRate)
                .orElseThrow(() -> new CurrencyNotFoundException("Exchange rate from " + baseCurrencyCode + " to USD not found."));
        return amount.divide(rate, 10, RoundingMode.HALF_UP); // Convert base currency to USD
    }

    private BigDecimal convertFromUSD(String targetCurrencyCode, BigDecimal amountInUSD) {
        if ("USD".equalsIgnoreCase(targetCurrencyCode)) {
            return amountInUSD; // No conversion needed if target is USD
        }
        Optional<ExchangeRate> rateOptional = exchangeRateRepository.findByBaseCurrencyAndTargetCurrency("USD", targetCurrencyCode);
        BigDecimal rate = rateOptional.map(ExchangeRate::getRate)
                .orElseThrow(() -> new CurrencyNotFoundException("USD to " + targetCurrencyCode + " exchange rate not found."));
        return amountInUSD.multiply(rate); // Convert USD to target currency
    }



}


