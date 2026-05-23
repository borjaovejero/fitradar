package com.fitradar.frontend.user.dto;

import java.time.LocalDate;

public class UpdateUserRequest {

    private String email;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private String sex;

    // Perfil deportivo
    private Double heightCm;
    private Double weightKg;
    private String sportType;
    private String athleteLevel;
    private Integer weeklyTrainingDays;
    private String observations;

    public UpdateUserRequest() {}

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }

    public String getSex() { return sex; }
    public void setSex(String sex) { this.sex = sex; }

    public Double getHeightCm() { return heightCm; }
    public void setHeightCm(Double heightCm) { this.heightCm = heightCm; }

    public Double getWeightKg() { return weightKg; }
    public void setWeightKg(Double weightKg) { this.weightKg = weightKg; }

    public String getSportType() { return sportType; }
    public void setSportType(String sportType) { this.sportType = sportType; }

    public String getAthleteLevel() { return athleteLevel; }
    public void setAthleteLevel(String athleteLevel) { this.athleteLevel = athleteLevel; }

    public Integer getWeeklyTrainingDays() { return weeklyTrainingDays; }
    public void setWeeklyTrainingDays(Integer weeklyTrainingDays) { this.weeklyTrainingDays = weeklyTrainingDays; }

    public String getObservations() { return observations; }
    public void setObservations(String observations) { this.observations = observations; }
}