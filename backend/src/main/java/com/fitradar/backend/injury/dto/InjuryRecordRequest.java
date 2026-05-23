package com.fitradar.backend.injury.dto;

import com.fitradar.backend.injury.model.InjuryRecord;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public class InjuryRecordRequest {

    // username eliminado — viene de Authentication

    @NotNull(message = "El tipo de lesión es obligatorio")
    private InjuryRecord.InjuryType injuryType;

    @NotNull(message = "La zona corporal es obligatoria")
    private InjuryRecord.BodyZone bodyZone;

    @NotNull(message = "La gravedad es obligatoria")
    private InjuryRecord.InjurySeverity injurySeverity;

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDate startDate;

    // null = lesión activa todavía, se puede actualizar después
    private LocalDate endDate;

    private boolean recurrence;

    private boolean fullyRecovered;

    @Size(max = 500)
    private String description;

    public InjuryRecordRequest() {}

    public InjuryRecord.InjuryType getInjuryType() { return injuryType; }
    public void setInjuryType(InjuryRecord.InjuryType injuryType) { this.injuryType = injuryType; }

    public InjuryRecord.BodyZone getBodyZone() { return bodyZone; }
    public void setBodyZone(InjuryRecord.BodyZone bodyZone) { this.bodyZone = bodyZone; }

    public InjuryRecord.InjurySeverity getInjurySeverity() { return injurySeverity; }
    public void setInjurySeverity(InjuryRecord.InjurySeverity injurySeverity) { this.injurySeverity = injurySeverity; }

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