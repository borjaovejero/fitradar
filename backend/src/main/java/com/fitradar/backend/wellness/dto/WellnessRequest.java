package com.fitradar.backend.wellness.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public class WellnessRequest {

    private LocalDate recordDate;

    @Min(value = 0) @Max(value = 24)
    private Double sleepHours;

    @Min(value = 1) @Max(value = 5)
    private Integer sleepQuality;

    @Min(value = 1) @Max(value = 5)
    private Integer fatigue;

    @Min(value = 1) @Max(value = 5)
    private Integer stress;

    @Min(value = 1) @Max(value = 5)
    private Integer muscleSoreness;

    @Min(value = 1) @Max(value = 5)
    private Integer generalFeeling;

    @Min(value = 1) @Max(value = 5)
    private Integer recoveryFeeling;

    // Opcionales — solo si el usuario tiene wearable
    @Min(value = 0) @Max(value = 300)
    private Double hrvRmssd;

    @Min(value = 30) @Max(value = 220)
    private Integer restingHr;

    @Size(max = 500)
    private String notes;

    public WellnessRequest() {}

    public Double getSleepHours() { return sleepHours; }
    public void setSleepHours(Double sleepHours) { this.sleepHours = sleepHours; }

    public Integer getSleepQuality() { return sleepQuality; }
    public void setSleepQuality(Integer sleepQuality) { this.sleepQuality = sleepQuality; }

    public Integer getFatigue() { return fatigue; }
    public void setFatigue(Integer fatigue) { this.fatigue = fatigue; }

    public Integer getStress() { return stress; }
    public void setStress(Integer stress) { this.stress = stress; }

    public Integer getMuscleSoreness() { return muscleSoreness; }
    public void setMuscleSoreness(Integer muscleSoreness) { this.muscleSoreness = muscleSoreness; }

    public Integer getGeneralFeeling() { return generalFeeling; }
    public void setGeneralFeeling(Integer generalFeeling) { this.generalFeeling = generalFeeling; }

    public Integer getRecoveryFeeling() { return recoveryFeeling; }
    public void setRecoveryFeeling(Integer recoveryFeeling) { this.recoveryFeeling = recoveryFeeling; }

    public Double getHrvRmssd() { return hrvRmssd; }
    public void setHrvRmssd(Double hrvRmssd) { this.hrvRmssd = hrvRmssd; }

    public Integer getRestingHr() { return restingHr; }
    public void setRestingHr(Integer restingHr) { this.restingHr = restingHr; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}