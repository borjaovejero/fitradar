package com.fitradar.frontend.injury.dto;

import java.time.LocalDate;

public class InjuryRecordResponse {

    private Long id;
    private String username;
    private String injuryType;
    private String bodyZone;
    private String injurySeverity;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer daysOff;           // calculado automáticamente por el backend
    private boolean recurrence;
    private boolean fullyRecovered;
    private boolean reportedToModel;   // si esta lesión ya se envió al modelo ML
    private String description;

    public InjuryRecordResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

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