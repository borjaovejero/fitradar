package com.fitradar.backend.training.model;

import com.fitradar.backend.user.model.User;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "training_sessions")
public class TrainingSession {

    public enum TrainingType {
        RUNNING, GYM, FOOTBALL, CYCLING,
        SWIMMING, TENNIS, PADEL, BASKETBALL, OTHER
    }

    public enum IntensityLevel { LOW, MEDIUM, HIGH }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "username", nullable = false)
    private User user;

    @Column(name = "session_date", nullable = false)
    private LocalDate sessionDate;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "training_type", nullable = false, length = 30)
    private TrainingType trainingType;

    @Column(name = "duration_min", nullable = false)
    private Integer durationMinutes;

    @Column(name = "rpe")
    private Integer rpe;

    @Enumerated(EnumType.STRING)
    @Column(name = "intensity_level", length = 20)
    private IntensityLevel intensityLevel;

    @Column(name = "distance_km")
    private Double distanceKm;

    @Column(name = "average_heart_rate")
    private Integer averageHeartRate;

    @Column(name = "max_heart_rate")
    private Integer maxHeartRate;

    @Column(name = "calories")
    private Integer calories;

    @Column(name = "session_load")
    private Integer sessionLoad = 0;

    @Column(name = "acute_load_7d")
    private Double acuteLoad7d = 0.0;

    @Column(name = "chronic_load_28d")
    private Double chronicLoad28d = 0.0;

    @Column(name = "acwr")
    private Double acwr = 0.0;

    @Column(name = "days_since_last_rest", columnDefinition = "INT DEFAULT 0")
    private Integer daysSinceLastRest;

    @Column(name = "notes", length = 500)
    private String notes;

    public TrainingSession() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public LocalDate getSessionDate() { return sessionDate; }
    public void setSessionDate(LocalDate sessionDate) { this.sessionDate = sessionDate; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public TrainingType getTrainingType() { return trainingType; }
    public void setTrainingType(TrainingType trainingType) { this.trainingType = trainingType; }

    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }

    public Integer getRpe() { return rpe; }
    public void setRpe(Integer rpe) { this.rpe = rpe; }

    public IntensityLevel getIntensityLevel() { return intensityLevel; }
    public void setIntensityLevel(IntensityLevel intensityLevel) { this.intensityLevel = intensityLevel; }

    public Double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(Double distanceKm) { this.distanceKm = distanceKm; }

    public Integer getAverageHeartRate() { return averageHeartRate; }
    public void setAverageHeartRate(Integer averageHeartRate) { this.averageHeartRate = averageHeartRate; }

    public Integer getMaxHeartRate() { return maxHeartRate; }
    public void setMaxHeartRate(Integer maxHeartRate) { this.maxHeartRate = maxHeartRate; }

    public Integer getCalories() { return calories; }
    public void setCalories(Integer calories) { this.calories = calories; }

    public Integer getSessionLoad() { return sessionLoad; }
    public void setSessionLoad(Integer sessionLoad) { this.sessionLoad = sessionLoad; }

    public Double getAcuteLoad7d() { return acuteLoad7d; }
    public void setAcuteLoad7d(Double acuteLoad7d) { this.acuteLoad7d = acuteLoad7d; }

    public Double getChronicLoad28d() { return chronicLoad28d; }
    public void setChronicLoad28d(Double chronicLoad28d) { this.chronicLoad28d = chronicLoad28d; }

    public Double getAcwr() { return acwr; }
    public void setAcwr(Double acwr) { this.acwr = acwr; }

    public Integer getDaysSinceLastRest() { return daysSinceLastRest; }
    public void setDaysSinceLastRest(Integer daysSinceLastRest) { this.daysSinceLastRest = daysSinceLastRest; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}