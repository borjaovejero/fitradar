package com.fitradar.frontend.dashboard.dto;

import com.fitradar.frontend.risk.dto.RiskPredictionResponse;
import com.fitradar.frontend.training.dto.TrainingSessionResponse;
import com.fitradar.frontend.wellness.dto.WellnessResponse;

public class DashboardResponse {

    private String username;
    private String fullName;

    private WellnessResponse latestWellness;
    private TrainingSessionResponse latestTrainingSession;
    private RiskPredictionResponse latestRiskPrediction;

    // Estadísticas de la semana actual
    private Integer weeklyTrainingSessions;
    private Integer weeklyTrainingMinutes;
    private Integer weeklyCalories;

    // ACWR de la última sesión
    private Double latestAcwr;

    // Progreso personalización modelo ML
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