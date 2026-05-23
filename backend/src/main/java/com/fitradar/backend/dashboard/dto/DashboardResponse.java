package com.fitradar.backend.dashboard.dto;

import com.fitradar.backend.risk.dto.RiskPredictionResponse;
import com.fitradar.backend.training.dto.TrainingSessionResponse;
import com.fitradar.backend.wellness.dto.WellnessResponse;

public class DashboardResponse {

    private String username;
    private String fullName;

    // Últimos registros de cada entidad
    private WellnessResponse latestWellness;
    private TrainingSessionResponse latestTrainingSession;
    private RiskPredictionResponse latestRiskPrediction;

    // Estadísticas de la semana actual
    private Integer weeklyTrainingSessions;
    private Integer weeklyTrainingMinutes;
    private Integer weeklyCalories;

    // ACWR de la última sesión — clave para el dashboard de riesgo
    private Double latestAcwr;

    // Progreso de personalización del modelo ML (0 a 15)
    private Integer samplesCollected;

    // Lesiones
    private Integer activeInjuries;
    private Integer recoveredInjuries;

    public DashboardResponse() {}

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public WellnessResponse getLatestWellness() { return latestWellness; }
    public void setLatestWellness(WellnessResponse latestWellness) { this.latestWellness = latestWellness; }

    public TrainingSessionResponse getLatestTrainingSession() { return latestTrainingSession; }
    public void setLatestTrainingSession(TrainingSessionResponse latestTrainingSession) { this.latestTrainingSession = latestTrainingSession; }

    public RiskPredictionResponse getLatestRiskPrediction() { return latestRiskPrediction; }
    public void setLatestRiskPrediction(RiskPredictionResponse latestRiskPrediction) { this.latestRiskPrediction = latestRiskPrediction; }

    public Integer getWeeklyTrainingSessions() { return weeklyTrainingSessions; }
    public void setWeeklyTrainingSessions(Integer weeklyTrainingSessions) { this.weeklyTrainingSessions = weeklyTrainingSessions; }

    public Integer getWeeklyTrainingMinutes() { return weeklyTrainingMinutes; }
    public void setWeeklyTrainingMinutes(Integer weeklyTrainingMinutes) { this.weeklyTrainingMinutes = weeklyTrainingMinutes; }

    public Integer getWeeklyCalories() { return weeklyCalories; }
    public void setWeeklyCalories(Integer weeklyCalories) { this.weeklyCalories = weeklyCalories; }

    public Double getLatestAcwr() { return latestAcwr; }
    public void setLatestAcwr(Double latestAcwr) { this.latestAcwr = latestAcwr; }

    public Integer getSamplesCollected() { return samplesCollected; }
    public void setSamplesCollected(Integer samplesCollected) { this.samplesCollected = samplesCollected; }

    public Integer getActiveInjuries() { return activeInjuries; }
    public void setActiveInjuries(Integer activeInjuries) { this.activeInjuries = activeInjuries; }

    public Integer getRecoveredInjuries() { return recoveredInjuries; }
    public void setRecoveredInjuries(Integer recoveredInjuries) { this.recoveredInjuries = recoveredInjuries; }
}