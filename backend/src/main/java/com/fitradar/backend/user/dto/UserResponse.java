package com.fitradar.backend.user.dto;

import com.fitradar.backend.user.model.User;
import java.time.LocalDate;

public class UserResponse {

    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private User.Sex sex;
    private boolean enabled;
    private Double heightCm;
    private Double weightKg;
    private User.SportType sportType;
    private User.AthleteLevel athleteLevel;
    private Integer weeklyTrainingDays;
    private boolean previousInjury;
    private String observations;

    public UserResponse() {}

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }

    public User.Sex getSex() { return sex; }
    public void setSex(User.Sex sex) { this.sex = sex; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public Double getHeightCm() { return heightCm; }
    public void setHeightCm(Double heightCm) { this.heightCm = heightCm; }

    public Double getWeightKg() { return weightKg; }
    public void setWeightKg(Double weightKg) { this.weightKg = weightKg; }

    public User.SportType getSportType() { return sportType; }
    public void setSportType(User.SportType sportType) { this.sportType = sportType; }

    public User.AthleteLevel getAthleteLevel() { return athleteLevel; }
    public void setAthleteLevel(User.AthleteLevel athleteLevel) { this.athleteLevel = athleteLevel; }

    public Integer getWeeklyTrainingDays() { return weeklyTrainingDays; }
    public void setWeeklyTrainingDays(Integer weeklyTrainingDays) { this.weeklyTrainingDays = weeklyTrainingDays; }

    public boolean isPreviousInjury() { return previousInjury; }
    public void setPreviousInjury(boolean previousInjury) { this.previousInjury = previousInjury; }

    public String getObservations() { return observations; }
    public void setObservations(String observations) { this.observations = observations; }
}