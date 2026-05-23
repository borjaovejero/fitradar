package com.fitradar.backend.wellness.model;

import com.fitradar.backend.user.model.User;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "wellness")
public class Wellness {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "username", nullable = false)
    private User user;

    @Column(name = "record_date", nullable = false)
    private LocalDate recordDate;

    // ── Sueño ─────────────────────────────────────────────────
    @Column(name = "sleep_hours")
    private Double sleepHours;

    @Column(name = "sleep_quality")
    private Integer sleepQuality;       // 1-5

    // ── Estado subjetivo ──────────────────────────────────────
    @Column(name = "fatigue")
    private Integer fatigue;            // 1-5

    @Column(name = "stress")
    private Integer stress;             // 1-5

    @Column(name = "muscle_soreness")
    private Integer muscleSoreness;     // 1-5

    @Column(name = "general_feeling")
    private Integer generalFeeling;     // 1-5

    @Column(name = "recovery_feeling")
    private Integer recoveryFeeling;    // 1-5

    // ── Datos fisiológicos (opcionales, wearable) ─────────────
    // hrv_rmssd → se usa como 'recovery' en el modelo ML
    @Column(name = "hrv_rmssd")
    private Double hrvRmssd;

    // resting_hr → se usa como 'heart_rate' en el modelo ML
    @Column(name = "resting_hr")
    private Integer restingHr;

    @Column(name = "notes", length = 500)
    private String notes;

    public Wellness() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

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
}