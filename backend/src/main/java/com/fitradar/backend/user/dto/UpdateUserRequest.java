package com.fitradar.backend.user.dto;

import com.fitradar.backend.user.model.User;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

public class UpdateUserRequest {

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email no tiene un formato válido")
    private String email;

    @NotBlank(message = "El nombre es obligatorio")
    private String firstName;

    @NotBlank(message = "Los apellidos son obligatorios")
    private String lastName;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    private LocalDate birthDate;

    @NotNull(message = "El sexo es obligatorio")
    private User.Sex sex;

    // Perfil deportivo (todos opcionales)
    private Double heightCm;
    private Double weightKg;
    private User.SportType sportType;
    private User.AthleteLevel athleteLevel;
    private Integer weeklyTrainingDays;
    private boolean previousInjury;
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

    public User.Sex getSex() { return sex; }
    public void setSex(User.Sex sex) { this.sex = sex; }

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