package com.fitradar.backend.risk.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class RiskPredictionRequest {

    // 0 = no hubo lesión, 1 = sí hubo lesión/molestia
    @NotNull(message = "El resultado es obligatorio")
    @Min(value = 0) @Max(value = 1)
    private Integer outcome;

    public RiskPredictionRequest() {}

    public Integer getOutcome() { return outcome; }
    public void setOutcome(Integer outcome) { this.outcome = outcome; }
}