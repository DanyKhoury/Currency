package com.eddress.Currency.dto;

import java.math.BigDecimal;

public class ConversionRequestDTO {
    private String baseCurrencyCode;
    private String targetCurrencyCode;
    private BigDecimal amount;

    // Default constructor
    public ConversionRequestDTO() {
    }


    public ConversionRequestDTO(String baseCurrencyCode, String targetCurrencyCode, BigDecimal amount) {
        this.baseCurrencyCode = baseCurrencyCode;
        this.targetCurrencyCode = targetCurrencyCode;
        this.amount = amount;
    }

    // Getters
    public String getBaseCurrencyCode() {
        return baseCurrencyCode;
    }

    public String getTargetCurrencyCode() {
        return targetCurrencyCode;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    // Setters
    public void setBaseCurrencyCode(String baseCurrencyCode) {
        this.baseCurrencyCode = baseCurrencyCode;
    }

    public void setTargetCurrencyCode(String targetCurrencyCode) {
        this.targetCurrencyCode = targetCurrencyCode;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
