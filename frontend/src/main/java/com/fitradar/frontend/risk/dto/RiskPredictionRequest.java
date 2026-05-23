package com.fitradar.frontend.risk.dto;

public class RiskPredictionRequest {

    // El único campo es outcome: 0 = sin lesión, 1 = lesión/molestia
    // Se usa para el endpoint POST /api/risk-predictions/{id}/feedback
    private Integer outcome;

    public RiskPredictionRequest() {}

    public Integer getOutcome() { return outcome; }
    public void setOutcome(Integer outcome) { this.outcome = outcome; }
}