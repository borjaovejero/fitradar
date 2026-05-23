package com.fitradar.backend.injury.model;

import com.fitradar.backend.user.model.User;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "injury_records")
public class InjuryRecord {

    public enum InjuryType {
        MUSCULAR, TENDON, LIGAMENT, JOINT, BONE, OTHER
    }

    public enum BodyZone {
        NECK, SHOULDER, ARM, ELBOW, WRIST, BACK, LUMBAR,
        HIP, GLUTE, QUADRICEPS, HAMSTRING, KNEE, CALF, ANKLE, FOOT, OTHER
    }

    public enum InjurySeverity { LOW, MEDIUM, HIGH }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "username", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "injury_type", nullable = false, length = 30)
    private InjuryType injuryType;

    @Enumerated(EnumType.STRING)
    @Column(name = "body_zone", nullable = false, length = 30)
    private BodyZone bodyZone;

    @Enumerated(EnumType.STRING)
    @Column(name = "injury_severity", nullable = false, length = 20)
    private InjurySeverity injurySeverity;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    // Calculado automáticamente cuando se establece endDate
    @Column(name = "days_off")
    private Integer daysOff;

    @Column(name = "recurrence", nullable = false)
    private boolean recurrence;

    @Column(name = "fully_recovered", nullable = false)
    private boolean fullyRecovered;

    // true cuando esta lesión ya se envió al modelo ML para actualizar
    // la capa de calibración personal del usuario
    @Column(name = "reported_to_model", nullable = false)
    private boolean reportedToModel = false;

    @Column(name = "description", length = 500)
    private String description;

    public InjuryRecord() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public InjuryType getInjuryType() { return injuryType; }
    public void setInjuryType(InjuryType injuryType) { this.injuryType = injuryType; }

    public BodyZone getBodyZone() { return bodyZone; }
    public void setBodyZone(BodyZone bodyZone) { this.bodyZone = bodyZone; }

    public InjurySeverity getInjurySeverity() { return injurySeverity; }
    public void setInjurySeverity(InjurySeverity injurySeverity) { this.injurySeverity = injurySeverity; }

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