package com.fitradar.backend.risk.model;

import com.fitradar.backend.user.model.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "risk_predictions")
public class RiskPrediction {

    public enum RiskLevel { LOW, MEDIUM, HIGH }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "username", nullable = false)
    private User user;

    // Sesión de entrenamiento que disparó esta predicción
    @Column(name = "training_session_id")
    private Long trainingSessionId;

    @Column(name = "prediction_date", nullable = false)
    private LocalDateTime predictionDate;

    // Probabilidad final (puede ser personalizada si hay historial)
    @Column(name = "risk_score", nullable = false)
    private Double riskScore;

    // Probabilidad del modelo global sin personalizar
    @Column(name = "global_probability")
    private Double globalProbability;

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_level", nullable = false, length = 20)
    private RiskLevel riskLevel;

    // "global" o "personalized"
    @Column(name = "model_used", length = 20)
    private String modelUsed;

    // Sesiones acumuladas del usuario para personalización
    @Column(name = "samples_collected")
    private Integer samplesCollected;

    @Column(name = "recommendation", length = 500)
    private String recommendation;

    @Column(name = "model_version", length = 50)
    private String modelVersion;

    public RiskPrediction() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Long getTrainingSessionId() { return trainingSessionId; }
    public void setTrainingSessionId(Long trainingSessionId) { this.trainingSessionId = trainingSessionId; }

    public LocalDateTime getPredictionDate() { return predictionDate; }
    public void setPredictionDate(LocalDateTime predictionDate) { this.predictionDate = predictionDate; }

    public Double getRiskScore() { return riskScore; }
    public void setRiskScore(Double riskScore) { this.riskScore = riskScore; }

    public Double getGlobalProbability() { return globalProbability; }
    public void setGlobalProbability(Double globalProbability) { this.globalProbability = globalProbability; }

    public RiskLevel getRiskLevel() { return riskLevel; }
    public void setRiskLevel(RiskLevel riskLevel) { this.riskLevel = riskLevel; }

    public String getModelUsed() { return modelUsed; }
    public void setModelUsed(String modelUsed) { this.modelUsed = modelUsed; }

    public Integer getSamplesCollected() { return samplesCollected; }
    public void setSamplesCollected(Integer samplesCollected) { this.samplesCollected = samplesCollected; }

    public String getRecommendation() { return recommendation; }
    public void setRecommendation(String recommendation) { this.recommendation = recommendation; }

    public String getModelVersion() { return modelVersion; }
    public void setModelVersion(String modelVersion) { this.modelVersion = modelVersion; }
}