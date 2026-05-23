package com.fitradar.frontend.risk.dto;

import java.time.LocalDateTime;

public class RiskPredictionResponse {

    private Long id;
    private String username;
    private Long trainingSessionId;
    private LocalDateTime predictionDate;
    private Double riskScore;
    private Double globalProbability;
    private String riskLevel;
    private String modelUsed;
    private Integer samplesCollected;
    private String recommendation;
    private String modelVersion;

    public RiskPredictionResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public Long getTrainingSessionId() { return trainingSessionId; }
    public void setTrainingSessionId(Long trainingSessionId) { this.trainingSessionId = trainingSessionId; }

    public LocalDateTime getPredictionDate() { return predictionDate; }
    public void setPredictionDate(LocalDateTime predictionDate) { this.predictionDate = predictionDate; }

    public Double getRiskScore() { return riskScore; }
    public void setRiskScore(Double riskScore) { this.riskScore = riskScore; }

    public Double getGlobalProbability() { return globalProbability; }
    public void setGlobalProbability(Double globalProbability) { this.globalProbability = globalProbability; }

    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }

    public String getModelUsed() { return modelUsed; }
    public void setModelUsed(String modelUsed) { this.modelUsed = modelUsed; }

    public Integer getSamplesCollected() { return samplesCollected; }
    public void setSamplesCollected(Integer samplesCollected) { this.samplesCollected = samplesCollected; }

    public String getRecommendation() { return recommendation; }
    public void setRecommendation(String recommendation) { this.recommendation = recommendation; }

    public String getModelVersion() { return modelVersion; }
    public void setModelVersion(String modelVersion) { this.modelVersion = modelVersion; }
}