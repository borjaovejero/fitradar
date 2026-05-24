package com.fitradar.backend.user.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "users")
public class User {

    public enum Sex { MALE, FEMALE, OTHER }

    public enum SportType {
        RUNNING, FOOTBALL, GYM, CYCLING,
        BASKETBALL, TENNIS, PADEL, SWIMMING, OTHER
    }

    public enum AthleteLevel { BEGINNER, INTERMEDIATE, ADVANCED }

    // ── Credenciales ──────────────────────────────────────────
    @Id
    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "enabled", nullable = false)
    private boolean enabled = true;

    // ── Datos personales (obligatorios en registro) ───────────
    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "sex", nullable = true, length = 20)
    private Sex sex;

    // ── Perfil deportivo (opcionales, editables desde perfil) ─
    @Column(name = "height_cm")
    private Double heightCm;

    @Column(name = "weight_kg")
    private Double weightKg;

    @Enumerated(EnumType.STRING)
    @Column(name = "sport_type", length = 30)
    private SportType sportType;

    @Enumerated(EnumType.STRING)
    @Column(name = "athlete_level", length = 30)
    private AthleteLevel athleteLevel;

    @Column(name = "weekly_training_days")
    private Integer weeklyTrainingDays;

    @Column(name = "previous_injury", nullable = false)
    private boolean previousInjury = false;

    @Column(name = "observations", length = 500)
    private String observations;

    public User() {}

    // Getters y setters de todos los campos:

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }

    public Sex getSex() { return sex; }
    public void setSex(Sex sex) { this.sex = sex; }

    public Double getHeightCm() { return heightCm; }
    public void setHeightCm(Double heightCm) { this.heightCm = heightCm; }

    public Double getWeightKg() { return weightKg; }
    public void setWeightKg(Double weightKg) { this.weightKg = weightKg; }

    public SportType getSportType() { return sportType; }
    public void setSportType(SportType sportType) { this.sportType = sportType; }

    public AthleteLevel getAthleteLevel() { return athleteLevel; }
    public void setAthleteLevel(AthleteLevel athleteLevel) { this.athleteLevel = athleteLevel; }

    public Integer getWeeklyTrainingDays() { return weeklyTrainingDays; }
    public void setWeeklyTrainingDays(Integer weeklyTrainingDays) { this.weeklyTrainingDays = weeklyTrainingDays; }

    public boolean isPreviousInjury() { return previousInjury; }
    public void setPreviousInjury(boolean previousInjury) { this.previousInjury = previousInjury; }

    public String getObservations() { return observations; }
    public void setObservations(String observations) { this.observations = observations; }
}