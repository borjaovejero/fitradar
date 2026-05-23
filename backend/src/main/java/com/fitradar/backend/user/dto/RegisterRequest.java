package com.fitradar.backend.user.dto;

import com.fitradar.backend.user.model.User;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

public class RegisterRequest {

    // Obligatorios
    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(min = 3, max = 50, message = "El nombre de usuario debe tener entre 3 y 50 caracteres")
    private String username;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email no tiene un formato válido")
    @Size(max = 100)
    private String email;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 50)
    private String firstName;

    @NotBlank(message = "Los apellidos son obligatorios")
    @Size(max = 100)
    private String lastName;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 4, max = 100)
    private String password;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Past(message = "La fecha de nacimiento debe ser anterior a hoy")
    private LocalDate birthDate;

    @NotNull(message = "El sexo es obligatorio")
    private User.Sex sex;

    // Opcionales (perfil deportivo)
    private Double heightCm;
    private Double weightKg;
    private User.SportType sportType;
    private User.AthleteLevel athleteLevel;
    private Integer weeklyTrainingDays;
    private boolean previousInjury = false;
    private String observations;

    public RegisterRequest() {}

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

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