package com.eddress.Currency.IServices;

import com.eddress.Currency.dto.ConversionRequestDTO;
import com.eddress.Currency.entities.ExchangeRate;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface IExchangeRateService {


    void updateExchangeRates(String usd) throws IOException, InterruptedException;
    List<ExchangeRate> getAllExchangeRates();
    Optional<ExchangeRate> getExchangeRateById(Long id);
    void deleteExchangeRateById(Long id);
    ExchangeRate createCurrency(String targetCurrency, BigDecimal rate) throws Exception;

    BigDecimal convertCurrency(ConversionRequestDTO requestDTO);
}
