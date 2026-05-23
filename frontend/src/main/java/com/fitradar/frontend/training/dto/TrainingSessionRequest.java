package com.fitradar.frontend.training.dto;

import java.time.LocalDate;

public class TrainingSessionRequest {

    // username e intensityLevel eliminados — el backend los gestiona automáticamente
    private LocalDate sessionDate;
    private String title;
    private String trainingType;
    private Integer durationMinutes;
    private Integer rpe;              // 1-10 — el backend deriva intensityLevel del RPE
    private Double distanceKm;
    private Integer averageHeartRate;
    private Integer maxHeartRate;
    private Integer calories;
    private String notes;

    public TrainingSessionRequest() {}

    public LocalDate getSessionDate() { return sessionDate; }
    public void setSessionDate(LocalDate sessionDate) { this.sessionDate = sessionDate; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getTrainingType() { return trainingType; }
    public void setTrainingType(String trainingType) { this.trainingType = trainingType; }

    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }

    public Integer getRpe() { return rpe; }
    public void setRpe(Integer rpe) { this.rpe = rpe; }

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
}