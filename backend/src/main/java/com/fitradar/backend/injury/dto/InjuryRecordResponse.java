package com.fitradar.backend.injury.dto;

import com.fitradar.backend.injury.model.InjuryRecord;
import java.time.LocalDate;

public class InjuryRecordResponse {

    private Long id;
    private String username;
    private InjuryRecord.InjuryType injuryType;
    private InjuryRecord.BodyZone bodyZone;
    private InjuryRecord.InjurySeverity injurySeverity;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer daysOff;
    private boolean recurrence;
    private boolean fullyRecovered;
    private boolean reportedToModel;
    private String description;

    public InjuryRecordResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

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

    public Integer getDaysOff() { return daysOff; }
    public void setDaysOff(Integer daysOff) { this.daysOff = daysOff; }

    public boolean isRecurrence() { return recurrence; }
    public void setRecurrence(boolean recurrence) { this.recurrence = recurrence; }

    public boolean isFullyRecovered() { return fullyRecovered; }
    public void setFullyRecovered(boolean fullyRecovered) { this.fullyRecovered = fullyRecovered; }

    public boolean isReportedToModel() { return reportedToModel; }
    public void setReportedToModel(boolean reportedToModel) { this.reportedToModel = reportedToModel; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}