package com.fitradar.frontend.injury.dto;

import java.time.LocalDate;

public class InjuryRecordRequest {

    // username eliminado — viene de Authentication en el backend
    private String injuryType;
    private String bodyZone;
    private String injurySeverity;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean recurrence;
    private boolean fullyRecovered;
    private String description;

    public InjuryRecordRequest() {}

    public String getInjuryType() { return injuryType; }
    public void setInjuryType(String injuryType) { this.injuryType = injuryType; }

    public String getBodyZone() { return bodyZone; }
    public void setBodyZone(String bodyZone) { this.bodyZone = bodyZone; }

    public String getInjurySeverity() { return injurySeverity; }
    public void setInjurySeverity(String injurySeverity) { this.injurySeverity = injurySeverity; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public boolean isRecurrence() { return recurrence; }
    public void setRecurrence(boolean recurrence) { this.recurrence = recurrence; }

    public boolean isFullyRecovered() { return fullyRecovered; }
    public void setFullyRecovered(boolean fullyRecovered) { this.fullyRecovered = fullyRecovered; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}