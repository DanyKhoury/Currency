package com.eddress.Currency.dto;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import java.math.BigDecimal;

public class CurrencyCreationRequest {

    @NotNull(message = "Target currency must not be null.")
    @Size(max = 3, message = "Target currency must be no more than 3 characters.")
    private String targetCurrency;
    @NotNull(message = "Rate must not be null.")
    @DecimalMin(value = "0.0", inclusive = false, message = "Rate must be greater than 0.")
    private BigDecimal rate;


    // Getters and Setters
    public String getTargetCurrency() {
        return targetCurrency;
    }

    public void setTargetCurrency(String targetCurrency) {
        this.targetCurrency = targetCurrency;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }
}
