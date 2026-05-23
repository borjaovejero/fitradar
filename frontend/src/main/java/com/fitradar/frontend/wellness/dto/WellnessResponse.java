package com.fitradar.frontend.wellness.dto;

import java.time.LocalDate;

public class WellnessResponse {

    private Long id;
    private String username;
    private LocalDate recordDate;
    private Double sleepHours;
    private Integer sleepQuality;
    private Integer fatigue;
    private Integer stress;
    private Integer muscleSoreness;
    private Integer generalFeeling;
    private Integer recoveryFeeling;
    private Double hrvRmssd;
    private Integer restingHr;
    private String notes;

    public WellnessResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public LocalDate getRecordDate() { return recordDate; }
    public void setRecordDate(LocalDate recordDate) { this.recordDate = recordDate; }

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