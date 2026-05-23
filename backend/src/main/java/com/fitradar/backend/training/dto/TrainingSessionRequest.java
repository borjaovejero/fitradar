package com.fitradar.backend.training.dto;

import com.fitradar.backend.training.model.TrainingSession;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

public class TrainingSessionRequest {

    // username e intensityLevel eliminados
    // username viene de Authentication, intensityLevel se deriva del RPE en el service

    @NotNull(message = "La fecha de sesión es obligatoria")
    private LocalDate sessionDate;

    @NotBlank(message = "El título es obligatorio")
    @Size(max = 100)
    private String title;

    @NotNull(message = "El tipo de entrenamiento es obligatorio")
    private TrainingSession.TrainingType trainingType;

    @NotNull(message = "La duración es obligatoria")
    @Min(value = 1, message = "La duración debe ser mayor que 0")
    private Integer durationMinutes;

    @Min(value = 1) @Max(value = 10)
    private Integer rpe;

    @Min(value = 0)
    private Double distanceKm;

    @Min(value = 0) @Max(value = 250)
    private Integer averageHeartRate;

    @Min(value = 0) @Max(value = 250)
    private Integer maxHeartRate;

    @Min(value = 0)
    private Integer calories;

    @Size(max = 500)
    private String notes;

    public TrainingSessionRequest() {}

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