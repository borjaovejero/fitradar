package com.fitradar.backend.training.dto;

import com.fitradar.backend.training.model.TrainingSession;
import java.time.LocalDate;

public class TrainingSessionResponse {

    private Long id;
    private String username;
    private LocalDate sessionDate;
    private String title;
    private TrainingSession.TrainingType trainingType;
    private Integer durationMinutes;
    private Integer rpe;
    private TrainingSession.IntensityLevel intensityLevel;
    private Double distanceKm;
    private Integer averageHeartRate;
    private Integer maxHeartRate;
    private Integer calories;
    private String notes;
    private Integer sessionLoad;
    private Double acuteLoad7d;
    private Double chronicLoad28d;
    private Double acwr;
    private Integer daysSinceLastRest;

    public TrainingSessionResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public LocalDate getSessionDate() { return sessionDate; }
    public void setSessionDate(LocalDate sessionDate) { this.sessionDate = sessionDate; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public TrainingSession.TrainingType getTrainingType() { return trainingType; }
    public void setTrainingType(TrainingSession.TrainingType trainingType) { this.trainingType = trainingType; }

    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }

    public Integer getRpe() { return rpe; }
    public void setRpe(Integer rpe) { this.rpe = rpe; }

    public TrainingSession.IntensityLevel getIntensityLevel() { return intensityLevel; }
    public void setIntensityLevel(TrainingSession.IntensityLevel intensityLevel) { this.intensityLevel = intensityLevel; }

    public Double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(Double distanceKm) { this.distanceKm = distanceKm; }

    public Integer getAverageHeartRate() { return averageHeartRate; }
    public void setAverageHeartRate(Integer averageHeartRate) { this.averageHeartRate = averageHeartRate; }

    public Integer getMaxHeartRate() { return maxHeartRate; }
    public void setMaxHeartRate(Integer maxHeartRate) { this.maxHeartRate = maxHeartRate; }

    public Integer getCalories() { return calories; }
    public void setCalories(Integer calories) { this.calories = calories; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Integer getSessionLoad() { return sessionLoad; }
    public void setSessionLoad(Integer sessionLoad) { this.sessionLoad = sessionLoad; }

    public Double getAcuteLoad7d() { return acuteLoad7d; }
    public void setAcuteLoad7d(Double acuteLoad7d) { this.acuteLoad7d = acuteLoad7d; }

    public Double getChronicLoad28d() { return chronicLoad28d; }
    public void setChronicLoad28d(Double chronicLoad28d) { this.chronicLoad28d = chronicLoad28d; }

    public Double getAcwr() { return acwr; }
    public void setAcwr(Double acwr) { this.acwr = acwr; }

    public Integer getDaysSinceLastRest() { return daysSinceLastRest; }
    public void setDaysSinceLastRest(Integer daysSinceLastRest) { this.daysSinceLastRest = daysSinceLastRest; }
}